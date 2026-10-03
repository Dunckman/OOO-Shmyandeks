--liquibase formatted sql

--changeset staff:001-create-users-and-items
CREATE TABLE app_user (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    login VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(16) NOT NULL,
    CONSTRAINT uq_app_user_login UNIQUE (login),
    CONSTRAINT ck_app_user_login CHECK (btrim(login) <> ''),
    CONSTRAINT ck_app_user_password_hash CHECK (btrim(password_hash) <> ''),
    CONSTRAINT ck_app_user_role CHECK (role IN ('USER', 'STAFF'))
);

CREATE TABLE item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    inventory_number VARCHAR(100) NOT NULL,
    name VARCHAR(200) NOT NULL,
    condition_description TEXT NOT NULL,
    issue_allowed BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_item_inventory_number UNIQUE (inventory_number),
    CONSTRAINT ck_item_inventory_number CHECK (btrim(inventory_number) <> ''),
    CONSTRAINT ck_item_name CHECK (btrim(name) <> ''),
    CONSTRAINT ck_item_condition CHECK (btrim(condition_description) <> '')
);

