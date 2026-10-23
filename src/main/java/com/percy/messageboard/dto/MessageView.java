package com.percy.messageboard.dto;

import com.percy.messageboard.entity.User;
import com.percy.messageboard.util.TimeUtils;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageView {

    private Long id;
    private Long userId;
    private String authorName;
    private String avatar;
    private Integer role;
    private String content;
    private String ip;
    private LocalDateTime createdAt;

    public boolean isAdmin() {
        return role != null && role == User.ROLE_ADMIN;
    }

    public String getCreatedAtText() {
        return TimeUtils.format(createdAt);
    }

    public String getCreatedAtIso() {
        return TimeUtils.iso(createdAt);
    }

    public String getAuthorInitial() {
        return authorName == null || authorName.isEmpty() ? "?" : authorName.substring(0, 1);
    }
}
