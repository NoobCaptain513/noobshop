package com.app.noobshop.job.init;

import com.app.noobshop.infrastructure.redis.connect.StringRedisConnector;
import com.app.noobshop.infrastructure.redis.generator.RedisKeyGenerator;
import com.app.noobshop.mapper.ProductMapper;
import com.app.noobshop.mapper.ProductSpecMapper;
import com.app.noobshop.pojo.entity.Product;
import com.app.noobshop.pojo.entity.ProductSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 应用启动时，把商品/规格库存预热到 Redis（用于下单时的原子扣减）。
 * 使用 setIfAbsent，只在 Redis 缺失该 key 时才写入，避免覆盖重启前已经在用的、更新的库存值
 * （Redis 配置了 AOF/RDB 持久化的情况下，重启后缓存通常还在）。
 */
@Slf4j
@Component
@Order(10)
@RequiredArgsConstructor
public class ProductStockRedisCacheInitRunner implements ApplicationRunner {

    private final ProductMapper productMapper;
    private final ProductSpecMapper productSpecMapper;

    private static final long STOCK_CACHE_TTL_DAYS = 7;

    @Override
    public void run(ApplicationArguments args) {
        log.info("开始预热商品库存缓存到 Redis...");

        List<Product> productList = productMapper.selectList(null);
        int productCount = 0;
        for (Product product : productList) {
            if (product.getStock() == null) {
                continue;
            }
            String key = RedisKeyGenerator.productStockKey(product.getId());
            boolean written = StringRedisConnector.setStockIfAbsent(key, product.getStock().intValue(),
                    STOCK_CACHE_TTL_DAYS, TimeUnit.DAYS);
            if (written) {
                productCount++;
            }
        }

        List<ProductSpec> specList = productSpecMapper.selectList(null);
        int specCount = 0;
        for (ProductSpec spec : specList) {
            if (spec.getStock() == null) {
                continue;
            }
            String key = RedisKeyGenerator.productSpecStockKey(spec.getId());
            boolean written = StringRedisConnector.setStockIfAbsent(key, spec.getStock(),
                    STOCK_CACHE_TTL_DAYS, TimeUnit.DAYS);
            if (written) {
                specCount++;
            }
        }

        log.info("商品库存缓存预热完成，商品新写入{}个，规格新写入{}个（已存在的 key 未覆盖）", productCount, specCount);
    }
}
