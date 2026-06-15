package com.app.noobshop.service;

import com.app.noobshop.common.result.Result;
import com.app.noobshop.pojo.entity.ProductSearchKeyword;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface ProductSearchKeywordService extends IService<ProductSearchKeyword> {
    Result getProductSearchKeywordListUser();

    Result getProductSearchKeywordListAdmin();

    Result updateProductSearchListAdmin(@NotNull List<ProductSearchKeyword> productSearchKeywordList);
}

