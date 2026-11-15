USE pocketlife;

CREATE TABLE IF NOT EXISTS t_note_tag (
    note_id BIGINT UNSIGNED NOT NULL,
    position SMALLINT UNSIGNED NOT NULL,
    name VARCHAR(128) NOT NULL,
    PRIMARY KEY (note_id, position)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
