CREATE TABLE IF NOT EXISTS person
(
    id               SERIAL PRIMARY KEY,
    first_name       VARCHAR(100) NOT NULL,
    last_name        VARCHAR(100) NOT NULL,
    date_of_birth    DATE         NOT NULL,
    city_of_birth    VARCHAR(100) NOT NULL,
    country_of_birth VARCHAR(100) NOT NULL,
    nationality      VARCHAR(100) NOT NULL,
    avatar           TEXT
);
