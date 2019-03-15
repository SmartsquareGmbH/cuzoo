ALTER TABLE contact
  DROP COLUMN manager_id;

DROP SCHEMA cuzoo CASCADE;

CREATE TABLE users
(
  ID       BIGINT PRIMARY KEY,
  USERNAME VARCHAR NOT NULL,
  PASSWORD VARCHAR
);

INSERT INTO users (id, username, password)
VALUES (1, 'drs', '$2a$11$Yow9DsbrWthcmT8FFOSPROoM3zVWHJDYOft.gQwyeZZHyOppc3fCG'),
       (2, 'alex', '$2a$11$Zxqg2KHYhHpd5yhjdmAMm.D3dz1vN/aKznGIJW.TQU.MvhLn12zqC');

ALTER TABLE contact
  ADD manager_id BIGINT;

UPDATE contact
SET manager_id = 2;

ALTER TABLE contact
  ALTER COLUMN manager_id SET NOT NULL;

ALTER TABLE contact
  ADD FOREIGN KEY (manager_id) REFERENCES users (id) ON DELETE CASCADE;