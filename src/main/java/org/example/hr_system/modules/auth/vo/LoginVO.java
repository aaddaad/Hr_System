package org.example.hr_system.modules.auth.vo;

import lombok.Data;

@Data
public class LoginVO {
    private String token;       // JWT令牌
    private Long userId;        // 用户ID
    private String realName;    // 真实姓名
    private String role;        // 角色编码
}