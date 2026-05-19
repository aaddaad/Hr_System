package org.example.hr_system.entity;

import lombok.Data;
import org.example.hr_system.common.enums.UserRole;
import java.time.LocalDateTime;

@Data
public class SysUser {
    private Long id;
    private String username;
    private String password;
    private String realName;
    private Integer status;
    private UserRole role;  // 用户角色：RECRUITER/APPROVER/VISITOR
    private LocalDateTime createTime;
}