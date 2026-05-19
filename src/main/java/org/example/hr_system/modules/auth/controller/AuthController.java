package org.example.hr_system.controller;

import lombok.RequiredArgsConstructor;
import org.example.hr_system.common.result.Result;
import org.example.hr_system.modules.auth.dto.LoginDTO;
import org.example.hr_system.service.AuthService;
import org.example.hr_system.modules.auth.vo.LoginVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        return authService.login(dto);
    }
}