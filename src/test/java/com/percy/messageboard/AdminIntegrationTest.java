package com.percy.messageboard;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.percy.messageboard.entity.Message;
import com.percy.messageboard.mapper.MessageMapper;
import com.percy.messageboard.util.SessionKeys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    void adminCanLoginAndOpenUserManagement() throws Exception {
        MvcResult result = mockMvc.perform(post("/admin/login")
                        .param("email", "percyjie@gmail.com")
                        .param("password", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"))
                .andReturn();

        MockHttpSession adminSession = (MockHttpSession) result.getRequest().getSession(false);
        assertThat(adminSession.getAttribute(SessionKeys.ADMIN)).isNotNull();

        mockMvc.perform(get("/admin/users").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attributeExists("users", "page"));

        mockMvc.perform(get("/admin/users/2/messages").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user-messages"));
    }

    @Test
    void normalUserCannotLoginToAdminArea() throws Exception {
        String email = "normal-" + UUID.randomUUID() + "@example.com";
        mockMvc.perform(post("/user/register")
                        .param("name", "普通用户")
                        .param("email", email)
                        .param("password", "12345678"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/admin/login")
                        .param("email", email)
                        .param("password", "12345678"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"))
                .andExpect(model().attribute("error", "邮箱或密码错误，或该账号没有管理员权限"));
    }

    @Test
    void adminPagesRequireLogin() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/login*"));
    }

    @Test
    void adminCanDeleteAnonymousMessageFromHome() throws Exception {
        mockMvc.perform(post("/messages").param("content", "待删除的匿名留言"))
                .andExpect(status().is3xxRedirection());

        Message message = messageMapper.selectOne(new LambdaQueryWrapper<Message>()
                .eq(Message::getContent, "待删除的匿名留言")
                .last("LIMIT 1"));
        assertThat(message).isNotNull();
        assertThat(message.getUserId()).isNull();

        MvcResult login = mockMvc.perform(post("/admin/login")
                        .param("email", "percyjie@gmail.com")
                        .param("password", "123456"))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        MockHttpSession adminSession = (MockHttpSession) login.getRequest().getSession(false);

        mockMvc.perform(post("/admin/messages/{id}/delete", message.getId())
                        .session(adminSession)
                        .param("page", "1"))
                .andExpect(status().is3xxRedirection());

        assertThat(messageMapper.selectById(message.getId())).isNull();
    }
}
