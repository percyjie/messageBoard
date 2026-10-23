package com.percy.messageboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.percy.messageboard.dto.AdminUserForm;
import com.percy.messageboard.dto.ProfileForm;
import com.percy.messageboard.dto.RegisterForm;
import com.percy.messageboard.dto.SessionUser;
import com.percy.messageboard.dto.UserRow;
import com.percy.messageboard.entity.Message;
import com.percy.messageboard.entity.User;
import com.percy.messageboard.mapper.MessageMapper;
import com.percy.messageboard.mapper.UserMapper;
import com.percy.messageboard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final MessageMapper messageMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public SessionUser register(RegisterForm form) {
        ensureEmailAvailable(form.getEmail(), null);
        User user = new User();
        user.setRole(User.ROLE_USER);
        user.setName(form.getName().trim());
        user.setEmail(normalizeEmail(form.getEmail()));
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        insert(user);
        return toSessionUser(user);
    }

    @Override
    public SessionUser authenticate(String email, String rawPassword) {
        User user = findByEmail(email);
        if (user == null) {
            return null;
        }
        boolean matches;
        try {
            matches = passwordEncoder.matches(rawPassword, user.getPassword());
        } catch (IllegalArgumentException e) {
            return null;
        }
        return matches ? toSessionUser(user) : null;
    }

    @Override
    public SessionUser authenticateAdmin(String email, String rawPassword) {
        SessionUser user = authenticate(email, rawPassword);
        return user != null && user.isAdmin() ? user : null;
    }

    @Override
    public long count() {
        return userMapper.selectCount(null);
    }

    @Override
    public Page<UserRow> pageUsers(long current, long size) {
        Page<User> page = queryUserPage(Math.max(current, 1), size);
        if (page.getPages() > 0 && page.getCurrent() > page.getPages()) {
            page = queryUserPage(page.getPages(), size);
        }
        Page<UserRow> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(this::toRow).toList());
        return result;
    }

    @Override
    public User require(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在");
        }
        return user;
    }

    @Override
    public SessionUser snapshot(Long id) {
        return toSessionUser(require(id));
    }

    @Override
    @Transactional
    public SessionUser updateProfile(Long id, ProfileForm form) {
        User user = require(id);
        ensureEmailAvailable(form.getEmail(), id);
        user.setName(form.getName().trim());
        user.setEmail(normalizeEmail(form.getEmail()));
        if (StringUtils.hasText(form.getPassword())) {
            user.setPassword(passwordEncoder.encode(form.getPassword()));
        }
        update(user);
        return toSessionUser(user);
    }

    @Override
    @Transactional
    public SessionUser updateAvatar(Long id, String filename) {
        User user = require(id);
        user.setAvatar(filename);
        userMapper.updateById(user);
        return toSessionUser(user);
    }

    @Override
    @Transactional
    public void createAdmin(AdminUserForm form) {
        if (!StringUtils.hasText(form.getPassword())) {
            throw new IllegalArgumentException("请为新管理员设置密码");
        }
        ensureEmailAvailable(form.getEmail(), null);
        User user = new User();
        user.setRole(User.ROLE_ADMIN);
        user.setName(form.getName().trim());
        user.setEmail(normalizeEmail(form.getEmail()));
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        insert(user);
    }

    @Override
    @Transactional
    public void adminUpdate(Long id, AdminUserForm form) {
        User user = require(id);
        ensureEmailAvailable(form.getEmail(), id);
        user.setName(form.getName().trim());
        user.setEmail(normalizeEmail(form.getEmail()));
        if (StringUtils.hasText(form.getPassword())) {
            user.setPassword(passwordEncoder.encode(form.getPassword()));
        }
        update(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id, Long actingAdminId) {
        if (id.equals(actingAdminId)) {
            throw new IllegalArgumentException("不能删除当前登录的管理员账号");
        }
        require(id);
        messageMapper.delete(new LambdaQueryWrapper<Message>().eq(Message::getUserId, id));
        userMapper.deleteById(id);
    }

    @Override
    public boolean emailTaken(String email, Long excludeId) {
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<User>()
                .eq(User::getEmail, normalizeEmail(email));
        if (excludeId != null) {
            query.ne(User::getId, excludeId);
        }
        return userMapper.selectCount(query) > 0;
    }

    @Override
    public SessionUser toSessionUser(User user) {
        return new SessionUser(user.getId(), user.getName(), user.getEmail(), user.getAvatar(), user.getRole());
    }

    private Page<User> queryUserPage(long current, long size) {
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<User>()
                .select(User::getId, User::getRole, User::getName, User::getEmail,
                        User::getAvatar, User::getCreatedAt)
                .orderByAsc(User::getId);
        userMapper.selectPage(page, query);
        return page;
    }

    private User findByEmail(String email) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, normalizeEmail(email))
                .last("LIMIT 1"));
    }

    private void ensureEmailAvailable(String email, Long excludeId) {
        if (emailTaken(email, excludeId)) {
            throw new IllegalArgumentException("该邮箱已被使用");
        }
    }

    private void insert(User user) {
        try {
            userMapper.insert(user);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("该邮箱已被使用");
        }
    }

    private void update(User user) {
        try {
            userMapper.updateById(user);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("该邮箱已被使用");
        }
    }

    private UserRow toRow(User user) {
        return UserRow.from(user);
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}
