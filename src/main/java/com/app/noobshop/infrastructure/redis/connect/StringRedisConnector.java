package com.app.noobshop.infrastructure.redis.connect;


import lombok.Setter;
import org.springframework.data.redis.core.*;

import java.util.concurrent.TimeUnit;

public class StringRedisConnector {

    // 由配置类注入 StringRedisTemplate（处理纯字符串）
    @Setter
    private static StringRedisTemplate stringRedisTemplate;

    // ===================== 纯字符串场景 - 各种数据结构操作 =====================
    public static ValueOperations<String, String> opsForValue() {
        return stringRedisTemplate.opsForValue();
    }

    public static HashOperations<String, String, String> opsForHash() {
        return stringRedisTemplate.opsForHash();
    }

    public static ListOperations<String, String> opsForList() {
        return stringRedisTemplate.opsForList();
    }

    public static SetOperations<String, String> opsForSet() {
        return stringRedisTemplate.opsForSet();
    }

    public static ZSetOperations<String, String> opsForZSet() {
        return stringRedisTemplate.opsForZSet();
    }

    // ===================== 通用方法 =====================
    public static Boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }

    public static Boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    public static Boolean expire(String key, long timeout, java.util.concurrent.TimeUnit unit) {
        return stringRedisTemplate.expire(key, timeout, unit);
    }

    public static Long deductStock(String stockKey, int quantity) {
        return stringRedisTemplate.opsForHash().increment(stockKey, "stock", -quantity);
    }

    public static void incrementStock(String stockKey, Integer quantity) {
        stringRedisTemplate.opsForHash().increment(stockKey, "stock", quantity);
    }

    public static boolean setStockIfAbsent(String stockKey, Integer dbStock, long stockCacheTtlDays, TimeUnit timeUnit) {
        // 检查 key 是否存在且类型是否为 Hash
        Boolean exists = stringRedisTemplate.hasKey(stockKey);
        if (Boolean.TRUE.equals(exists)) {
            // 如果 key 已存在但不是 Hash 类型，先删除
            try {
                stringRedisTemplate.opsForHash().get(stockKey, "stock");
            } catch (Exception e) {
                // 类型不匹配，删除旧 key
                stringRedisTemplate.delete(stockKey);
            }
        }
        
        Boolean result = stringRedisTemplate.opsForHash().putIfAbsent(stockKey, "stock", String.valueOf(dbStock));
        if (Boolean.TRUE.equals(result)) {
            stringRedisTemplate.expire(stockKey, stockCacheTtlDays, timeUnit);
            return true;
        }
        return false;
    }
}
