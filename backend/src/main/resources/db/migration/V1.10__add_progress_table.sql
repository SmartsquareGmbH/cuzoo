CREATE TABLE opportunity_progress
(
    opportunity_state VARCHAR NOT NULL,
    progress_text     VARCHAR,
    date              TIMESTAMP,
    opportunity_id    BIGINT,
    FOREIGN KEY (opportunity_id) REFERENCES opportunity (id) ON DELETE CASCADE
);