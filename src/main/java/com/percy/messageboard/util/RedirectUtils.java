package com.percy.messageboard.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

public final class RedirectUtils {

    private RedirectUtils() {
    }

    /**
     * Only allow same-site absolute paths, so a crafted ?redirect= cannot send users to another host.
     */
    public static String safe(String target, String fallback) {
        if (!StringUtils.hasText(target)) {
            return fallback;
        }
        String value = target.trim();
        if (value.length() > 500
                || !value.startsWith("/")
                || value.startsWith("//")
                || value.contains("\\")
                || value.contains("\r")
                || value.contains("\n")
                || value.contains("://")) {
            return fallback;
        }
        return value;
    }

    public static String currentPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (StringUtils.hasText(contextPath) && uri.startsWith(contextPath)) {
            uri = uri.substring(contextPath.length());
        }
        if (!StringUtils.hasText(uri)) {
            uri = "/";
        }
        String query = request.getQueryString();
        return StringUtils.hasText(query) ? uri + "?" + query : uri;
    }
}
