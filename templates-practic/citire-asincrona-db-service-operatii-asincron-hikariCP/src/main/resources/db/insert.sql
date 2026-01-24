-- Auto-generated Data Inserts

-- 1. REGULAR STAFF
-- Notice: We include inherited fields (name, email) and local fields (salary)
INSERT INTO staff (name, email, phone_number, salary, hire_date, department) VALUES
                                                                                 ('Alice Staff', 'alice@company.com', '0700123456', 4500.00, '2022-03-15', 'Sales'),
                                                                                 ('Bob Support', 'bob@company.com', '0700654321', 4000.00, '2022-06-20', 'Support'),
                                                                                 ('Charlie Dev', 'charlie@company.com', '0700987654', 6500.00, '2023-01-10', 'IT');

-- 2. MANAGERS (Separate Table)
-- Contains Person fields + Staff fields + Manager fields
INSERT INTO managers (name, email, phone_number, salary, hire_date, department, bonus, team_size, access_level) VALUES
                                                                                                                    ('Diana Boss', 'diana@company.com', '0799000111', 9500.00, '2020-01-01', 'IT', 2000.00, 10, 5),
                                                                                                                    ('Evan Lead', 'evan@company.com', '0799000222', 8500.00, '2021-05-15', 'Sales', 1500.00, 5, 4);
-- 1. Tech & Corporate Clients
INSERT INTO clients (full_name, client_type, budget, registration_date) VALUES
                                                                            ('TechStart Solutions', 'Corporate', 15000.00, '2023-01-15'),
                                                                            ('Global Dynamics', 'Corporate', 45000.50, '2023-02-10'),
                                                                            ('SoftServe Inc.', 'Corporate', 12000.00, '2023-03-05'),
                                                                            ('Alpha Networks', 'Corporate', 22500.00, '2023-03-22'),
                                                                            ('Omega Systems', 'Corporate', 33000.00, '2023-04-11');

-- 2. Individual & Freelance Clients
INSERT INTO clients (full_name, client_type, budget, registration_date) VALUES
                                                                            ('John Doe', 'Individual', 1500.00, '2023-05-01'),
                                                                            ('Jane Smith', 'Individual', 2500.00, '2023-05-15'),
                                                                            ('Robert Brown', 'Freelancer', 500.00, '2023-06-01'),
                                                                            ('Emily Davis', 'Freelancer', 850.00, '2023-06-10'),
                                                                            ('Michael Wilson', 'Individual', 3000.00, '2023-07-20');

-- 3. Edge Cases (Low budget, Recent dates)
INSERT INTO clients (full_name, client_type, budget, registration_date) VALUES
                                                                            ('Budget Fixers', 'Small Business', 100.00, '2023-11-01'),
                                                                            ('Late Comer LLC', 'Corporate', 50000.00, '2023-12-01');
-- 3. VIP CLIENTS
-- Note: 'assigned_manager_id' refers to the ID in the 'managers' table (1 = Diana, 2 = Evan)
INSERT INTO vip_clients (full_name, client_type, budget, registration_date, loyalty_points, personalmanager_id) VALUES
                                                                                                                     ('MegaCorp Int.', 'Corporate', 100000.00, '2023-01-01', 5000, 1),

                                                                                                             ('Wealthy Individual', 'Individual', 25000.00, '2023-03-15', 1200, 2);

select *
from clients;