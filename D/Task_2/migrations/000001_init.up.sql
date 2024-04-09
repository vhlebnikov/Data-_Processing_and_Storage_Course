CREATE DOMAIN gender_type AS VARCHAR(8)
    CHECK (
        VALUE ~ 'male'
    OR VALUE ~ 'female'
    );

CREATE DOMAIN sibling_type AS VARCHAR(9)
    CHECK (
        VALUE ~ 'brother'
    OR VALUE ~ 'sister'
    );

CREATE DOMAIN parent_type AS VARCHAR(8)
    CHECK (
        VALUE ~ 'father'
    OR VALUE ~ 'mother'
    );

CREATE DOMAIN spouce_type AS VARCHAR(9)
    CHECK (
        VALUE ~ 'husband'
    OR VALUE ~ 'wife'
    );

CREATE DOMAIN child_type AS VARCHAR(10)
    CHECK (
        VALUE ~ 'son'
    OR VALUE ~ 'daughter'
    );

CREATE TABLE persons
(
    id       SERIAL PRIMARY KEY,
    fullname VARCHAR,
    surname  VARCHAR,
    gender   gender_type
);

CREATE TABLE person_parents
(
    id               SERIAL PRIMARY KEY,
    type             parent_type NOT NULL,
    master_person_id INTEGER     NOT NULL,
    slave_person_id  INTEGER     NOT NULL,
    FOREIGN KEY (master_person_id) REFERENCES persons (id),
    FOREIGN KEY (slave_person_id) REFERENCES persons (id)
);

CREATE TABLE person_siblings
(
    id               SERIAL PRIMARY KEY,
    type             sibling_type NOT NULL,
    master_person_id INTEGER      NOT NULL,
    slave_person_id  INTEGER      NOT NULL,
    FOREIGN KEY (master_person_id) REFERENCES persons (id),
    FOREIGN KEY (slave_person_id) REFERENCES persons (id)
);

CREATE TABLE person_children
(
    id               SERIAL PRIMARY KEY,
    type             child_type NOT NULL,
    master_person_id INTEGER    NOT NULL,
    slave_person_id  INTEGER    NOT NULL,
    FOREIGN KEY (master_person_id) REFERENCES persons (id),
    FOREIGN KEY (slave_person_id) REFERENCES persons (id)
);

CREATE TABLE person_spouce
(
    id               SERIAL PRIMARY KEY,
    type             spouce_type NOT NULL,
    master_person_id INTEGER     NOT NULL,
    slave_person_id  INTEGER     NOT NULL,
    FOREIGN KEY (master_person_id) REFERENCES persons (id),
    FOREIGN KEY (slave_person_id) REFERENCES persons (id)
);


