USE pocketlife;

CREATE TABLE IF NOT EXISTS t_following (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    following_user_id BIGINT UNSIGNED NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_following (user_id, following_user_id),
    KEY idx_following_time (user_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_fans (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    fans_user_id BIGINT UNSIGNED NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_fans (user_id, fans_user_id),
    KEY idx_fans_time (user_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_note_like (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    note_id BIGINT UNSIGNED NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status TINYINT NOT NULL DEFAULT 1,
    UNIQUE KEY uq_note_like (user_id, note_id),
    KEY idx_note_like_time (user_id, status, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_note_collection (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    note_id BIGINT UNSIGNED NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status TINYINT NOT NULL DEFAULT 1,
    UNIQUE KEY uq_note_collection (user_id, note_id),
    KEY idx_note_collection_time (user_id, status, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_user_count (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    fans_total BIGINT NOT NULL DEFAULT 0,
    following_total BIGINT NOT NULL DEFAULT 0,
    note_total BIGINT NOT NULL DEFAULT 0,
    like_total BIGINT NOT NULL DEFAULT 0,
    collect_total BIGINT NOT NULL DEFAULT 0,
    UNIQUE KEY uq_user_count (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_note_count (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    note_id BIGINT UNSIGNED NOT NULL,
    like_total BIGINT NOT NULL DEFAULT 0,
    collect_total BIGINT NOT NULL DEFAULT 0,
    comment_total BIGINT NOT NULL DEFAULT 0,
    UNIQUE KEY uq_note_count (note_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_comment (
    id BIGINT UNSIGNED NOT NULL PRIMARY KEY,
    note_id BIGINT UNSIGNED NOT NULL,
    user_id BIGINT UNSIGNED NOT NULL,
    content_uuid VARCHAR(36) NOT NULL DEFAULT '',
    is_content_empty BIT(1) NOT NULL DEFAULT b'1',
    image_url VARCHAR(512) NOT NULL DEFAULT '',
    level TINYINT NOT NULL,
    reply_total BIGINT NOT NULL DEFAULT 0,
    like_total BIGINT NOT NULL DEFAULT 0,
    parent_id BIGINT UNSIGNED NOT NULL DEFAULT 0,
    reply_comment_id BIGINT UNSIGNED NOT NULL DEFAULT 0,
    reply_user_id BIGINT UNSIGNED NOT NULL DEFAULT 0,
    is_top TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    child_comment_total BIGINT NOT NULL DEFAULT 0,
    first_reply_comment_id BIGINT UNSIGNED NOT NULL DEFAULT 0,
    heat DOUBLE NOT NULL DEFAULT 0,
    KEY idx_comment_note_heat (note_id, level, heat),
    KEY idx_comment_parent_time (parent_id, level, create_time),
    KEY idx_comment_reply (reply_comment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_comment_like (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    comment_id BIGINT UNSIGNED NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_comment_like (user_id, comment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO leaf.leaf_alloc (biz_tag, max_id, step, description)
SELECT 'leaf-segment-comment-id', COALESCE(MAX(id), 0) + 1, 1000, 'pocketlife comment ID'
FROM t_comment;
