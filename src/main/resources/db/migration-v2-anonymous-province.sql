-- 适用于已经按旧版 schema-mysql.sql 创建过数据库的环境。
-- 仅需执行一次；如果 province 列已存在会报错，可忽略对应语句。
USE message_board;

ALTER TABLE t_message
    MODIFY COLUMN user_id BIGINT NULL COMMENT 'NULL 表示匿名留言';

ALTER TABLE t_message
    ADD COLUMN province VARCHAR(50) NULL AFTER ip;
