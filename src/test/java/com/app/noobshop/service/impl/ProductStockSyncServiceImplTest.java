package com.app.noobshop.service.impl;

import com.app.noobshop.mapper.MqConsumeRecordMapper;
import com.app.noobshop.mapper.ProductMapper;
import com.app.noobshop.mapper.ProductSpecMapper;
import com.app.noobshop.pojo.dto.StockChangeMqDTO;
import com.app.noobshop.pojo.entity.MqConsumeRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductStockSyncServiceImplTest {

    @Mock
    private ProductMapper productMapper;
    @Mock
    private ProductSpecMapper productSpecMapper;
    @Mock
    private MqConsumeRecordMapper consumeRecordMapper;

    private ProductStockSyncServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ProductStockSyncServiceImpl(productMapper, productSpecMapper, consumeRecordMapper);
    }

    @Test
    void shouldSkipDuplicateStockChange() {
        StockChangeMqDTO change = change("change-1", null, -2);
        when(consumeRecordMapper.insert(any(MqConsumeRecord.class)))
                .thenThrow(new DuplicateKeyException("duplicate changeId"));

        service.syncToDatabase(List.of(change));

        verify(productMapper, never()).updateStockByDelta(any(), any());
        verify(productSpecMapper, never()).updateStockByDelta(any(), any());
    }

    @Test
    void shouldUpdateSpecAndProductStockOnce() {
        StockChangeMqDTO change = change("change-2", 20L, -2);
        when(consumeRecordMapper.insert(any(MqConsumeRecord.class))).thenReturn(1);
        when(productSpecMapper.updateStockByDelta(20L, -2)).thenReturn(1);
        when(productMapper.updateStockByDelta(10L, -2)).thenReturn(1);

        service.syncToDatabase(List.of(change));

        verify(productSpecMapper).updateStockByDelta(20L, -2);
        verify(productMapper).updateStockByDelta(10L, -2);
    }

    @Test
    void shouldFailWhenDatabaseStockRowDoesNotExist() {
        StockChangeMqDTO change = change("change-3", null, -2);
        when(consumeRecordMapper.insert(any(MqConsumeRecord.class))).thenReturn(1);
        when(productMapper.updateStockByDelta(10L, -2)).thenReturn(0);

        assertThrows(IllegalStateException.class, () -> service.syncToDatabase(List.of(change)));
    }

    @Test
    void shouldPropagateNonDuplicateInsertFailure() {
        StockChangeMqDTO change = change("change-4", null, -2);
        when(consumeRecordMapper.insert(any(MqConsumeRecord.class)))
                .thenThrow(new IllegalStateException("database unavailable"));

        assertThrows(IllegalStateException.class, () -> service.syncToDatabase(List.of(change)));
        verify(productMapper, never()).updateStockByDelta(any(), any());
    }

    private StockChangeMqDTO change(String changeId, Long specId, int delta) {
        return StockChangeMqDTO.builder()
                .changeId(changeId)
                .orderNo("order-1")
                .productId(10L)
                .specId(specId)
                .delta(delta)
                .build();
    }
}
