ALTER TABLE mate ADD CONSTRAINT unique_meeting_and_member_and_deleted_at UNIQUE (meeting_id, member_id, deleted_at);
