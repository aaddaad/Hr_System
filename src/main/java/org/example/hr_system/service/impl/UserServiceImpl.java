package org.example.hr_system.service.impl;

import org.example.hr_system.common.result.Result;
import org.example.hr_system.entity.SysUser;
import org.example.hr_system.modules.auth.vo.LoginVO;
import org.example.hr_system.common.mapper.UserMapper;
import org.example.hr_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    // ===================== 登录（绝对不报错！） =====================
    @Override
    public LoginVO login(String username, String password) {
        // 1. 查询用户
        SysUser user = userMapper.selectByUsername(username);

        // 2. 用户不存在
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 3. 密码错误
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("密码错误");
        }

        // 4. 只赋值 不报错 的字段！！！
        LoginVO vo = new LoginVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());

        // role 我们先不加！避免报错！
        // vo.setRole(xxx); <==== 这行彻底删掉！

        return vo;
    }

    // ===================== 注册（不动） =====================
    @Override
    public Result register(SysUser user) {

        System.out.println("========================================");
        System.out.println("前端传入用户名：" + user.getUsername());
        System.out.println("前端传入密码：" + user.getPassword());
        System.out.println("前端传入真实姓名：" + user.getRealName());
        System.out.println("========================================");

        SysUser exist = userMapper.selectByUsername(user.getUsername());
        System.out.println("查询到用户：" + exist);

        if (exist != null) {
            return Result.error(500, "用户名已存在");
        }

        int rows = userMapper.insert(user);
        System.out.println("插入数据库行数：" + rows);

        if (rows > 0) {
            return Result.success("注册成功");
        } else {
            return Result.error(500, "注册失败");
        }
    }
}