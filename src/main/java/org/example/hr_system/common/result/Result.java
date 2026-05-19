package org.example.hr_system.common.result;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> success() {
        return result(200, "操作成功", null);
    }

    public static <T> Result<T> success(T data) {
        return result(200, "操作成功", data);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return result(code, message, null);
    }

    private static <T> Result<T> result(Integer code, String message, T data) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMessage(message);
        r.setData(data);
        return r;
    }
}