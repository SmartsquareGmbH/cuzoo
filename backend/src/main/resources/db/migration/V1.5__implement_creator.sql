ALTER TABLE contact_point
  ADD creator_id BIGINT;

ALTER TABLE todo
  ADD creator_id BIGINT;

UPDATE contact_point
SET creator_id = 2;

UPDATE todo
SET creator_id = 2;

ALTER TABLE contact_point
  ALTER COLUMN creator_id SET NOT NULL;

ALTER TABLE todo
  ALTER COLUMN creator_id SET NOT NULL;

ALTER TABLE contact_point
  ADD FOREIGN KEY (creator_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE todo
  ADD FOREIGN KEY (creator_id) REFERENCES users (id) ON DELETE CASCADE;