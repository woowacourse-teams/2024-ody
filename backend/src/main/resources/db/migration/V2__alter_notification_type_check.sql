ALTER TABLE notification ADD CONSTRAINT notification_chk_1 CHECK (notification.type IN ('DEPARTURE_REMINDER', 'ENTRY', 'NUDGE', 'MEMBER_DELETION', 'ETA_NOTICE'));
