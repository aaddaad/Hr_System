package org.example.hr_system.modules.auth.vo;

import lombok.Data;

@Data // 自动生成getter/setter
public class LoginVO {
    private Long userId;
    private String username;
    private String realName;
    private String role;
    private String token;
}