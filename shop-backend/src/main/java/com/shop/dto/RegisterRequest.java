package com.shop.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @Size(min = 6, max = 20, message = "用户名长度为6-20个字符")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]*$", message = "用户名必须以字母开头，且仅包含字母、数字和下划线")
    private String username;

    @Size(min = 8, max = 16, message = "密码长度为8-16个字符")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&*]).+$",
             message = "密码必须同时包含大写字母、小写字母、数字和特殊字符(@#$%^&*)")
    private String password;

    @Size(min = 8, max = 16, message = "密码长度为8-16个字符")
    private String confirmPassword;
}
