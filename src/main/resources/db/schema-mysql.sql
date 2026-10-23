CREATE DATABASE IF NOT EXISTS message_board
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE message_board;

CREATE TABLE IF NOT EXISTS t_user
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    role       TINYINT      NOT NULL DEFAULT 1 COMMENT '1=普通用户, 2=管理员',
    name       VARCHAR(20)  NOT NULL,
    email      VARCHAR(190) NOT NULL,
    password   VARCHAR(100) NOT NULL COMMENT 'BCrypt 哈希',
    avatar     VARCHAR(255) NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_t_user_email (email)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS t_message
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    user_id    BIGINT       NULL COMMENT 'NULL 表示匿名留言',
    content    VARCHAR(255) NOT NULL,
    ip         VARCHAR(45)  NOT NULL,
    province   VARCHAR(50)  NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_t_message_user_id (user_id),
    KEY idx_t_message_created_at (created_at),
    CONSTRAINT fk_t_message_user FOREIGN KEY (user_id) REFERENCES t_user (id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
