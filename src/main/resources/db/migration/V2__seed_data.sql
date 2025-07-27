-- Seed data for categories and products

-- Insert categories
INSERT INTO categories (name)
VALUES ('Electronics'),
       ('Clothing'),
       ('Books'),
       ('Home & Garden'),
       ('Sports');

-- Insert products
INSERT INTO products (name, price, category_id)
VALUES
-- Electronics
('Laptop', 999.99, (SELECT id FROM categories WHERE name = 'Electronics')),
('Smartphone', 699.99, (SELECT id FROM categories WHERE name = 'Electronics')),
('Tablet', 399.99, (SELECT id FROM categories WHERE name = 'Electronics')),
('Headphones', 149.99, (SELECT id FROM categories WHERE name = 'Electronics')),
('Smart Watch', 299.99, (SELECT id FROM categories WHERE name = 'Electronics')),

-- Clothing
('T-Shirt', 19.99, (SELECT id FROM categories WHERE name = 'Clothing')),
('Jeans', 59.99, (SELECT id FROM categories WHERE name = 'Clothing')),
('Sneakers', 89.99, (SELECT id FROM categories WHERE name = 'Clothing')),
('Jacket', 129.99, (SELECT id FROM categories WHERE name = 'Clothing')),
('Hat', 24.99, (SELECT id FROM categories WHERE name = 'Clothing')),

-- Books
('Programming Book', 49.99, (SELECT id FROM categories WHERE name = 'Books')),
('Novel', 14.99, (SELECT id FROM categories WHERE name = 'Books')),
('Cookbook', 29.99, (SELECT id FROM categories WHERE name = 'Books')),
('Technical Manual', 79.99, (SELECT id FROM categories WHERE name = 'Books')),
('Biography', 22.99, (SELECT id FROM categories WHERE name = 'Books')),

-- Home & Garden
('Coffee Maker', 89.99, (SELECT id FROM categories WHERE name = 'Home & Garden')),
('Plant Pot', 15.99, (SELECT id FROM categories WHERE name = 'Home & Garden')),
('Garden Tool Set', 45.99, (SELECT id FROM categories WHERE name = 'Home & Garden')),
('Lamp', 39.99, (SELECT id FROM categories WHERE name = 'Home & Garden')),
('Bedding Set', 69.99, (SELECT id FROM categories WHERE name = 'Home & Garden')),

-- Sports
('Basketball', 29.99, (SELECT id FROM categories WHERE name = 'Sports')),
('Tennis Racket', 89.99, (SELECT id FROM categories WHERE name = 'Sports')),
('Yoga Mat', 34.99, (SELECT id FROM categories WHERE name = 'Sports')),
('Running Shoes', 119.99, (SELECT id FROM categories WHERE name = 'Sports')),
('Water Bottle', 19.99, (SELECT id FROM categories WHERE name = 'Sports'));