package com.app.noobshop.service;

import com.app.noobshop.pojo.dto.OrderItemDTO;
import com.app.noobshop.pojo.entity.OrderItem;

import java.util.List;

/**
 * 商品库存服务
 * 负责下单时的库存预扣减（Redis 原子操作）与取消订单时的库存回滚，
 * 并通过 RocketMQ 异步将结果同步落库，保证 Redis 与 MySQL 最终一致。
 */
public interface ProductStockService {

    /**
     * 下单前预扣库存（Redis 原子扣减）。
     * 任意一个商品库存不足时，会自动回滚本次已经扣减成功的部分，并抛出 StockInsufficientException。
     *
     * @param orderNo    订单号，用于异步消息追溯
     * @param orderItems 订单明细（含 productId / specId / quantity）
     */
    void preDeductStock(String orderNo, List<OrderItemDTO> orderItems);

    /**
     * 订单取消/超时未支付时回滚库存（Redis 加回 + 异步同步落库）。
     *
     * @param orderNo    订单号
     * @param orderItems 订单明细（下单时落库的 OrderItem，包含 productId / specId / quantity）
     */
    void rollbackStock(String orderNo, List<OrderItem> orderItems);
}
