package com.percy.messageboard.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginForm {

    @NotBlank(message = "请输入邮箱")
    @Email(message = "邮箱格式不正确")
    @Size(max = 190, message = "邮箱过长")
    private String email;

    @NotBlank(message = "请输入密码")
    @Size(max = 20, message = "密码长度不能超过 20 位")
    private String password;
}
