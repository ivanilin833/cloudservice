CREATE TABLE files
(
    id           bigserial PRIMARY KEY,
    filename     VARCHAR(255) NOT NULL UNIQUE,
    date         TIMESTAMP    NOT NULL,
    size         BIGINT       NOT NULL,
    file_content BYTEA        NOT NULL, -- Используем BYTEA для хранения массива байтов
    user_id      BIGINT,                -- Внешний ключ для связи с таблицей пользователей
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id)
);
