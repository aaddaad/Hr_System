package org.example.hr_system.common.config;

import org.example.hr_system.common.interceptor.RoleInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 注册角色权限拦截器
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RoleInterceptor roleInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册角色拦截器，拦截所有/webapi/positions路径
        registry.addInterceptor(roleInterceptor)
                .addPathPatterns("/webapi/positions/**")
                // 排除登录接口
                .excludePathPatterns("/api/v1/auth/**");
    }
}
