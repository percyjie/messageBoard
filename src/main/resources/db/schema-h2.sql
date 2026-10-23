CREATE TABLE IF NOT EXISTS t_user
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    role       TINYINT      NOT NULL DEFAULT 1,
    name       VARCHAR(20)  NOT NULL,
    email      VARCHAR(190) NOT NULL,
    password   VARCHAR(100) NOT NULL,
    avatar     VARCHAR(255),
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_t_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS t_message
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    content    VARCHAR(255) NOT NULL,
    ip         VARCHAR(45)  NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_t_message_user FOREIGN KEY (user_id) REFERENCES t_user (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_t_message_user_id ON t_message (user_id);
CREATE INDEX IF NOT EXISTS idx_t_message_created_at ON t_message (created_at);
