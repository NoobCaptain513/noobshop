package com.app.noobshop.service.impl;

import com.app.noobshop.common.result.Result;
import com.app.noobshop.infrastructure.redis.connect.RedisConnector;
import com.app.noobshop.infrastructure.redis.generator.RedisKeyGenerator;
import com.app.noobshop.mapper.ProductImageMapper;
import com.app.noobshop.pojo.entity.ProductImage;
import com.app.noobshop.service.ProductImageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductImageServiceImpl extends ServiceImpl<ProductImageMapper, ProductImage>
        implements ProductImageService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<List<ProductImage>> saveProductImages(Long productId, List<String> imageUrls) {
        List<ProductImage> productImages = buildProductImages(productId, imageUrls);
        if (!productImages.isEmpty()) {
            saveBatch(productImages);
            RedisConnector.delete(RedisKeyGenerator.productDetail(productId));
        }
        return Result.success(productImages);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<List<ProductImage>> replaceProductImages(Long productId, List<String> imageUrls) {
        if (productId == null) {
            throw new IllegalArgumentException("productId can not be null");
        }
        lambdaUpdate().eq(ProductImage::getProductId, productId).remove();
        List<ProductImage> productImages = buildProductImages(productId, imageUrls);
        if (!productImages.isEmpty()) {
            saveBatch(productImages);
        }
        RedisConnector.delete(RedisKeyGenerator.productDetail(productId));
        return Result.success(productImages);
    }

    private List<ProductImage> buildProductImages(Long productId, List<String> imageUrls) {
        if (productId == null) {
            throw new IllegalArgumentException("productId can not be null");
        }
        if (imageUrls == null || imageUrls.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<ProductImage> productImages = new ArrayList<>(imageUrls.size());
        for (int i = 0; i < imageUrls.size(); i++) {
            String imageUrl = imageUrls.get(i);
            if (imageUrl == null || imageUrl.isBlank()) {
                continue;
            }
            ProductImage productImage = new ProductImage();
            productImage.setProductId(productId);
            productImage.setImageUrl(imageUrl);
            productImage.setSort(i + 1);
            productImages.add(productImage);
        }
        return productImages;
    }
}
