package com.percy.messageboard.config;

import com.percy.messageboard.dto.SessionUser;
import com.percy.messageboard.util.SessionKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("currentUser")
    public SessionUser currentUser(HttpServletRequest request) {
        return sessionUser(request, SessionKeys.USER);
    }

    @ModelAttribute("currentAdmin")
    public SessionUser currentAdmin(HttpServletRequest request) {
        return sessionUser(request, SessionKeys.ADMIN);
    }

    private SessionUser sessionUser(HttpServletRequest request, String key) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(key);
        if (value instanceof SessionUser) {
            return (SessionUser) value;
        }
        return null;
    }
}
