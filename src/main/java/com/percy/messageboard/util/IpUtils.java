package com.percy.messageboard.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import java.util.List;

public final class IpUtils {

    private static final List<String> IP_HEADERS =
            List.of("X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP");

    private IpUtils() {
    }

    public static String clientIp(HttpServletRequest request) {
        for (String header : IP_HEADERS) {
            String value = request.getHeader(header);
            if (StringUtils.hasText(value) && !"unknown".equalsIgnoreCase(value)) {
                String ip = value.split(",")[0].trim();
                return normalizeAndTruncate(ip);
            }
        }
        return normalizeAndTruncate(request.getRemoteAddr());
    }

    private static String normalizeAndTruncate(String ip) {
        if (!StringUtils.hasText(ip)) {
            return "unknown";
        }
        String value = ip;
        if (value.startsWith("::ffff:")) {
            value = value.substring(7);
        }
        if ("0:0:0:0:0:0:0:1".equals(value) || "::1".equals(value)) {
            value = "127.0.0.1";
        }
        return value.length() > 45 ? value.substring(0, 45) : value;
    }
}
