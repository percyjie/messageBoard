package com.percy.messageboard;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
@MapperScan("com.percy.messageboard.mapper")
public class MessageBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageBoardApplication.class, args);
    }
}
