package org.example.hr_system.common.enums;

public enum ResultCode {
    PARAM_ERROR(400, "参数错误"),
    FORBIDDEN(403, "无权访问"),
    USER_NOT_FOUND(400, "用户名或密码错误");

    private final Integer code;
    private final String msg;

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}