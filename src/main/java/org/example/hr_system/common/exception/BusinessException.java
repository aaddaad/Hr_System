package org.example.hr_system.common.exception;

import org.example.hr_system.common.enums.ResultCode;

public class BusinessException extends RuntimeException {
    private Integer code;

    public BusinessException(ResultCode code) {
        super(code.getMsg());
        this.code = code.getCode();
    }

    public BusinessException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}