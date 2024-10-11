/*! ALTER TABLE member MODIFY COLUMN deleted_at timestamp(6); */
/*! ALTER TABLE meeting MODIFY COLUMN created_at timestamp(6); */
/*! ALTER TABLE meeting MODIFY COLUMN updated_at timestamp(6); */
/*! ALTER TABLE mate MODIFY COLUMN deleted_at timestamp(6); */
/*! ALTER TABLE eta CHANGE COLUMN created_at first_api_call_at timestamp not null; */
/*! ALTER TABLE eta CHANGE COLUMN updated_at last_api_call_at timestamp not null; */
/*! ALTER TABLE eta MODIFY COLUMN deleted_at timestamp(6); */
/*! ALTER TABLE notification MODIFY COLUMN send_at timestamp(6); */
/*! ALTER TABLE notification MODIFY COLUMN created_at timestamp(6); */
/*! ALTER TABLE notification MODIFY COLUMN updated_at timestamp(6); */
