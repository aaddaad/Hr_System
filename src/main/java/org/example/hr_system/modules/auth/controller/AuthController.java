package org.example.hr_system.modules.auth.controller;

import org.example.hr_system.common.result.Result;
import org.example.hr_system.modules.auth.vo.LoginVO;
import org.example.hr_system.entity.SysUser;
import org.example.hr_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    // ===================== 登录接口（修复版！） =====================
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody SysUser user) { // 这里改了！
        LoginVO loginVO = userService.login(user.getUsername(), user.getPassword());
        return Result.success(loginVO);
    }

    // ===================== 注册接口（正常） =====================
    @PostMapping("/register")
    public Result register(@RequestBody SysUser user) {
        return userService.register(user);
    }
}