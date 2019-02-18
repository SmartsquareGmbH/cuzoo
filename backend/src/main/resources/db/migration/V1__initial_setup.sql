CREATE TABLE company
(
  id          BIGINT PRIMARY KEY,
  name        VARCHAR NOT NULL,
  comment     VARCHAR,
  description VARCHAR,
  status      VARCHAR,
  place       VARCHAR,
  zipcode     VARCHAR,
  street      VARCHAR,
  homepage    VARCHAR,
  other       VARCHAR
);

CREATE TABLE contact
(
  id         BIGINT PRIMARY KEY,
  name       VARCHAR NOT NULL,
  role       VARCHAR,
  mail       VARCHAR,
  telephone  VARCHAR,
  comment    VARCHAR,
  homepage   VARCHAR,
  other      VARCHAR,
  company_id BIGINT,
  FOREIGN KEY (company_id) REFERENCES company (id) ON DELETE CASCADE
);

CREATE TABLE contact_point
(
  id         BIGINT PRIMARY KEY,
  title      VARCHAR   NOT NULL,
  date       TIMESTAMP NOT NULL,
  comment    VARCHAR,
  contact_id BIGINT,
  FOREIGN KEY (contact_id) REFERENCES contact (id) ON DELETE CASCADE
);

CREATE TABLE todo
(
  id          BIGINT PRIMARY KEY,
  description VARCHAR   NOT NULL,
  expiration  TIMESTAMP NOT NULL,
  reminder    TIMESTAMP NOT NULL,
  done        BOOLEAN,
  company_id  BIGINT,
  FOREIGN KEY (company_id) REFERENCES company (id) ON DELETE CASCADE
);

CREATE TABLE attachment
(
  id               BIGINT PRIMARY KEY,
  filename         VARCHAR            NOT NULL,
  content          BYTEA(9999999) NOT NULL,
  upload_date      DATE,
  contact_point_id BIGINT,
  FOREIGN KEY (contact_point_id) REFERENCES contact_point (id) ON DELETE CASCADE
);

CREATE TABLE label
(
  id    BIGINT PRIMARY KEY,
  title VARCHAR NOT NULL
);

CREATE TABLE company_labels
(
  company_id BIGINT,
  label_id   BIGINT
);

CREATE TABLE contact_point_labels
(
  contact_point_id BIGINT,
  label_id         BIGINT
);

CREATE TABLE contact_point_types
(
  contact_point_id BIGINT,
  label_id         BIGINT
);

CREATE SEQUENCE company_seq INCREMENT BY 50;
CREATE SEQUENCE contact_seq INCREMENT BY 50;
CREATE SEQUENCE contact_point_seq INCREMENT BY 50;
CREATE SEQUENCE attachment_seq INCREMENT BY 50;
CREATE SEQUENCE label_seq INCREMENT BY 50;
CREATE SEQUENCE todo_seq INCREMENT BY 50;