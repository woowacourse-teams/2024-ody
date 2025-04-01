create table if not exists meeting_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    mate_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    show_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_meeting_log_mate
    FOREIGN KEY (mate_id) REFERENCES mate(id)
);
