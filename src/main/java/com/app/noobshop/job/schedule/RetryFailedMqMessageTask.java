package com.app.noobshop.job.schedule;

import com.app.noobshop.common.util.JacksonUtils;
import com.app.noobshop.infrastructure.es.document.ProductDocument;
import com.app.noobshop.infrastructure.rocketmq.constant.product.MqProductConstant;
import com.app.noobshop.pojo.dto.StockChangeMqDTO;
import com.app.noobshop.pojo.entity.MqConsumerFailedMsg;
import com.app.noobshop.service.MqConsumerFailedMsgService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 重新投递生产端发送失败的 MQ 消息。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RetryFailedMqMessageTask {

    private static final int PENDING = 0;
    private static final int PROCESSED = 1;
    private static final int ABANDONED = 2;
    private static final int MAX_RETRY_COUNT = 5;
    private static final int BATCH_SIZE = 100;

    private final MqConsumerFailedMsgService failedMsgService;
    private final RocketMQTemplate rocketMQTemplate;
    private final RedissonClient redissonClient;

    @Scheduled(fixedDelay = 60_000L, initialDelay = 60_000L)
    public void retry() {
        RLock lock = redissonClient.getLock("lock:mq:failed-message-retry");
        boolean locked = false;
        try {
            locked = lock.tryLock(0, TimeUnit.SECONDS);
            if (!locked) {
                return;
            }
            LocalDateTime now = LocalDateTime.now();
            List<MqConsumerFailedMsg> messages = failedMsgService.list(
                    Wrappers.<MqConsumerFailedMsg>lambdaQuery()
                            .eq(MqConsumerFailedMsg::getStatus, PENDING)
                            .and(wrapper -> wrapper.isNull(MqConsumerFailedMsg::getNextRetryTime)
                                    .or().le(MqConsumerFailedMsg::getNextRetryTime, now))
                            .orderByAsc(MqConsumerFailedMsg::getId)
                            .last("LIMIT " + BATCH_SIZE));
            messages.forEach(this::retryOne);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("失败 MQ 消息补偿任务执行异常", e);
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private void retryOne(MqConsumerFailedMsg failedMsg) {
        int retryCount = failedMsg.getRetryCount() == null ? 0 : failedMsg.getRetryCount();
        try {
            String destination = failedMsg.getTopic() + ":" + failedMsg.getTag();
            SendResult sendResult = rocketMQTemplate.syncSend(
                    destination, deserializePayload(failedMsg), 5_000L);
            if (sendResult == null || sendResult.getSendStatus() != SendStatus.SEND_OK) {
                throw new IllegalStateException("RocketMQ补偿发送状态异常: "
                        + (sendResult == null ? "null" : sendResult.getSendStatus()));
            }
            updateState(failedMsg, PROCESSED, retryCount, null, null);
        } catch (Exception e) {
            int nextRetryCount = retryCount + 1;
            boolean abandoned = nextRetryCount >= MAX_RETRY_COUNT;
            LocalDateTime nextRetryTime = abandoned
                    ? null
                    : LocalDateTime.now().plusSeconds(backoffSeconds(nextRetryCount));
            updateState(failedMsg, abandoned ? ABANDONED : PENDING,
                    nextRetryCount, nextRetryTime, e.getMessage());
            if (abandoned) {
                log.error("失败 MQ 消息重试耗尽, id:{}, topic:{}, tag:{}",
                        failedMsg.getId(), failedMsg.getTopic(), failedMsg.getTag(), e);
            }
        }
    }

    private void updateState(MqConsumerFailedMsg failedMsg, int status, int retryCount,
                             LocalDateTime nextRetryTime, String errorMsg) {
        boolean updated = failedMsgService.lambdaUpdate()
                .eq(MqConsumerFailedMsg::getId, failedMsg.getId())
                .eq(MqConsumerFailedMsg::getStatus, PENDING)
                .set(MqConsumerFailedMsg::getStatus, status)
                .set(MqConsumerFailedMsg::getRetryCount, retryCount)
                .set(MqConsumerFailedMsg::getNextRetryTime, nextRetryTime)
                .set(errorMsg != null, MqConsumerFailedMsg::getErrorMsg, errorMsg)
                .update();
        if (!updated) {
            log.warn("失败 MQ 消息状态更新未生效, id:{}", failedMsg.getId());
        }
    }

    private long backoffSeconds(int retryCount) {
        return Math.min(60L * (1L << Math.max(0, retryCount - 1)), 1_800L);
    }

    private Object deserializePayload(MqConsumerFailedMsg failedMsg) {
        if (MqProductConstant.TOPIC_PRODUCT.equals(failedMsg.getTopic())
                && MqProductConstant.TAG_STOCK_CHANGE_SYNC.equals(failedMsg.getTag())) {
            return JacksonUtils.toList(failedMsg.getBody(), StockChangeMqDTO.class);
        }
        if (MqProductConstant.TOPIC_PRODUCT.equals(failedMsg.getTopic())
                && MqProductConstant.TAG_PRODUCT_DOCUMENT_SYNC.equals(failedMsg.getTag())) {
            return JacksonUtils.toList(failedMsg.getBody(), ProductDocument.class);
        }
        throw new IllegalArgumentException("不支持补偿的 MQ 消息类型: "
                + failedMsg.getTopic() + ":" + failedMsg.getTag());
    }
}
