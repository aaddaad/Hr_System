package org.example.hr_system.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hr_system.common.enums.ResultCode;
import org.example.hr_system.common.exception.BusinessException;
import org.example.hr_system.common.result.Result;
import org.example.hr_system.modules.auth.dto.LoginDTO;
import org.example.hr_system.entity.SysUser;
import org.example.hr_system.service.AuthService;
import org.example.hr_system.modules.auth.vo.LoginVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Result<LoginVO> login(LoginDTO dto) {
        String sql = "select id, username, password, real_name, status, role, create_time from sys_user where username = ?";
        List<SysUser> userList = jdbcTemplate.query(sql,
                new Object[]{dto.getUsername()},
                (rs, rowNum) -> {
                    String realNameValue = rs.getString("real_name");
                    log.debug("RowMapper - real_name column value: [{}]", realNameValue);
                    SysUser user = new SysUser();
                    user.setId(rs.getLong("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setRealName(realNameValue);
                    user.setStatus(rs.getInt("status"));
                    user.setRole(org.example.hr_system.common.enums.UserRole.valueOf(rs.getString("role")));
                    user.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
                    return user;
                });

        // 无用户
        if (userList.isEmpty()) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        SysUser user = userList.get(0);
        log.debug("After mapping - user.getRealName(): [{}]", user.getRealName());
        // 密码校验
        if (!dto.getPassword().equals(user.getPassword())) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        // 状态禁用
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        LoginVO vo = new LoginVO();
        vo.setToken("test-token-" + user.getId());
        vo.setUserId(user.getId());
        vo.setRealName(user.getRealName());
        vo.setRole(user.getRole() != null ? user.getRole().name() : "VISITOR");
        return Result.success(vo);
    }
}