USE pocketlife;

START TRANSACTION;

INSERT INTO t_channel (name, create_time, update_time, is_deleted)
SELECT wanted.name, NOW(), NOW(), 0
FROM (
    SELECT '游戏' AS name UNION ALL SELECT '旅行' UNION ALL SELECT '健身'
    UNION ALL SELECT '新闻' UNION ALL SELECT '搞笑' UNION ALL SELECT '影视'
    UNION ALL SELECT '美食' UNION ALL SELECT '科技' UNION ALL SELECT 'AI'
    UNION ALL SELECT '校园圈'
) AS wanted
WHERE NOT EXISTS (
    SELECT 1 FROM t_channel WHERE name = wanted.name AND is_deleted = 0
);

-- Retire the old tabs while retaining their IDs and any note associations.
UPDATE t_channel SET is_deleted = 1, update_time = NOW()
WHERE is_deleted = 0 AND name NOT IN
    ('游戏', '旅行', '健身', '新闻', '搞笑', '影视', '美食', '科技', 'AI', '校园圈');

COMMIT;
