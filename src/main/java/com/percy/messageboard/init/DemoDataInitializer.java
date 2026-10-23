package com.percy.messageboard.init;

import com.percy.messageboard.entity.Message;
import com.percy.messageboard.entity.User;
import com.percy.messageboard.mapper.MessageMapper;
import com.percy.messageboard.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.demo-data", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DemoDataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final MessageMapper messageMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userMapper.selectCount(null) > 0) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        User admin = seedUser("彼得潘", "percyjie@gmail.com", "123456",
                User.ROLE_ADMIN, now.minusDays(30));
        User qiu = seedUser("邱莹莹", "licyun@qq.com", "12345678",
                User.ROLE_USER, now.minusDays(20));
        User guan = seedUser("关关", "licyun@gmail.com", "12345678",
                User.ROLE_USER, now.minusDays(15));
        User qu = seedUser("曲筱绡", "licyun@163.com", "12345678",
                User.ROLE_USER, now.minusDays(10));

        seedMessage(admin, "欢迎来到新版留言板。这里保留了原来的功能，也换上了更清爽的界面。",
                "119.96.0.1", "湖北省", now.minusDays(9));
        seedMessage(qiu, "这个界面比十年前清爽多了，看起来舒服很多。",
                "113.108.0.1", "广东省", now.minusDays(6));
        seedMessage(guan, "有人一起研究 Spring Boot 吗？最近在试着把老项目翻新。",
                "123.125.0.1", "北京市", now.minusDays(4));
        seedMessage(qu, "最近在整理旧代码。\n把 JSP 换成 Thymeleaf 之后，页面维护轻松了不少，模板也更好读。",
                "115.236.0.1", "浙江省", now.minusDays(2));
        seedMessage(qiu, "今天也要好好写代码。",
                "113.108.0.1", "广东省", now.minusHours(5));
        seedMessage(admin, "留言会自动记录时间和来源 IP，匿名访客也可以浏览全部内容。",
                "119.96.0.1", "湖北省", now.minusMinutes(40));
        seedMessage(null, "路过留个言，匿个名。",
                "113.108.0.1", "广东省", now.minusMinutes(12));
        seedMessage(guan, "第一条留言来自 2016 年，新的故事从这里开始。",
                "127.0.0.1", "本地", now.minusMinutes(3));

        log.info("演示数据已初始化，管理员账号: percyjie@gmail.com / 123456");
    }

    private User seedUser(String name, String email, String rawPassword, int role, LocalDateTime createdAt) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setCreatedAt(createdAt);
        userMapper.insert(user);
        return user;
    }

    private void seedMessage(User author, String content, String ip, String province,
                             LocalDateTime createdAt) {
        Message message = new Message();
        message.setUserId(author == null ? null : author.getId());
        message.setContent(content);
        message.setIp(ip);
        message.setProvince(province);
        message.setCreatedAt(createdAt);
        messageMapper.insert(message);
    }
}
