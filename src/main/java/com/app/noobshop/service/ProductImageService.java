package com.app.noobshop.service;

import com.app.noobshop.common.result.Result;
import com.app.noobshop.pojo.entity.ProductImage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ProductImageService extends IService<ProductImage> {

    Result<List<ProductImage>> saveProductImages(Long productId, List<String> imageUrls);

    Result<List<ProductImage>> replaceProductImages(Long productId, List<String> imageUrls);
}
