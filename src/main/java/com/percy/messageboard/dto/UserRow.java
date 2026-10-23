package com.percy.messageboard.dto;

import com.percy.messageboard.entity.User;
import com.percy.messageboard.util.TimeUtils;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserRow {

    private Long id;
    private Integer role;
    private String name;
    private String email;
    private String avatar;
    private LocalDateTime createdAt;

    public boolean isAdmin() {
        return role != null && role == User.ROLE_ADMIN;
    }

    public String getRoleLabel() {
        return isAdmin() ? "管理员" : "普通用户";
    }

    public String getInitial() {
        return name == null || name.isEmpty() ? "?" : name.substring(0, 1);
    }

    public String getCreatedAtText() {
        return TimeUtils.format(createdAt);
    }

    public static UserRow from(User user) {
        if (user == null) {
            return null;
        }
        UserRow row = new UserRow();
        row.setId(user.getId());
        row.setRole(user.getRole());
        row.setName(user.getName());
        row.setEmail(user.getEmail());
        row.setAvatar(user.getAvatar());
        row.setCreatedAt(user.getCreatedAt());
        return row;
    }
}
