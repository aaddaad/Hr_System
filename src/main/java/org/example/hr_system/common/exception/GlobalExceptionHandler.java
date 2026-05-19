package org.example.hr_system.common.exception;

import org.example.hr_system.common.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    // 404 资源不存在
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleNotFound(IllegalArgumentException e) {
        return Result.error(404, e.getMessage());
    }

    // 400 参数错误
    @ExceptionHandler(NullPointerException.class)
    public Result<Void> handleNull() {
        return Result.error(400, "参数不能为空");
    }

    // 500 服务器异常
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        return Result.error(500, "服务器异常：" + e.getMessage());
    }
}