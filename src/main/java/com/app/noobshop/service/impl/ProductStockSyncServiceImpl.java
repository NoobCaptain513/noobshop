package com.app.noobshop.service.impl;

import com.app.noobshop.mapper.MqConsumeRecordMapper;
import com.app.noobshop.mapper.ProductMapper;
import com.app.noobshop.mapper.ProductSpecMapper;
import com.app.noobshop.pojo.dto.StockChangeMqDTO;
import com.app.noobshop.pojo.entity.MqConsumeRecord;
import com.app.noobshop.service.ProductStockSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductStockSyncServiceImpl implements ProductStockSyncService {

    private final ProductMapper productMapper;
    private final ProductSpecMapper productSpecMapper;
    private final MqConsumeRecordMapper mqConsumeRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncToDatabase(List<StockChangeMqDTO> changeList) {
        for (StockChangeMqDTO change : changeList) {
            validate(change);
            MqConsumeRecord record = MqConsumeRecord.builder()
                    .changeId(change.getChangeId())
                    .orderNo(change.getOrderNo())
                    .productId(change.getProductId())
                    .specId(change.getSpecId())
                    .build();
            if (!tryCreateConsumeRecord(record)) {
                log.info("跳过已消费库存变更, changeId:{}", change.getChangeId());
                continue;
            }

            int rows;
            if (Objects.nonNull(change.getSpecId())) {
                rows = productSpecMapper.updateStockByDelta(change.getSpecId(), change.getDelta());
                if (rows == 0 || productMapper.updateStockByDelta(change.getProductId(), change.getDelta()) == 0) {
                    throw new IllegalStateException("规格或商品库存更新失败, changeId:" + change.getChangeId());
                }
            } else {
                rows = productMapper.updateStockByDelta(change.getProductId(), change.getDelta());
                if (rows == 0) {
                    throw new IllegalStateException("商品库存更新失败, changeId:" + change.getChangeId());
                }
            }
        }
    }

    private void validate(StockChangeMqDTO change) {
        if (Objects.isNull(change)
                || StringUtils.isBlank(change.getChangeId())
                || Objects.isNull(change.getProductId())
                || Objects.isNull(change.getDelta())
                || change.getDelta() == 0) {
            throw new IllegalArgumentException("库存变更消息缺少必要字段: " + change);
        }
        if (change.getChangeId().length() > 64) {
            throw new IllegalArgumentException("库存变更 changeId 长度超过64: " + change.getChangeId());
        }
    }

    private boolean tryCreateConsumeRecord(MqConsumeRecord record) {
        try {
            mqConsumeRecordMapper.insert(record);
            return true;
        } catch (DuplicateKeyException e) {
            return false;
        }
    }
}
