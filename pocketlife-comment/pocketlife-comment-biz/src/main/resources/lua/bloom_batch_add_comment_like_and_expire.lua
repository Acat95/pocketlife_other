-- 操作的 Key
local key = KEYS[1]
local keyType = redis.call('TYPE', key).ok
local usesSet = keyType == 'none' or keyType == 'set'

local function add(value)
    if usesSet then
        return redis.call('SADD', key, value)
    end
    return redis.call('BF.ADD', key, value)
end


for i = 1, #ARGV - 1 do
    add(ARGV[i])
end

---- 最后一个参数为过期时间
local expireTime = ARGV[#ARGV]
-- 设置过期时间
redis.call("EXPIRE", key, expireTime)
return 0