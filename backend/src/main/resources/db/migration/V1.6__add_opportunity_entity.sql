CREATE TABLE opportunity
(
  id          BIGINT PRIMARY KEY,
  title       VARCHAR NOT NULL,
  state       VARCHAR NOT NULL,
  description VARCHAR
);

ALTER TABLE contact_point
  ADD COLUMN opportunity_id BIGINT,
  ADD FOREIGN KEY (opportunity_id) REFERENCES opportunity (id) ON DELETE CASCADE;

CREATE SEQUENCE opportunity_seq;