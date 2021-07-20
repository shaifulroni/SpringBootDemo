CREATE TABLE IF NOT EXISTS customer (
    id bigserial PRIMARY KEY,
    first_name varchar(50) DEFAULT NULL,
    last_name varchar(50) DEFAULT NULL,
    phone_number varchar(20) DEFAULT NULL,
    email varchar(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS currency (
    id serial PRIMARY KEY,
    name varchar(30) NOT NULL,
    short_name varchar(3) UNIQUE NOT NULL,
    symbol varchar(5) DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS country (
    id serial PRIMARY KEY,
    name varchar(30) NOT NULL,
    code varchar(3) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS account (
    id bigserial PRIMARY KEY,
    customer_id bigint UNIQUE NOT NULL,
    country_id integer NOT NULL,
    CONSTRAINT fk_customer
        FOREIGN KEY(customer_id)
            REFERENCES customer(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_country
        FOREIGN KEY(country_id)
            REFERENCES country(id)
);

CREATE TABLE IF NOT EXISTS balance (
    id bigserial PRIMARY KEY,
    amount numeric DEFAULT 0.0,
    account_id bigint NOT NULL,
    currency_id integer NOT NULL,
    CONSTRAINT fk_account
        FOREIGN KEY(account_id)
            REFERENCES account(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_currency
        FOREIGN KEY(currency_id)
            REFERENCES currency(id),
    CONSTRAINT balance_positive
        CHECK (amount >= 0.0),
    UNIQUE(account_id, currency_id)
);

create type transaction_dir AS ENUM('IN', 'OUT');

CREATE TABLE IF NOT EXISTS transaction (
    id bigserial PRIMARY KEY,
    account_id bigint NOT NULL,
    currency_id int NOT NULL,
    amount numeric NOT NULL,
    direction transaction_dir NOT NULL,
    description varchar(250) NOT NULL,
    CONSTRAINT fk_account
        FOREIGN KEY(account_id)
            REFERENCES account(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_currency
            FOREIGN KEY(currency_id)
                REFERENCES currency(id),
    CONSTRAINT amount_positive
            CHECK (amount >= 0.0)
);