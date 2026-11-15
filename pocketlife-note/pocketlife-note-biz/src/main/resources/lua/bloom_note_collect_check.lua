-- LUA 脚本：笔记收藏布隆过滤器

local key = KEYS[1] -- 操作的 Redis Key
local keyType = redis.call('TYPE', key).ok
local usesSet = keyType == 'none' or keyType == 'set'

local function contains(value)
    if usesSet then
        return redis.call('SISMEMBER', key, value)
    end
    return redis.call('BF.EXISTS', key, value)
end

local function add(value)
    if usesSet then
        return redis.call('SADD', key, value)
    end
    return redis.call('BF.ADD', key, value)
end

local noteId = ARGV[1] -- 笔记ID

-- 使用 EXISTS 命令检查布隆过滤器是否存在
local exists = redis.call('EXISTS', key)
if exists == 0 then
    return -1
end

-- 校验该篇笔记是否被收藏过(1 表示已经收藏，0 表示未收藏)
local isCollected = contains(noteId)
if isCollected == 1 then
    return 1
end

-- 未被收藏，添加收藏数据
add(noteId)
return 0
