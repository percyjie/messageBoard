package com.percy.messageboard.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public void handleMaxUploadSize(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.warn("上传文件超过大小限制");
        response.sendRedirect(request.getContextPath() + "/user/avatar?error=size");
    }
}
