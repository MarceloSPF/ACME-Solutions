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

-- 1. Inserir Peças no Estoque (Parts)
INSERT INTO parts (name, code, unit_price, stock) VALUES
('Filtro de Óleo', 'FIL-001', 45.00, 50),
('Óleo Sintético 5W30', 'OIL-530', 60.00, 100),
('Pastilha de Freio Dianteira', 'BRK-F01', 150.00, 30),
('Pneu 195/60 R15', 'TR-195', 450.00, 20),
('Kit Limpeza Injeção', 'INJ-KIT', 85.00, 15),
('Bico Injetor', 'INJ-002', 350.00, 10);

-- 2. Inserir Itens de Serviço / Mão de Obra (Service Items)

-- Para a OS do João Silva (Revisão Completa)
INSERT INTO service_items (service_order_id, description, labor_cost, quantity) VALUES
(
    (SELECT id FROM service_orders WHERE description LIKE 'Revisão completa%'),
    'Troca de óleo e filtro',
    120.00,
    1
),
(
    (SELECT id FROM service_orders WHERE description LIKE 'Revisão completa%'),
    'Substituição das pastilhas de freio',
    180.00,
    1
);

-- Para a OS da Maria Santos (Injeção Eletrônica)
INSERT INTO service_items (service_order_id, description, labor_cost, quantity) VALUES
(
    (SELECT id FROM service_orders WHERE description LIKE 'Diagnóstico e reparo%'),
    'Diagnóstico com Scanner',
    150.00,
    1
),
(
    (SELECT id FROM service_orders WHERE description LIKE 'Diagnóstico e reparo%'),
    'Limpeza de bicos injetores',
    250.00,
    1
);

-- Para a OS do Carlos Oliveira (Pneus)
INSERT INTO service_items (service_order_id, description, labor_cost, quantity) VALUES
(
    (SELECT id FROM service_orders WHERE description LIKE 'Alinhamento%'),
    'Alinhamento 3D',
    100.00,
    1
),
(
    (SELECT id FROM service_orders WHERE description LIKE 'Alinhamento%'),
    'Balanceamento de rodas',
    40.00,
    4
);

-- 3. Inserir Peças Utilizadas nas Ordens (Service Order Parts)

-- Peças para a OS do João (Revisão)
INSERT INTO service_order_parts (service_order_id, part_id, quantity, unit_price) VALUES
(
    (SELECT id FROM service_orders WHERE description LIKE 'Revisão completa%'),
    (SELECT id FROM parts WHERE code = 'FIL-001'),
    1,
    45.00
),
(
    (SELECT id FROM service_orders WHERE description LIKE 'Revisão completa%'),
    (SELECT id FROM parts WHERE code = 'OIL-530'),
    4,
    60.00
),
(
    (SELECT id FROM service_orders WHERE description LIKE 'Revisão completa%'),
    (SELECT id FROM parts WHERE code = 'BRK-F01'),
    1,
    150.00
);

-- Peças para a OS da Maria (Injeção)
INSERT INTO service_order_parts (service_order_id, part_id, quantity, unit_price) VALUES
(
    (SELECT id FROM service_orders WHERE description LIKE 'Diagnóstico e reparo%'),
    (SELECT id FROM parts WHERE code = 'INJ-KIT'),
    1,
    85.00
),
(
    (SELECT id FROM service_orders WHERE description LIKE 'Diagnóstico e reparo%'),
    (SELECT id FROM parts WHERE code = 'INJ-002'),
    1,
    350.00
);

-- Peças para a OS do Carlos (Pneus)
INSERT INTO service_order_parts (service_order_id, part_id, quantity, unit_price) VALUES
(
    (SELECT id FROM service_orders WHERE description LIKE 'Alinhamento%'),
    (SELECT id FROM parts WHERE code = 'TR-195'),
    2,
    450.00
);