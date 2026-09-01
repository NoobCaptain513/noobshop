package com.app.noobshop.infrastructure.rocketmq.consumer.product;

import com.app.noobshop.infrastructure.rocketmq.constant.product.MqProductConstant;
import com.app.noobshop.pojo.dto.StockChangeMqDTO;
import com.app.noobshop.service.ProductStockSyncService;
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

    private final ProductStockSyncService productStockSyncService;

    @Override
    public void onMessage(List<StockChangeMqDTO> changeList) {
        if (Objects.isNull(changeList) || changeList.isEmpty()) {
            return;
        }
        productStockSyncService.syncToDatabase(changeList);
    }
}
