package com.app.noobshop.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 库存变更同步消息（Redis 扣减/回滚成功后，异步通知 DB 落库）
 * delta 为负数表示扣减，为正数表示回滚（加回库存）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockChangeMqDTO implements Serializable {

    /**
     * 单次库存变更唯一标识，用于消费者幂等去重
     */
    private String changeId;

    /**
     * 关联订单号，便于排查问题时追溯
     */
    private String orderNo;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 规格ID，可为空（无规格商品直接扣减商品自身库存）
     */
    private Long specId;

    /**
     * 库存变化量：负数=扣减，正数=回滚加回
     */
    private Integer delta;
}
