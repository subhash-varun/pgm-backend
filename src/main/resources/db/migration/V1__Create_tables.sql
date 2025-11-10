-- V1__Create_tables.sql

-- Admin table
CREATE TABLE admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    contact_no VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Staff table
CREATE TABLE staff (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    admin_id BIGINT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    password VARCHAR(255),
    role ENUM('manager', 'staff') DEFAULT 'staff',
    status ENUM('active', 'inactive') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES admin(id)
);

-- Room table
CREATE TABLE room (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(20) UNIQUE NOT NULL,
    room_type ENUM('single', 'double', 'shared') NOT NULL,
    rent_amount DECIMAL(10,2) NOT NULL,
    status ENUM('available', 'occupied', 'maintenance') DEFAULT 'available',
    facilities TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tenant table
CREATE TABLE tenant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(15) NOT NULL,
    id_proof_type VARCHAR(50),
    id_proof_number VARCHAR(100),
    check_in_date DATE NOT NULL,
    check_out_date DATE,
    deposit_amount DECIMAL(10,2) DEFAULT 0,
    status ENUM('active', 'moved_out') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES room(id)
);

-- Payment table
CREATE TABLE payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT,
    amount DECIMAL(10,2) NOT NULL,
    payment_date DATE,
    payment_month VARCHAR(20),
    payment_method ENUM('cash', 'online', 'bank_transfer', 'upi'),
    receipt_number VARCHAR(50) UNIQUE,
    status ENUM('paid', 'pending', 'failed') DEFAULT 'paid',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenant(id)
);

-- Inventory table
CREATE TABLE inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT,
    item_name VARCHAR(100) NOT NULL,
    quantity INT DEFAULT 1,
    condition_status ENUM('good', 'needs_repair', 'replaced') DEFAULT 'good',
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES room(id)
);

-- Role table
CREATE TABLE role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Permission table
CREATE TABLE permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    `key` VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Role Permission table (many-to-many)
CREATE TABLE role_permission (
    role_id BIGINT,
    permission_id BIGINT,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES role(id),
    FOREIGN KEY (permission_id) REFERENCES permission(id)
);

-- User Role table
CREATE TABLE user_role (
    user_id BIGINT,
    role_id BIGINT,
    user_type ENUM('ADMIN', 'STAFF'),
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (role_id) REFERENCES role(id)
);

-- Maintenance table (placeholder)
CREATE TABLE maintenance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT,
    description TEXT,
    status ENUM('pending', 'in_progress', 'completed') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES room(id)
);
