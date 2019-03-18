ALTER TABLE CONTACT
  ADD MANAGER_ID BIGINT;

UPDATE CONTACT
SET MANAGER_ID = 2;

ALTER TABLE CONTACT
  ALTER COLUMN MANAGER_ID SET NOT NULL;

ALTER TABLE CONTACT
  ADD FOREIGN KEY (MANAGER_ID) REFERENCES CUZOO.USER (id) ON DELETE CASCADE;