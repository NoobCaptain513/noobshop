package com.app.noobshop.pojo.emums;

import java.util.Objects;

public enum OrderStatusChangeEvent {
    PAY_SUCCESS(OrderStatusEnum.PENDING_SHIPMENT),
    CONFIRM_ORDER(OrderStatusEnum.PENDING_SHIPMENT),
    SHIP(OrderStatusEnum.PENDING_RECEIPT),
    RECEIVE(OrderStatusEnum.COMPLETED),
    CANCEL(OrderStatusEnum.CANCELLED),
    APPLY_AFTER_SALE(OrderStatusEnum.AFTER_SALE),
    EVALUATE(OrderStatusEnum.EVALUATED),
    REVIEW(OrderStatusEnum.REVIEWED);

    private final OrderStatusEnum targetStatus;

    OrderStatusChangeEvent(OrderStatusEnum targetStatus) {
        this.targetStatus = targetStatus;
    }

    public OrderStatusEnum getTargetStatus() {
        return targetStatus;
    }

    public static OrderStatusChangeEvent getByTargetStatus(OrderStatusEnum targetStatus) {
        for (OrderStatusChangeEvent event : values()) {
            if (Objects.equals(event.targetStatus, targetStatus)) {
                return event;
            }
        }
        return null;
    }
}
