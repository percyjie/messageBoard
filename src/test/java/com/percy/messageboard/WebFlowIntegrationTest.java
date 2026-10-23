package com.percy.messageboard;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.percy.messageboard.dto.SessionUser;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    void homeShowsFirstPageOfDemoMessages() throws Exception {
        MvcResult result = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andReturn();

        Page<?> page = (Page<?>) result.getModelAndView().getModel().get("page");
        List<?> messages = (List<?>) result.getModelAndView().getModel().get("messages");

        assertThat(page.getTotal()).isGreaterThanOrEqualTo(7L);
        assertThat(messages).hasSize(5);
    }

    @Test
    void protectedPagesRedirectAnonymousUsersToLogin() throws Exception {
        mockMvc.perform(get("/user/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/user/login*"));

        mockMvc.perform(post("/messages").param("content", "匿名留言"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/user/login*"));
    }

    @Test
    void registeredUserCanPostAndOnlyDeleteOwnMessage() throws Exception {
        MockHttpSession owner = register(uniqueEmail(), "用户甲", "123456");
        MockHttpSession stranger = register(uniqueEmail(), "用户乙", "123456");

        mockMvc.perform(post("/messages").session(owner).param("content", "这是一条测试留言"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        SessionUser ownerUser = (SessionUser) owner.getAttribute(SessionKeys.USER);
        List<Message> messages = messageMapper.selectList(new LambdaQueryWrapper<Message>()
                .eq(Message::getUserId, ownerUser.getId())
                .orderByDesc(Message::getId));
        assertThat(messages).isNotEmpty();
        Message message = messages.get(0);

        mockMvc.perform(post("/user/messages/{id}/delete", message.getId())
                        .session(stranger)
                        .param("page", "1"))
                .andExpect(status().isNotFound());
        assertThat(messageMapper.selectById(message.getId())).isNotNull();

        mockMvc.perform(post("/user/messages/{id}/delete", message.getId())
                        .session(owner)
                        .param("page", "1"))
                .andExpect(status().is3xxRedirection());
        assertThat(messageMapper.selectById(message.getId())).isNull();
    }

    @Test
    void messageContentIsEscaped() throws Exception {
        MockHttpSession session = register(uniqueEmail(), "测试用户", "123456");
        String payload = "<script>alert('xss')</script>";

        mockMvc.perform(post("/messages").session(session).param("content", payload))
                .andExpect(status().is3xxRedirection());

        String html = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(html).contains("&lt;script&gt;");
        assertThat(html).doesNotContain(payload);
    }

    private MockHttpSession register(String email, String name, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/user/register")
                        .param("name", name)
                        .param("email", email)
                        .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession(false);
    }

    private String uniqueEmail() {
        return "user-" + UUID.randomUUID() + "@example.com";
    }
}
