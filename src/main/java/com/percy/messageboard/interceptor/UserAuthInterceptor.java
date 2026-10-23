package com.percy.messageboard.interceptor;

import com.percy.messageboard.util.RedirectUtils;
import com.percy.messageboard.util.SessionKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class UserAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(SessionKeys.USER) != null) {
            return true;
        }
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return false;
        }
        String target = URLEncoder.encode(RedirectUtils.currentPath(request), StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath() + "/user/login?redirect=" + target);
        return false;
    }
}
