insert into USERS (id, email, password, fullname) values (1, 'udvarid@hotmail.com', '{bcrypt}$2a$10$YvjRz/VbU3ScVa.GnmeGJ.EEkBPwY9m8I1YxOX5dJ0AhM2prFHZKO', 'Udvari Donát - 1');
insert into USERS (id, email, password, fullname) values (2, 'udvari.donat@gmail.com', '{bcrypt}$2a$10$YvjRz/VbU3ScVa.GnmeGJ.EEkBPwY9m8I1YxOX5dJ0AhM2prFHZKO', 'Udvari Donát - 2');
insert into USERS (id, email, password, fullname) values (3, 'donat.udvari@ponte.hu', '{bcrypt}$2a$10$YvjRz/VbU3ScVa.GnmeGJ.EEkBPwY9m8I1YxOX5dJ0AhM2prFHZKO', 'Udvari Donát - 3');

insert into ROLES (id, role) values (1, 'USER');
insert into ROLES (id, role) values (2, 'ADMIN');

insert into USERS_ROLES (user_id, role_id) values (1, 2);
insert into USERS_ROLES (user_id, role_id) values (2, 1);
insert into USERS_ROLES (user_id, role_id) values (3, 1);
