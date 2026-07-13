package com.app.noobshop.infrastructure.rocketmq.consumer.product;

import com.app.noobshop.infrastructure.rocketmq.constant.product.MqProductConstant;
import com.app.noobshop.mapper.ProductMapper;
import com.app.noobshop.mapper.ProductSpecMapper;
import com.app.noobshop.pojo.dto.StockChangeMqDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 消费库存变更消息（下单预扣 / 取消回滚），把 Redis 里已经生效的库存变化最终同步落库到 MySQL。
 * Redis 是并发控制的唯一裁决点，这里的 DB 更新只做“最终一致”落库，不做库存是否充足的二次校验。
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(topic = MqProductConstant.TOPIC_PRODUCT
        , selectorExpression = MqProductConstant.TAG_STOCK_CHANGE_SYNC
        , consumerGroup = MqProductConstant.CONSUMER_GROUP_STOCK_CHANGE_SYNC
        , maxReconsumeTimes = 3)
public class ProductStockSyncConsumer implements RocketMQListener<List<StockChangeMqDTO>> {

    private final ProductMapper productMapper;
    private final ProductSpecMapper productSpecMapper;

    @Override
    public void onMessage(List<StockChangeMqDTO> changeList) {
        if (Objects.isNull(changeList) || changeList.isEmpty()) {
            return;
        }
        for (StockChangeMqDTO change : changeList) {
            int rows;
            if (Objects.nonNull(change.getSpecId())) {
                rows = productSpecMapper.updateStockByDelta(change.getSpecId(), change.getDelta());
                // 规格库存之外，商品总库存做同步累加，用于列表页展示汇总库存
                productMapper.updateStockByDelta(change.getProductId(), change.getDelta());
            } else {
                rows = productMapper.updateStockByDelta(change.getProductId(), change.getDelta());
            }
            if (rows == 0) {
                // 理论上不应该发生（Redis 已经做过库存充足性校验），出现说明 DB 与 Redis 数据不一致，需要人工核对
                log.error("库存落库更新影响行数为0，疑似DB与Redis库存不一致，orderNo:{},productId:{},specId:{},delta:{}",
                        change.getOrderNo(), change.getProductId(), change.getSpecId(), change.getDelta());
            }
        }
    }
}
