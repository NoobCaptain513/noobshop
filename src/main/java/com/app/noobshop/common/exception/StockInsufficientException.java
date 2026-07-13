package com.app.noobshop.common.exception;

/**
 * 库存不足异常
 */
public class StockInsufficientException extends BusinessException {

    private final Long productId;

    public StockInsufficientException(Long productId) {
        super("商品库存不足，商品ID: " + productId);
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }
}
