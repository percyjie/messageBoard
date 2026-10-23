package com.percy.messageboard.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.percy.messageboard.dto.AdminUserForm;
import com.percy.messageboard.dto.ProfileForm;
import com.percy.messageboard.dto.RegisterForm;
import com.percy.messageboard.dto.SessionUser;
import com.percy.messageboard.dto.UserRow;
import com.percy.messageboard.entity.User;

public interface UserService {

    SessionUser register(RegisterForm form);

    SessionUser authenticate(String email, String rawPassword);

    SessionUser authenticateAdmin(String email, String rawPassword);

    long count();

    Page<UserRow> pageUsers(long current, long size);

    User require(Long id);

    SessionUser snapshot(Long id);

    SessionUser updateProfile(Long id, ProfileForm form);

    SessionUser updateAvatar(Long id, String filename);

    void createAdmin(AdminUserForm form);

    void adminUpdate(Long id, AdminUserForm form);

    void deleteUser(Long id, Long actingAdminId);

    boolean emailTaken(String email, Long excludeId);

    SessionUser toSessionUser(User user);
}
