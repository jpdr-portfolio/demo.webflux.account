--DROP TABLE IF EXISTS account;
--DROP TABLE IF EXISTS account_transaction;

CREATE TABLE IF NOT EXISTS account (
  id int AUTO_INCREMENT primary key,
  number UUID NOT NULL,
  owner_id int NOT NULL,
  owner_name VARCHAR(100) NOT NULL,
  balance NUMERIC(20,2) NOT NULL,
  last_transaction_id int NULL,
  last_transaction_date TIMESTAMP WITH TIME ZONE NULL,
  is_active BOOLEAN NOT NULL,
  creation_date TIMESTAMP WITH TIME ZONE NOT NULL,
  deletion_date TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS account_transaction (
  id int AUTO_INCREMENT primary key,
  account_id int NOT NULL,
  transaction_date TIMESTAMP WITH TIME ZONE NULL,
  transaction_type CHAR(1) NOT NULL,
  transaction_amount NUMERIC(20,2) NOT NULL,
  transaction_description VARCHAR(100) NULL,
  previous_balance NUMERIC(20,2) NOT NULL,
  current_balance NUMERIC(20,2) NOT NULL
);