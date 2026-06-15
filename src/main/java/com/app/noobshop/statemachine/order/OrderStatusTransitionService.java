package com.app.noobshop.statemachine.order;

import com.app.noobshop.pojo.emums.OrderStatusChangeEvent;
import com.app.noobshop.pojo.emums.OrderStatusEnum;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@Service
public class OrderStatusTransitionService {

    private final StateMachineFactory<OrderStatusEnum, OrderStatusChangeEvent> stateMachineFactory;

    public OrderStatusTransitionService(StateMachineFactory<OrderStatusEnum, OrderStatusChangeEvent> stateMachineFactory) {
        this.stateMachineFactory = stateMachineFactory;
    }

    public boolean canTransition(OrderStatusEnum sourceStatus, OrderStatusEnum targetStatus) {
        if (Objects.isNull(sourceStatus) || Objects.isNull(targetStatus)) {
            return false;
        }
        for (OrderStatusChangeEvent event : OrderStatusChangeEvent.values()) {
            if (Objects.equals(event.getTargetStatus(), targetStatus) && canTransition(sourceStatus, event)) {
                return true;
            }
        }
        return false;
    }

    public boolean canTransition(OrderStatusEnum sourceStatus, OrderStatusChangeEvent event) {
        if (Objects.isNull(sourceStatus) || Objects.isNull(event)) {
            return false;
        }
        StateMachine<OrderStatusEnum, OrderStatusChangeEvent> stateMachine =
                stateMachineFactory.getStateMachine(UUID.randomUUID().toString());
        try {
            resetStateMachine(stateMachine, sourceStatus);
            Boolean accepted = stateMachine
                    .sendEvent(Mono.just(MessageBuilder.withPayload(event).build()))
                    .any(result -> result.getResultType() == StateMachineEventResult.ResultType.ACCEPTED)
                    .block();
            return Boolean.TRUE.equals(accepted)
                    && Objects.equals(stateMachine.getState().getId(), event.getTargetStatus());
        } finally {
            stateMachine.stopReactively().block();
        }
    }

    private void resetStateMachine(StateMachine<OrderStatusEnum, OrderStatusChangeEvent> stateMachine,
                                   OrderStatusEnum sourceStatus) {
        stateMachine.stopReactively().block();
        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(access -> access.resetStateMachineReactively(
                        new DefaultStateMachineContext<>(sourceStatus, null, null, null)).block());
        stateMachine.startReactively().block();
    }
}
