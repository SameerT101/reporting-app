-- Reset tables (PostgreSQL)
TRUNCATE TABLE products RESTART IDENTITY CASCADE;
TRUNCATE TABLE categories RESTART IDENTITY CASCADE;

-- -------------------------------------------------
-- Categories
-- -------------------------------------------------
INSERT INTO categories (id, name) VALUES
                                      (1, 'Excavators'),
                                      (2, 'Loaders'),
                                      (3, 'Cranes'),
                                      (4, 'Bulldozers'),
                                      (5, 'Forklifts'),
                                      (6, 'Generators');

-- -------------------------------------------------
-- Products  (24 rows – at least 3‑4 per category)
-- -------------------------------------------------
INSERT INTO products (id, name, price, category_id) VALUES
    -- Excavators
    ( 1, 'Mini Excavator 3‑ton',          45000.00, 1),
    ( 2, 'Hydraulic Excavator 20‑ton',   180000.00, 1),
    ( 3, 'Long‑Reach Excavator 15‑ton',  150000.00, 1),
    ( 4, 'Crawler Excavator 30‑ton',     250000.00, 1),

    -- Loaders
    ( 5, 'Skid Steer Loader 75 HP',       65000.00, 2),
    ( 6, 'Wheel Loader 3 m³',            120000.00, 2),
    ( 7, 'Compact Track Loader 90 HP',    78000.00, 2),
    ( 8, 'Backhoe Loader 95 HP',          85000.00, 2),

    -- Cranes
    ( 9,  'Tower Crane 12 T',            300000.00, 3),
    (10, 'Mobile Crane 50 T',            250000.00, 3),
    (11, 'Rough‑Terrain Crane 35 T',     220000.00, 3),
    (12, 'Crawler Crane 100 T',          450000.00, 3),

    -- Bulldozers
    (13, 'Mini Dozer 80 HP',              90000.00, 4),
    (14, 'Crawler Dozer 200 HP',         160000.00, 4),
    (15, 'Swamp Dozer 150 HP',           140000.00, 4),
    (16, 'High‑Drive Dozer 350 HP',      320000.00, 4),

    -- Forklifts
    (17, 'Electric Forklift 3 T',         30000.00, 5),
    (18, 'Diesel Forklift 5 T',           45000.00, 5),
    (19, 'Reach Truck 2 T',               35000.00, 5),
    (20, 'Telehandler 4 T',               90000.00, 5),

    -- Generators
    (21, 'Diesel Generator 50 kVA',       15000.00, 6),
    (22, 'Diesel Generator 200 kVA',      35000.00, 6),
    (23, 'Gas Generator 100 kW',          28000.00, 6),
    (24, 'Portable Generator 7 kW',        8000.00, 6);
