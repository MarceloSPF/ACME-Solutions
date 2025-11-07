-- Insert customers
INSERT INTO customers (name, email, phone, address) VALUES
('João Silva', 'joao.silva@email.com', '(11) 98765-4321', 'Rua das Flores, 123 - São Paulo, SP'),
('Maria Santos', 'maria.santos@email.com', '(11) 97654-3210', 'Avenida Paulista, 1000 - São Paulo, SP'),
('Carlos Oliveira', 'carlos.oliveira@email.com', '(11) 96543-2109', 'Rua Augusta, 789 - São Paulo, SP');

-- Insert technicians
INSERT INTO technicians (name, email, specialization) VALUES
('Pedro Souza', 'pedro.souza@oficina.com', 'Mecânico Geral'),
('Ana Costa', 'ana.costa@oficina.com', 'Especialista em Injeção Eletrônica');

-- Insert vehicles (using real Brazilian license plate format)
INSERT INTO vehicles (brand, model, model_year, license_plate, customer_id) VALUES
('Toyota', 'Corolla', 2022, 'ABC1D23', (SELECT id FROM customers WHERE name = 'João Silva')),
('Honda', 'Civic', 2021, 'DEF2G45', (SELECT id FROM customers WHERE name = 'João Silva')),
('Volkswagen', 'Golf', 2023, 'GHI3J67', (SELECT id FROM customers WHERE name = 'Maria Santos')),
('Hyundai', 'HB20', 2020, 'KLM4N89', (SELECT id FROM customers WHERE name = 'Carlos Oliveira'));

-- Insert service orders with different statuses and realistic dates/times
INSERT INTO service_orders 
(customer_id, vehicle_id, technician_id, description, status, created_at, completed_at, total_cost) 
VALUES
(
    (SELECT id FROM customers WHERE name = 'João Silva'),
    (SELECT id FROM vehicles WHERE license_plate = 'ABC1D23'),
    (SELECT id FROM technicians WHERE name = 'Pedro Souza'),
    'Revisão completa 30.000 km, troca de óleo, filtros e pastilhas de freio',
    'COMPLETED',
    '2025-10-15 09:00:00',
    '2025-10-15 17:30:00',
    850.00
),
(
    (SELECT id FROM customers WHERE name = 'Maria Santos'),
    (SELECT id FROM vehicles WHERE license_plate = 'GHI3J67'),
    (SELECT id FROM technicians WHERE name = 'Ana Costa'),
    'Diagnóstico e reparo do sistema de injeção eletrônica',
    'IN_PROGRESS',
    '2025-11-02 14:00:00',
    NULL,
    1200.00
),
(
    (SELECT id FROM customers WHERE name = 'Carlos Oliveira'),
    (SELECT id FROM vehicles WHERE license_plate = 'KLM4N89'),
    (SELECT id FROM technicians WHERE name = 'Pedro Souza'),
    'Alinhamento, balanceamento e troca de pneus',
    'PENDING',
    '2025-11-03 10:00:00',
    NULL,
    600.00
);