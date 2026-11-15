USE pocketlife;

SET @pocketlife_feed_migration = IF(
    EXISTS(SELECT 1 FROM information_schema.columns
           WHERE table_schema = DATABASE() AND table_name = 't_note' AND column_name = 'channel_id'),
    'SELECT 1',
    'ALTER TABLE t_note ADD COLUMN channel_id BIGINT UNSIGNED NULL,
     ADD INDEX idx_note_feed (status, visible, id),
     ADD INDEX idx_note_channel_feed (channel_id, status, visible, id)'
);
PREPARE pocketlife_feed_stmt FROM @pocketlife_feed_migration;
EXECUTE pocketlife_feed_stmt;
DEALLOCATE PREPARE pocketlife_feed_stmt;
