package com.app.noobshop.service;

import com.app.noobshop.pojo.dto.StockChangeMqDTO;

import java.util.List;

public interface ProductStockSyncService {

    void syncToDatabase(List<StockChangeMqDTO> changeList);
}
