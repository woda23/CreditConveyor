-- Таблица "passport"
CREATE TABLE passport
(
    passport_id   SERIAL PRIMARY KEY,
    passport_data JSONB
);

-- Таблица "employment"
CREATE TABLE employment
(
    employment_id   SERIAL PRIMARY KEY,
    employment_data JSONB
);

-- Таблица "client"
CREATE TABLE client
(
    client_id        SERIAL PRIMARY KEY,
    first_name       VARCHAR(255),
    last_name        VARCHAR(255),
    middle_name      VARCHAR(255),
    email            VARCHAR(255),
    gender           VARCHAR(255),
    marital_status   VARCHAR(255),
    dependent_amount INTEGER,
    birth_date       DATE,
    passport_id      BIGINT,
    employment_id    VARCHAR(255),
    account          VARCHAR(255),
    FOREIGN KEY (passport_id) REFERENCES passport (passport_id)
);

-- Таблица "credit"
CREATE TABLE credit
(
    credit_id        SERIAL PRIMARY KEY,
    amount           DECIMAL,
    term             INTEGER,
    monthly_payment  DECIMAL,
    rate             DECIMAL,
    psk              DECIMAL,
    payment_schedule JSONB,
    insurance_enable BOOLEAN,
    salary_client    BOOLEAN,
    credit_status    VARCHAR(255)
);

-- Таблица "application"
CREATE TABLE application
(
    application_id SERIAL PRIMARY KEY,
    client_id      BIGINT,
    credit_id      BIGINT,
    status         VARCHAR(255),
    creation_data  TIMESTAMP,
    applied_offer  VARCHAR(255),
    sign_date      TIMESTAMP,
    sec_code       INTEGER,
    status_history JSONB,
    FOREIGN KEY (client_id) REFERENCES client (client_id),
    FOREIGN KEY (credit_id) REFERENCES credit (credit_id)
);
