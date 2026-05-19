package org.example.hr_system.modules.auth.vo;

import lombok.Data;
import java.util.List;

@Data
public class UserInfoVO {
    private Long userId;
    private String username;
    private String realName;
    private List<String> roleList;
}