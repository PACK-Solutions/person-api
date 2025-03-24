CREATE TABLE IF NOT EXISTS person (
    id SERIAL PRIMARY KEY,
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    city_of_birth VARCHAR(100) NOT NULL,
    country_of_birth VARCHAR(100) NOT NULL,
    nationality VARCHAR(100) NOT NULL
);