-- Auto-generated Data Inserts for Restaurant Order System

-- Insert Tables (restaurant tables)
INSERT INTO tables (id) VALUES (1), (2), (3), (4), (5);

-- Insert Menu Items
-- Antreuri (Starters)
INSERT INTO menu_items (id, category, item, price, currency) VALUES
    (1, 'Antreuri', 'Bruschete cu rosii', 15, 'RON'),
    (2, 'Antreuri', 'Salata Caprese', 20, 'RON'),
    (3, 'Antreuri', 'Supa de pui', 18, 'RON'),
    (4, 'Antreuri', 'Carpaccio de vita', 28, 'RON');

-- Fel Principal (Main Course)
INSERT INTO menu_items (id, category, item, price, currency) VALUES
    (5, 'Fel Principal', 'Paste cu sos pesto', 25, 'RON'),
    (6, 'Fel Principal', 'Vinete parmigiana', 25, 'RON'),
    (7, 'Fel Principal', 'Piept de pui la gratar', 35, 'RON'),
    (8, 'Fel Principal', 'Somon cu legume', 45, 'RON');

-- Desert (Dessert)
INSERT INTO menu_items (id, category, item, price, currency) VALUES
    (9, 'Desert', 'Tiramisu', 22, 'RON'),
    (10, 'Desert', 'Panna Cotta', 18, 'RON'),
    (11, 'Desert', 'Cheesecake', 20, 'RON');

-- Bauturi (Drinks)
INSERT INTO menu_items (id, category, item, price, currency) VALUES
    (12, 'Bauturi', 'Apa plata', 8, 'RON'),
    (13, 'Bauturi', 'Apa minerala', 8, 'RON'),
    (14, 'Bauturi', 'Suc de portocale', 12, 'RON'),
    (15, 'Bauturi', 'Limonada', 10, 'RON');

-- Reset sequences
SELECT setval('tables_id_seq', (SELECT MAX(id) FROM tables));
SELECT setval('menu_items_id_seq', (SELECT MAX(id) FROM menu_items));
