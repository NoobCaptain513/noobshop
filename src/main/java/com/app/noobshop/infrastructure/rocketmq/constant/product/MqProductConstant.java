package com.app.noobshop.infrastructure.rocketmq.constant.product;

public class MqProductConstant {

    //=========Topic =========
    public static final String TOPIC_PRODUCT = "product-topic";


    //=========Tags================
    public static final String TAG_PRODUCT_DOCUMENT_SYNC = "product-document-sync-tag";



    //==========Consumer  Group=================
    public static final String CONSUMER_GROUP_PRODUCT_DOCUMENT_SYNC = "product-document-sync-consumer-group";
    public static final String CONSUMER_GROUP_STOCK_CHANGE_SYNC = "stock-change-sync-tag";
    public static final String TAG_STOCK_CHANGE_SYNC = "stock-change-sync-consumer-group";
}
