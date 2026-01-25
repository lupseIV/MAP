insert into doctors (name, specialty) values
('Dr. John Smith', 'Cardiology'),
('Dr. Emily Johnson', 'Neurology'),
('Dr. Michael Brown', 'Pediatrics');

insert into pacients (name, cnp) values
('Alice Williams', '1234567890123'),
('Bob Miller', '9876543210987'),
('Charlie Davis', '4567891234567');

insert into programari (id_medic, id_pacient, data_ora, status) values
(1, 1, '2024-02-15 10:00:00', 'Scheduled'),
(2, 2, '2024-02-16 11:30:00', 'Completed'),
(3, 3, '2024-02-17 09:15:00', 'Cancelled');

insert into programari (id_medic, id_pacient, data_ora, status) values
(1, 2, '2024-02-15 10:00:00', 'Scheduled'),
(1, 3, '2024-02-16 11:30:00', 'Completed'),
(1, 1, '2024-02-17 09:15:00', 'Scheduled');