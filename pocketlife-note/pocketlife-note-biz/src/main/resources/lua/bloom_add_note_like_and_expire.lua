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

local noteId = ARGV[1] -- 笔记ID
local expireSeconds = ARGV[2] -- 过期时间（秒）

add(noteId)

-- 设置过期时间
redis.call("EXPIRE", key, expireSeconds)
return 0