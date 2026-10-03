--liquibase formatted sql

--changeset staff:002-create-loans
CREATE TABLE loan (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    item_id UUID NOT NULL,
    borrower_id UUID NOT NULL,
    issued_by_id UUID NOT NULL,
    issued_at TIMESTAMP WITH TIME ZONE NOT NULL,
    due_at TIMESTAMP WITH TIME ZONE NOT NULL,
    issued_condition TEXT NOT NULL,
    returned_at TIMESTAMP WITH TIME ZONE,
    returned_by_id UUID,
    returned_condition TEXT,
    CONSTRAINT fk_loan_item FOREIGN KEY (item_id) REFERENCES item (id) ON DELETE RESTRICT,
    CONSTRAINT fk_loan_borrower FOREIGN KEY (borrower_id) REFERENCES app_user (id) ON DELETE RESTRICT,
    CONSTRAINT fk_loan_issued_by FOREIGN KEY (issued_by_id) REFERENCES app_user (id) ON DELETE RESTRICT,
    CONSTRAINT fk_loan_returned_by FOREIGN KEY (returned_by_id) REFERENCES app_user (id) ON DELETE RESTRICT,
    CONSTRAINT ck_loan_due_at CHECK (due_at > issued_at),
    CONSTRAINT ck_loan_issued_condition CHECK (btrim(issued_condition) <> ''),
    CONSTRAINT ck_loan_return_fields CHECK (
        (returned_at IS NULL AND returned_by_id IS NULL AND returned_condition IS NULL)
        OR
        (returned_at IS NOT NULL AND returned_by_id IS NOT NULL AND returned_condition IS NOT NULL)
    ),
    CONSTRAINT ck_loan_returned_at CHECK (returned_at >= issued_at),
    CONSTRAINT ck_loan_returned_condition CHECK (btrim(returned_condition) <> '')
);

CREATE UNIQUE INDEX uq_active_loan_per_item ON loan (item_id) WHERE returned_at IS NULL;
CREATE INDEX ix_loan_borrower_issued_at ON loan (borrower_id, issued_at DESC);
CREATE INDEX ix_loan_item_issued_at ON loan (item_id, issued_at DESC);

