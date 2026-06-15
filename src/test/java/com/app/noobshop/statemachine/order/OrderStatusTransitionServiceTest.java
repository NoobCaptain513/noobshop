package com.app.noobshop.statemachine.order;

import com.app.noobshop.pojo.emums.OrderStatusEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {OrderStateMachineConfig.class, OrderStatusTransitionService.class})
class OrderStatusTransitionServiceTest {

    @Resource
    private OrderStatusTransitionService orderStatusTransitionService;

    @Test
    void shouldAllowPaySuccessFromPendingPayment() {
        assertTrue(orderStatusTransitionService.canTransition(
                OrderStatusEnum.PENDING_PAYMENT,
                OrderStatusEnum.PENDING_SHIPMENT));
    }

    @Test
    void shouldAllowReceiveOnlyAfterShipment() {
        assertTrue(orderStatusTransitionService.canTransition(
                OrderStatusEnum.PENDING_RECEIPT,
                OrderStatusEnum.COMPLETED));
        assertFalse(orderStatusTransitionService.canTransition(
                OrderStatusEnum.PENDING_PAYMENT,
                OrderStatusEnum.COMPLETED));
    }

    @Test
    void shouldRejectCancelCompletedOrder() {
        assertFalse(orderStatusTransitionService.canTransition(
                OrderStatusEnum.COMPLETED,
                OrderStatusEnum.CANCELLED));
    }
}
