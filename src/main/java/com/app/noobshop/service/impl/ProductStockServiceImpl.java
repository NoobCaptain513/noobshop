package com.app.noobshop.service.impl;

import com.app.noobshop.common.exception.StockInsufficientException;
import com.app.noobshop.infrastructure.redis.connect.StringRedisConnector;
import com.app.noobshop.infrastructure.redis.generator.RedisKeyGenerator;
import com.app.noobshop.infrastructure.rocketmq.constant.failed.MqFailedMessageConstant;
import com.app.noobshop.infrastructure.rocketmq.constant.product.MqProductConstant;
import com.app.noobshop.mapper.ProductMapper;
import com.app.noobshop.mapper.ProductSpecMapper;
import com.app.noobshop.pojo.dto.OrderItemDTO;
import com.app.noobshop.pojo.dto.StockChangeMqDTO;
import com.app.noobshop.pojo.entity.MqConsumerFailedMsg;
import com.app.noobshop.pojo.entity.OrderItem;
import com.app.noobshop.pojo.entity.Product;
import com.app.noobshop.pojo.entity.ProductSpec;
import com.app.noobshop.service.MqConsumerFailedMsgService;
import com.app.noobshop.service.ProductStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.stereotype.Service;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductStockServiceImpl implements ProductStockService {

    private final ProductMapper productMapper;
    private final ProductSpecMapper productSpecMapper;
    private final RocketMQTemplate rocketMQTemplate;
    private final MqConsumerFailedMsgService mqConsumerFailedMsgService;

    /**
     * Redis 库存缓存兜底 TTL：DB 回源写入时顺带续期，避免长期占用内存
     */
    private static final long STOCK_CACHE_TTL_DAYS = 7;

    @Override
    public void preDeductStock(String orderNo, List<OrderItemDTO> orderItems) {
        // 记录本次已经在 Redis 扣减成功的项，任意一项失败时用于回滚
        List<StockChangeMqDTO> deductedItems = new ArrayList<>(orderItems.size());

        for (OrderItemDTO item : orderItems) {
            Long productId = Long.valueOf(item.getProductId());
            Long specId = Objects.nonNull(item.getSpecId()) && !item.getSpecId().isBlank()
                    ? Long.valueOf(item.getSpecId()) : null;
            int quantity = item.getQuantity();
            String stockKey = resolveStockKey(productId, specId);

            Long result = StringRedisConnector.deductStock(stockKey, quantity);

            // Redis 未命中缓存：从数据库回源加载后重试一次
            if (result == null || result == 0L) {
                loadStockToRedisFromDb(productId, specId, stockKey);
                result = StringRedisConnector.deductStock(stockKey, quantity);
            }

            if (result == null || result < 0) {
                log.warn("库存不足，回滚本次已扣减部分，orderNo:{},productId:{},specId:{}", orderNo, productId, specId);
                rollbackRedisOnly(deductedItems);
                throw new StockInsufficientException(productId);
            }

            deductedItems.add(StockChangeMqDTO.builder()
                    .orderNo(orderNo).productId(productId).specId(specId)
                    .delta(-quantity).build());
        }

        // Redis 全部扣减成功后，异步同步落库（最终一致）
        asyncSyncStockToDb(deductedItems, 0);
    }

    @Override
    public void rollbackStock(String orderNo, List<OrderItem> orderItems) {
        List<StockChangeMqDTO> rollbackItems = new ArrayList<>(orderItems.size());
        for (OrderItem item : orderItems) {
            Long productId = item.getProductId();
            Long specId = item.getSpecId();
            String stockKey = resolveStockKey(productId, specId);

            // 回滚操作是加库存，天然幂等安全，直接 INCR，不需要 Lua 原子校验
            StringRedisConnector.incrementStock(stockKey, item.getQuantity());

            rollbackItems.add(StockChangeMqDTO.builder()
                    .orderNo(orderNo).productId(productId).specId(specId)
                    .delta(item.getQuantity()).build());
        }
        asyncSyncStockToDb(rollbackItems, 0);
    }

    /**
     * 部分扣减失败时，把本次已经扣减成功的 Redis 库存加回去（仅 Redis，未落库，无需再发 MQ）
     */
    private void rollbackRedisOnly(List<StockChangeMqDTO> deductedItems) {
        for (StockChangeMqDTO item : deductedItems) {
            String stockKey = resolveStockKey(item.getProductId(), item.getSpecId());
            StringRedisConnector.incrementStock(stockKey, -item.getDelta());
        }
    }

    private String resolveStockKey(Long productId, Long specId) {
        return Objects.nonNull(specId)
                ? RedisKeyGenerator.productSpecStockKey(specId)
                : RedisKeyGenerator.productStockKey(productId);
    }

    /**
     * Redis 未命中库存缓存时，从数据库回源加载当前库存到 Redis（SETNX 防止并发重复加载覆盖）
     */
    private void loadStockToRedisFromDb(Long productId, Long specId, String stockKey) {
        Integer dbStock;
        if (Objects.nonNull(specId)) {
            ProductSpec spec = productSpecMapper.selectById(specId);
            dbStock = Objects.nonNull(spec) ? spec.getStock() : 0;
        } else {
            Product product = productMapper.selectById(productId);
            dbStock = Objects.nonNull(product) && Objects.nonNull(product.getStock())
                    ? product.getStock().intValue() : 0;
        }
        StringRedisConnector.setStockIfAbsent(stockKey, dbStock, STOCK_CACHE_TTL_DAYS, TimeUnit.DAYS);
    }

    /**
     * 异步通知 MQ 把库存变更同步落库，失败重试，重试耗尽则记录到 mq_consumer_failed_msg 表兜底
     */
    private void asyncSyncStockToDb(List<StockChangeMqDTO> changeList, int retryCount) {
        String destination = MqProductConstant.TOPIC_PRODUCT + ":" + MqProductConstant.TAG_STOCK_CHANGE_SYNC;
        int maxRetry = 2;
        rocketMQTemplate.asyncSend(destination, changeList, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
            }

            @Override
            public void onException(Throwable throwable) {
                if (retryCount < maxRetry) {
                    asyncSyncStockToDb(changeList, retryCount + 1);
                } else {
                    MqConsumerFailedMsg failedMsg = MqConsumerFailedMsg.builder()
                            .topic(MqProductConstant.TOPIC_PRODUCT)
                            .tag(MqProductConstant.TAG_STOCK_CHANGE_SYNC)
                            .errorMsg(MqFailedMessageConstant.MQ_FAILED_ASYNC_SEND + ": " + throwable.getMessage())
                            .body("库存变更同步落库失败: " + changeList)
                            .retryCount(retryCount)
                            .build();
                    mqConsumerFailedMsgService.save(failedMsg);
                }
            }
        });
    }
}
