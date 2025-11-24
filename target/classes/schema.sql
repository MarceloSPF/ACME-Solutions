-- Drop tables if they exist (useful for clean setup)
DROP TABLE IF EXISTS service_order_parts;
DROP TABLE IF EXISTS service_items;
DROP TABLE IF EXISTS parts;
DROP TABLE IF EXISTS service_orders;
DROP TABLE IF EXISTS vehicles;
DROP TABLE IF EXISTS technicians;
DROP TABLE IF EXISTS customers;

-- Create customers table
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(255) NOT NULL
);

-- Create technicians table
CREATE TABLE technicians (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    specialization VARCHAR(100) NOT NULL
);

-- Create vehicles table
CREATE TABLE vehicles (
    id BIGSERIAL PRIMARY KEY,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    model_year INTEGER NOT NULL,
    license_plate VARCHAR(20) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    CONSTRAINT fk_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

-- Create service_orders table
CREATE TABLE service_orders (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    technician_id BIGINT NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    total_cost DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_service_order_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT fk_service_order_vehicle
        FOREIGN KEY (vehicle_id)
        REFERENCES vehicles (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT fk_service_order_technician
        FOREIGN KEY (technician_id)
        REFERENCES technicians (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT check_status
        CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELED'))
);

-- Create parts table
CREATE TABLE parts (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(100) NOT NULL UNIQUE,
    unit_price DECIMAL(10,2) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0
);

-- Create service_items table
CREATE TABLE service_items (
    id BIGSERIAL PRIMARY KEY,
    service_order_id BIGINT NOT NULL,
    description VARCHAR(500) NOT NULL,
    labor_cost DECIMAL(10,2) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    CONSTRAINT fk_service_item_order
        FOREIGN KEY (service_order_id)
        REFERENCES service_orders (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- Create service_order_parts table (Many-to-Many relationship)
CREATE TABLE service_order_parts (
    id BIGSERIAL PRIMARY KEY,
    service_order_id BIGINT NOT NULL,
    part_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_service_order_part_order
        FOREIGN KEY (service_order_id)
        REFERENCES service_orders (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_service_order_part_part
        FOREIGN KEY (part_id)
        REFERENCES parts (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_vehicles_customer ON vehicles(customer_id);
CREATE INDEX idx_service_orders_customer ON service_orders(customer_id);
CREATE INDEX idx_service_orders_vehicle ON service_orders(vehicle_id);
CREATE INDEX idx_service_orders_technician ON service_orders(technician_id);
CREATE INDEX idx_service_orders_status ON service_orders(status);
CREATE INDEX idx_parts_code ON parts(code);
CREATE INDEX idx_service_items_order ON service_items(service_order_id);
CREATE INDEX idx_service_order_parts_order ON service_order_parts(service_order_id);
CREATE INDEX idx_service_order_parts_part ON service_order_parts(part_id);