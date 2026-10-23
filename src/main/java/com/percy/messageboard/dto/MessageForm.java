package com.percy.messageboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageForm {

    @NotBlank(message = "留言内容不能为空")
    @Size(max = 255, message = "留言内容不能超过 255 个字符")
    private String content;
}
