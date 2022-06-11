CREATE TABLE "user" (
    id        UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    username  VARCHAR(40)  NOT NULL UNIQUE,
    password  TEXT         NOT NULL,
    game_id   UUID
);
