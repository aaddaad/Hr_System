package org.example.hr_system.common.exception;

import org.example.hr_system.common.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 自定义业务异常 400
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    // 资源不存在异常 404 【修复测试用例500问题】
    @ExceptionHandler(ResourceNotFoundException.class)
    public Result<Void> handleNotFound(ResourceNotFoundException e){
        return Result.error(404,e.getMessage());
    }

    // 非法参数异常 400
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleParamErr(IllegalArgumentException e){
        return Result.error(400,e.getMessage());
    }

    // 空指针兜底
    @ExceptionHandler(NullPointerException.class)
    public Result<Void> handleNull(){
        return Result.error(400,"参数不能为空");
    }

    // 全局未知异常 500
    @ExceptionHandler(Exception.class)
    public Result<Void> handleAllException(Exception e){
        e.printStackTrace();
        return Result.error(500,"服务器内部异常，请稍后重试");
    }
}