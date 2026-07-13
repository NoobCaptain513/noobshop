-- 库存原子扣减脚本
-- KEYS[1] = 库存 Redis Key（product:stock:{id} 或 productSpec:stock:{id}）
-- ARGV[1] = 扣减数量
--
-- 返回值:
--   1  = 扣减成功
--   0  = key 不存在（未加载到缓存，需要调用方兜底从 DB 加载后重试）
--  -1  = 库存不足

local stockKey = KEYS[1]
local deductCount = tonumber(ARGV[1])

if redis.call('EXISTS', stockKey) == 0 then
    return 0
end

local stock = tonumber(redis.call('GET', stockKey))
if stock < deductCount then
    return -1
end

redis.call('DECRBY', stockKey, deductCount)
return 1
