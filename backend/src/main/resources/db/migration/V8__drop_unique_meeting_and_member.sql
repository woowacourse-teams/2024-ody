ALTER TABLE mate DROP CONSTRAINT fk_meeting_id;
ALTER TABLE mate DROP CONSTRAINT fk_member_id;

ALTER TABLE mate DROP CONSTRAINT unique_meeting_and_member;

ALTER TABLE mate ADD CONSTRAINT fk_meeting_id foreign key (meeting_id) references meeting (id);
ALTER TABLE mate ADD CONSTRAINT fk_member_id foreign key (member_id) references member (id);
