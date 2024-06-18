CREATE DOMAIN gender_type AS VARCHAR(8)
    CHECK (
        VALUE ~ 'male'
    OR VALUE ~ 'female'
    );

CREATE TABLE IF NOT EXISTS persons
(
    id        SERIAL PRIMARY KEY,
    firstname VARCHAR,
    surname   VARCHAR,
    gender    gender_type
);

CREATE TABLE IF NOT EXISTS siblings
(
    person_1_id INTEGER NOT NULL,
    person_2_id INTEGER NOT NULL,
    FOREIGN KEY (person_1_id) REFERENCES persons (id),
    FOREIGN KEY (person_2_id) REFERENCES persons (id)
);

CREATE TABLE IF NOT EXISTS spouses
(
    person_1_id INTEGER NOT NULL,
    person_2_id INTEGER NOT NULL,
    FOREIGN KEY (person_1_id) REFERENCES persons (id),
    FOREIGN KEY (person_2_id) REFERENCES persons (id)
);

CREATE TABLE IF NOT EXISTS family
(
    parent_id INTEGER NOT NULL,
    child_id  INTEGER NOT NULL,
    FOREIGN KEY (parent_id) REFERENCES persons (id),
    FOREIGN KEY (child_id) REFERENCES persons (id)
);
