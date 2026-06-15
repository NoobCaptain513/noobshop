package com.app.noobshop.infrastructure.rocketmq.consumer.product;

import com.app.noobshop.infrastructure.es.document.ProductDocument;
import com.app.noobshop.infrastructure.es.service.ProductDocumentService;
import com.app.noobshop.infrastructure.rocketmq.constant.product.MqProductConstant;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RocketMQMessageListener(topic = MqProductConstant.TOPIC_PRODUCT
,selectorExpression = MqProductConstant.TAG_PRODUCT_DOCUMENT_SYNC
,consumerGroup = MqProductConstant.CONSUMER_GROUP_PRODUCT_DOCUMENT_SYNC
,maxReconsumeTimes = 3)
@RequiredArgsConstructor
public class SyncProductDocumentConsumer implements RocketMQListener<List<ProductDocument>> {

    private final ProductDocumentService productDocumentService;

    @Override
    public void onMessage(List<ProductDocument> productDocumentList) {
        if (Objects.isNull(productDocumentList) || productDocumentList.isEmpty()){
            return;
        }
        productDocumentService.batchSaveProductDocument(productDocumentList);
    }
}
