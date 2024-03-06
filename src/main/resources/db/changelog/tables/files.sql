CREATE TABLE files
(
    id           int not null auto_increment primary key,
    filename      varchar(255) not null,
    date          datetime(6)  not null,
    file_content  longblob     not null,
    size          bigint       not null,
    user_id      int,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id)
);
