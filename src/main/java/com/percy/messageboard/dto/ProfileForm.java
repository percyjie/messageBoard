package com.percy.messageboard.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfileForm {

    @NotBlank(message = "请输入昵称")
    @Size(min = 2, max = 20, message = "昵称长度需为 2-20 个字符")
    private String name;

    @NotBlank(message = "请输入邮箱")
    @Email(message = "邮箱格式不正确")
    @Size(max = 190, message = "邮箱过长")
    private String email;

    @Pattern(regexp = "^$|^.{6,20}$", message = "密码长度需为 6-20 位")
    private String password;

    public static ProfileForm of(String name, String email) {
        ProfileForm form = new ProfileForm();
        form.setName(name);
        form.setEmail(email);
        form.setPassword("");
        return form;
    }
}
