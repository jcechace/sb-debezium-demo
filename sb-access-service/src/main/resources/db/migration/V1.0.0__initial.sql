DROP TABLE IF EXISTS access;

CREATE TABLE access
(
    id       BIGSERIAL     NOT NULL PRIMARY KEY,
    level    VARCHAR(255),
    service  VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    CONSTRAINT access_level_check CHECK (((level)::text = ANY ((ARRAY['READ':: character varying, 'WRITE':: character varying, 'MANAGE':: character varying])::text[]))),
    CONSTRAINT access_username_service_key UNIQUE (username, service)
);

ALTER TABLE access REPLICA IDENTITY FULL;
