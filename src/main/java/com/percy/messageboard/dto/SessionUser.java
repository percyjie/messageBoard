package com.percy.messageboard.dto;

import com.percy.messageboard.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@AllArgsConstructor
public class SessionUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long id;
    private final String name;
    private final String email;
    private final String avatar;
    private final Integer role;

    public boolean isAdmin() {
        return role != null && role == User.ROLE_ADMIN;
    }

    public String getRoleLabel() {
        return isAdmin() ? "管理员" : "普通用户";
    }

    public String getInitial() {
        return name == null || name.isEmpty() ? "?" : name.substring(0, 1);
    }
}
