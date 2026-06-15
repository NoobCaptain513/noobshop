package com.app.noobshop.statemachine.order;

import com.app.noobshop.pojo.emums.OrderStatusChangeEvent;
import com.app.noobshop.pojo.emums.OrderStatusEnum;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class OrderStateMachineConfig extends StateMachineConfigurerAdapter<OrderStatusEnum, OrderStatusChangeEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<OrderStatusEnum, OrderStatusChangeEvent> states) throws Exception {
        states.withStates()
                .initial(OrderStatusEnum.PENDING_PAYMENT)
                .states(EnumSet.allOf(OrderStatusEnum.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatusEnum, OrderStatusChangeEvent> transitions)
            throws Exception {
        transitions
                .withExternal()
                .source(OrderStatusEnum.PENDING_PAYMENT)
                .target(OrderStatusEnum.PENDING_SHIPMENT)
                .event(OrderStatusChangeEvent.PAY_SUCCESS)
                .and()
                .withExternal()
                .source(OrderStatusEnum.PENDING_CONFIRM)
                .target(OrderStatusEnum.PENDING_SHIPMENT)
                .event(OrderStatusChangeEvent.CONFIRM_ORDER)
                .and()
                .withExternal()
                .source(OrderStatusEnum.PENDING_SHIPMENT)
                .target(OrderStatusEnum.PENDING_RECEIPT)
                .event(OrderStatusChangeEvent.SHIP)
                .and()
                .withExternal()
                .source(OrderStatusEnum.PENDING_RECEIPT)
                .target(OrderStatusEnum.COMPLETED)
                .event(OrderStatusChangeEvent.RECEIVE)
                .and()
                .withExternal()
                .source(OrderStatusEnum.PENDING_PAYMENT)
                .target(OrderStatusEnum.CANCELLED)
                .event(OrderStatusChangeEvent.CANCEL)
                .and()
                .withExternal()
                .source(OrderStatusEnum.PENDING_CONFIRM)
                .target(OrderStatusEnum.CANCELLED)
                .event(OrderStatusChangeEvent.CANCEL)
                .and()
                .withExternal()
                .source(OrderStatusEnum.PENDING_SHIPMENT)
                .target(OrderStatusEnum.AFTER_SALE)
                .event(OrderStatusChangeEvent.APPLY_AFTER_SALE)
                .and()
                .withExternal()
                .source(OrderStatusEnum.PENDING_RECEIPT)
                .target(OrderStatusEnum.AFTER_SALE)
                .event(OrderStatusChangeEvent.APPLY_AFTER_SALE)
                .and()
                .withExternal()
                .source(OrderStatusEnum.COMPLETED)
                .target(OrderStatusEnum.AFTER_SALE)
                .event(OrderStatusChangeEvent.APPLY_AFTER_SALE)
                .and()
                .withExternal()
                .source(OrderStatusEnum.COMPLETED)
                .target(OrderStatusEnum.EVALUATED)
                .event(OrderStatusChangeEvent.EVALUATE)
                .and()
                .withExternal()
                .source(OrderStatusEnum.EVALUATED)
                .target(OrderStatusEnum.REVIEWED)
                .event(OrderStatusChangeEvent.REVIEW);
    }
}
