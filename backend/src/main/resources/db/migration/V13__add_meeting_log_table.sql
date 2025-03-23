create table if not exists meeting_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    mate_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_meeting_log_mate
    FOREIGN KEY (mate_id) REFERENCES mate(id)
);
