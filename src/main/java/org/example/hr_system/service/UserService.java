package org.example.hr_system.service;

import org.example.hr_system.common.result.Result;
import org.example.hr_system.entity.SysUser;
import org.example.hr_system.modules.auth.vo.LoginVO;

public interface UserService {

    // 登录
    LoginVO login(String username, String password);

    // 注册 ← 加上这一行！
    Result register(SysUser user);
}