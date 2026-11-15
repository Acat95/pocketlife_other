CREATE TABLE IF NOT EXISTS pocketlife.comment_content (
    note_id bigint,
    year_month text,
    content_id uuid,
    content text,
    PRIMARY KEY ((note_id, year_month), content_id)
);
