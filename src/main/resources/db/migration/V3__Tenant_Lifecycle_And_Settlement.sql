-- V3__Tenant_Lifecycle_And_Settlement.sql

-- 1. Add lifecycle fields to tenant table
ALTER TABLE tenant ADD COLUMN notice_date DATE NULL;
ALTER TABLE tenant ADD COLUMN expected_exit_date DATE NULL;
ALTER TABLE tenant ADD COLUMN actual_exit_date DATE NULL;
ALTER TABLE tenant ADD COLUMN exit_reason VARCHAR(255) NULL;
ALTER TABLE tenant ADD COLUMN bed_number VARCHAR(50) NULL;

-- 2. Create tenant_settlement table for exit clearance and security deposit refund
CREATE TABLE tenant_settlement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    notice_date DATE NULL,
    actual_exit_date DATE NOT NULL,
    security_deposit_paid DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    outstanding_rent_dues DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    damage_charges DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    other_deductions DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    net_refund_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    payment_method VARCHAR(50) NULL,
    remarks TEXT NULL,
    status ENUM('PENDING', 'SETTLED') NOT NULL DEFAULT 'SETTLED',
    settled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenant(id)
);
