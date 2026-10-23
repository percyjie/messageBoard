package com.percy.messageboard;

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
}
