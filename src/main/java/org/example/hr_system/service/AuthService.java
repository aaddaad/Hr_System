package org.example.hr_system.service;

import org.example.hr_system.common.result.Result;
import org.example.hr_system.modules.auth.dto.LoginDTO;
import org.example.hr_system.modules.auth.vo.LoginVO;

public interface AuthService {
    Result<LoginVO> login(LoginDTO dto);
}