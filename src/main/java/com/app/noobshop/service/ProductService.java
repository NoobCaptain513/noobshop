package com.app.noobshop.service;


import com.app.noobshop.common.result.CursorCommonEntity;
import com.app.noobshop.common.result.CursorCommonResult;
import com.app.noobshop.common.result.Result;
import com.app.noobshop.common.result.SimpleCursorCommonResult;
import com.app.noobshop.infrastructure.es.document.ProductDocument;
import com.app.noobshop.pojo.entity.Product;
import com.app.noobshop.pojo.vo.SimpleProductVO;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface ProductService extends IService<Product> {

    List<ProductDocument> getHotProduct(Integer limit);

    Result getProductDetail(String productId, String userId);

    CursorCommonResult searchProductList(CursorCommonEntity cursorCommonEntity ,String keyword);

    List<ProductDocument> getProductRelated(String productName, Integer limit);

    Result getProductSpecPrice(String productId, String specId);

    Result<List<SimpleProductVO>> getBriefProduct(String productIds);

    Result getCategoryProductList(@NotBlank String categoryId, String beginProductId, String sortType);

    SimpleCursorCommonResult getSimpleProductByScrollQuery(Long beginId , Integer querySize);

    Map<Long, Product> getProductDetailByProductIdSet(Set<Long> productIdSet);

    CursorCommonResult getCategorySimpleProduct(@Valid @NotNull CursorCommonEntity cursorCommonEntity ,Long categoryId , boolean isFirstCategoryId);
}

