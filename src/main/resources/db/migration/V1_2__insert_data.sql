insert into roles (name)
values
    ('ROLE_USER'), ('ROLE_ADMIN');

insert into users (username, password, email, refresh_token_id)
values
    ('user', '$2a$04$Fx/SX9.BAvtPlMyIIqqFx.hLY2Xp8nnhpzvEEVINvVpwIPbA3v/.i', 'user@gmail.com', null),
    ('admin', '$2a$04$Fx/SX9.BAvtPlMyIIqqFx.hLY2Xp8nnhpzvEEVINvVpwIPbA3v/.i', 'admin@gmail.com', null);

insert into users_roles (user_id, role_id)
values
    (1, 1),
    (2, 2);