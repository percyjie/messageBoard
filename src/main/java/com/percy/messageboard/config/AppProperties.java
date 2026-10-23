package com.percy.messageboard.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private int pageSize = 5;
    private String uploadDir = "./uploads";
    private DemoData demoData = new DemoData();

    public Path uploadPath() {
        return Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Getter
    @Setter
    public static class DemoData {
        private boolean enabled = true;
    }
}
