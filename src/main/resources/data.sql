insert into USERS (id, email, password, fullname, enabled) values (1, 'udvarid@hotmail.com', '{bcrypt}$2a$10$YvjRz/VbU3ScVa.GnmeGJ.EEkBPwY9m8I1YxOX5dJ0AhM2prFHZKO', 'Udvari Donát - 1', true);
insert into USERS (id, email, password, fullname, enabled) values (2, 'udvari.donat@gmail.com', '{bcrypt}$2a$10$YvjRz/VbU3ScVa.GnmeGJ.EEkBPwY9m8I1YxOX5dJ0AhM2prFHZKO', 'Udvari Donát - 2', true);

insert into ROLES (id, role) values (1, 'ROLE_USER');
insert into ROLES (id, role) values (2, 'ROLE_ADMIN');

insert into USERS_ROLES (user_id, role_id) values (1, 2);
insert into USERS_ROLES (user_id, role_id) values (2, 1);
