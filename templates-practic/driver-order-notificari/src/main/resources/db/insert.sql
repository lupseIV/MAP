INSERT INTO drivers(name)
values ('name'),('daniel'),('andrei');


insert into orders(driverid_id, status, start_date, end_date, pick_up_address, client_name, id, destination_address)
values (1,'PENDING', now(), null, 'Str Bumbacului', 'mihai', 1, 'centru');

select * from orders