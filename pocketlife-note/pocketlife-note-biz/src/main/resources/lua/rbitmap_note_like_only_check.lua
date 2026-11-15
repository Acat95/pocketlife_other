-- LUA 脚本：点赞 Roaring Bitmap

local key = KEYS[1] -- 操作的 Redis Key
local keyType = redis.call('TYPE', key).ok
local usesSet = keyType == 'none' or keyType == 'set'

local function contains(value)
    if usesSet then
        return redis.call('SISMEMBER', key, value)
    end
    return redis.call('R.GETBIT', key, value)
end

local noteId = ARGV[1] -- 笔记ID

-- 使用 EXISTS 命令检查 Roaring Bitmap 是否存在
local exists = redis.call('EXISTS', key)
if exists == 0 then
    return -1
end

-- 校验该篇笔记是否被点赞过(1 表示已经点赞，0 表示未点赞)
return contains(noteId)





