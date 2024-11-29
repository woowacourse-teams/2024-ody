ALTER TABLE notification DROP CONSTRAINT notification_chk_1;
ALTER TABLE notification ADD CONSTRAINT notification_chk_1 CHECK (`type` IN ('DEPARTURE_REMINDER', 'ENTRY', 'NUDGE', 'MEMBER_DELETION', 'ETA_NOTICE'));
