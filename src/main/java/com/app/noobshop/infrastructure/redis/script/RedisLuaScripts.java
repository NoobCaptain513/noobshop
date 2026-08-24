package com.app.noobshop.infrastructure.redis.script;

import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

/**
 * Redis Lua 脚本集合
 */
public class RedisLuaScripts {

    /**
     * 库存原子扣减脚本
     * KEYS[1]: 库存 key (Hash)
     * ARGV[1]: 扣减数量
     * 返回值：1-成功, 0-库存不足, -1-key不存在
     */
    public static final RedisScript<Long> DEDUCT_STOCK_SCRIPT = new DefaultRedisScript<>(
            "local stock = redis.call('HGET', KEYS[1], 'stock')\n" +
            "if not stock then\n" +
            "    return -1\n" +
            "end\n" +
            "local stockNum = tonumber(stock)\n" +
            "local deduct = tonumber(ARGV[1])\n" +
            "if stockNum >= deduct then\n" +
            "    redis.call('HINCRBY', KEYS[1], 'stock', -deduct)\n" +
            "    return 1\n" +
            "else\n" +
            "    return 0\n" +
            "end",
            Long.class
    );

    /**
     * 库存回滚脚本（增加库存）
     * KEYS[1]: 库存 key (Hash)
     * ARGV[1]: 回滚数量
     * 返回值：新的库存值
     */
    public static final RedisScript<Long> ROLLBACK_STOCK_SCRIPT = new DefaultRedisScript<>(
            "return redis.call('HINCRBY', KEYS[1], 'stock', ARGV[1])",
            Long.class
    );
}
