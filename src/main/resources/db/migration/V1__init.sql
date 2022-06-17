CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE "user" (
    id        UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    username  VARCHAR(40)  NOT NULL UNIQUE,
    password  TEXT         NOT NULL,
    game_id   UUID
);

CREATE TABLE game (
    id         UUID   PRIMARY KEY DEFAULT uuid_generate_v4(),
    game_blob  bytea  NOT NULL
);
