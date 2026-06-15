package com.app.noobshop.service;

import com.app.noobshop.common.result.Result;
import com.app.noobshop.common.result.SimpleCursorCommonEntity;
import com.app.noobshop.common.result.SimpleCursorCommonResult;
import com.app.noobshop.pojo.entity.ProductCollection;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.constraints.NotBlank;

public interface CollectionService extends IService<ProductCollection> {
    Result addCollection(String productId);

    Result deleteCollection(@NotBlank String productIds);

    /**
     * 获取用户收藏商品
     */
    Result<SimpleCursorCommonResult> getCollectionList(SimpleCursorCommonEntity simpleCursorCommonEntity);
}
