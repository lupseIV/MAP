
INSERT INTO users (username, password, role) VALUES
    ('admin1', 'admin123', 'ADMIN'),
    ('admin2', 'admin123', 'ADMIN'),
    ('admin3', 'admin123', 'ADMIN'),
    ('dealer1', 'dealer123', 'DEALER'),
    ('dealer2', 'dealer123', 'DEALER'),
    ('dealer3', 'dealer123', 'DEALER')
ON CONFLICT (username) DO NOTHING;

DELETE FROM cars;

INSERT INTO cars (name, description, base_price, status, comments, rejection_reason) VALUES
    ('Dacia Duster', 'SUV 1.5 dCi', 22000.00, 'NEW', NULL, NULL),
    ('Dacia Sandero', 'dimpla si rapida ', 14500.00, 'NEW', NULL, NULL),
    ('Dacia Logan', 'modificata motor 1.9 tdi, masina romanaului', 13000.00, 'NEEDS_APPROVAL', 'pachet navigatie', NULL),
    ('Dacia Jogger', '7 locuri', 19500.00, 'NEEDS_APPROVAL', 'scaune piele', NULL),
    ('Dacia Spring', 'potrivita pentru familie', 21000.00, 'APPROVED', NULL, NULL),
    ('Dacia 4x4', 'tractiune integrala', 27000.00, 'REJECTED', 'culoare speciala', 'culoare indisponibila'),
    ('Dacia Sandero Stepway', 'colorata', 16500.00, 'NEW', NULL, NULL),
    ('Dacia DACIA', 'Hibrid 140 CP', 23500.00, 'APPROVED', NULL, NULL)
ON CONFLICT DO NOTHING;
