package com.percy.messageboard.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("t_user")
public class User {

    public static final int ROLE_USER = 1;
    public static final int ROLE_ADMIN = 2;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer role;

    private String name;

    private String email;

    private String password;

    private String avatar;

    private LocalDateTime createdAt;

    public boolean isAdmin() {
        return role != null && role == ROLE_ADMIN;
    }
}
