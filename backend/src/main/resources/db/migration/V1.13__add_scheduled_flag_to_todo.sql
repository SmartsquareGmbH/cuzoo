ALTER TABLE todo
    ADD COLUMN scheduled BOOLEAN;

UPDATE todo
    SET scheduled = false;