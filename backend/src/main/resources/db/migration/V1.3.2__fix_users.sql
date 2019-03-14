DROP TABLE "user";

CREATE TABLE "user"
(
  id       BIGINT PRIMARY KEY,
  username VARCHAR NOT NULL,
  password VARCHAR
);

INSERT INTO "user" (id, username, password)
VALUES (1, 'drs', '$2a$11$Yow9DsbrWthcmT8FFOSPROoM3zVWHJDYOft.gQwyeZZHyOppc3fCG'),
       (2, 'alex', '$2a$11$Zxqg2KHYhHpd5yhjdmAMm.D3dz1vN/aKznGIJW.TQU.MvhLn12zqC');