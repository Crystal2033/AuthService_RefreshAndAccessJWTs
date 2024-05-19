CREATE TABLE refresh_token (
                               id                    int not null,
                               refresh_token_name    text not null ,
                               primary key (id)
);

ALTER TABLE users ADD COLUMN IF NOT EXISTS refresh_token_id int;
ALTER TABLE users ADD CONSTRAINT fk_user_token foreign key (refresh_token_id) references refresh_token (id);