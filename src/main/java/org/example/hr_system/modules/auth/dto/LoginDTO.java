package org.example.hr_system.modules.auth.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String username;
    private String password;
}