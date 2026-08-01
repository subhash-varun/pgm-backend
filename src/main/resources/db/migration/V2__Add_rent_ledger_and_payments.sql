-- V2__Add_rent_ledger_and_payments.sql

-- Add advance_balance to tenant
ALTER TABLE tenant ADD COLUMN advance_balance DECIMAL(10,2) DEFAULT 0.00;

-- Create rent_ledger table
CREATE TABLE rent_ledger (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    billing_month VARCHAR(7) NOT NULL,
    base_rent DECIMAL(10,2) NOT NULL,
    utility_charges DECIMAL(10,2) DEFAULT 0.00,
    late_fee DECIMAL(10,2) DEFAULT 0.00,
    total_amount DECIMAL(10,2) NOT NULL,
    paid_amount DECIMAL(10,2) DEFAULT 0.00,
    balance_due DECIMAL(10,2) NOT NULL,
    due_date DATE NOT NULL,
    status ENUM('UNPAID', 'PARTIAL', 'PAID', 'OVERDUE') DEFAULT 'UNPAID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenant(id),
    CONSTRAINT uk_tenant_month UNIQUE (tenant_id, billing_month)
);

-- Add ledger_id to payment
ALTER TABLE payment ADD COLUMN ledger_id BIGINT NULL;
ALTER TABLE payment ADD CONSTRAINT fk_payment_ledger FOREIGN KEY (ledger_id) REFERENCES rent_ledger(id);
