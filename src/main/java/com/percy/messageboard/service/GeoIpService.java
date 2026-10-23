package com.percy.messageboard.service;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.service.Config;
import org.lionsoul.ip2region.service.Ip2Region;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Slf4j
@Service
public class GeoIpService {

    private static final String UNKNOWN = "未知";
    private static final String LOCAL = "本地";

    private final Ip2Region ip2Region;

    public GeoIpService() {
        Ip2Region service = null;
        try (InputStream input = new ClassPathResource("ip2region/ip2region_v4.xdb").getInputStream()) {
            Config config = Config.custom()
                    .setCachePolicy(Config.BufferCache)
                    .setXdbInputStream(input)
                    .asV4();
            service = Ip2Region.create(config, null);
            log.info("IP 归属地库加载成功");
        } catch (Exception e) {
            log.warn("IP 归属地库加载失败，省份将显示为未知", e);
        }
        this.ip2Region = service;
    }

    public String resolveProvince(String ip) {
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            return UNKNOWN;
        }
        String value = ip.trim();
        if (isLoopback(value)) {
            return LOCAL;
        }
        if (isPrivate(value)) {
            return "内网";
        }
        if (value.indexOf(':') >= 0) {
            return UNKNOWN;
        }
        if (ip2Region == null) {
            return UNKNOWN;
        }
        try {
            return parseProvince(ip2Region.search(value));
        } catch (Exception e) {
            log.debug("IP 归属地解析失败: {}", value, e);
            return UNKNOWN;
        }
    }

    private String parseProvince(String region) {
        if (region == null || region.isBlank()) {
            return UNKNOWN;
        }
        // Bundled v4 xdb format: country|province|city|isp|countryCode
        String[] parts = region.split("\\|", -1);
        if (parts.length < 2) {
            return UNKNOWN;
        }
        String country = clean(parts[0]);
        String province = clean(parts[1]);
        if (!province.isEmpty()) {
            return province;
        }
        if (!country.isEmpty() && !"中国".equals(country)) {
            return country;
        }
        return UNKNOWN;
    }

    private String clean(String value) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        return isUnknown(trimmed) ? "" : trimmed;
    }

    private boolean isUnknown(String value) {
        return value == null || value.isBlank() || "0".equals(value) || UNKNOWN.equals(value);
    }

    private boolean isLoopback(String ip) {
        return "localhost".equalsIgnoreCase(ip)
                || "127.0.0.1".equals(ip)
                || "0:0:0:0:0:0:0:1".equals(ip)
                || "::1".equals(ip);
    }

    private boolean isPrivate(String ip) {
        if (ip.indexOf(':') >= 0) {
            return false;
        }
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        try {
            int first = Integer.parseInt(parts[0]);
            int second = Integer.parseInt(parts[1]);
            if (first == 10) {
                return true;
            }
            if (first == 172 && second >= 16 && second <= 31) {
                return true;
            }
            if (first == 192 && second == 168) {
                return true;
            }
            if (first == 169 && second == 254) {
                return true;
            }
            return first == 100 && second >= 64 && second <= 127;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @PreDestroy
    public void close() {
        if (ip2Region != null) {
            try {
                ip2Region.close();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
