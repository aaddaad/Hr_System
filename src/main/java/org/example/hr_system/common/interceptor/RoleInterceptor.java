package org.example.hr_system.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.hr_system.common.enums.UserRole;
import org.example.hr_system.common.exception.BusinessException;
import org.example.hr_system.entity.SysUser;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 角色权限拦截器
 * 根据用户角色控制接口访问权限
 */
@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头或Session中获取当前用户信息（简化实现）
        String userRole = request.getHeader("X-User-Role");
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // 如果没有角色信息，默认为访客
        if (userRole == null || userRole.isEmpty()) {
            userRole = "VISITOR";
        }

        // 访客权限：只能查看已发布的岗位列表
        if ("VISITOR".equals(userRole)) {
            // 访客只能访问 GET /webapi/positions（查询已发布岗位）
            if (uri.startsWith("/webapi/positions") && "GET".equals(method)) {
                // 允许访问，但需要在Service层过滤只返回PUBLISHED状态的岗位
                request.setAttribute("visitorFilter", true);
                return true;
            }
            // 其他接口拒绝访问
            throw new BusinessException(403, "访客无权限访问此接口");
        }

        // 审批人权限：只能进行审批操作
        if ("APPROVER".equals(userRole)) {
            // 审批人只能访问审批相关接口
            if (uri.contains("/approve") || uri.contains("/submit")) {
                return true;
            }
            // 审批人可以查看岗位列表和详情
            if (uri.startsWith("/webapi/positions") && ("GET".equals(method))) {
                return true;
            }
            throw new BusinessException(403, "审批人只能进行审批操作");
        }

        // 招聘管理员权限：所有操作
        if ("RECRUITER".equals(userRole)) {
            // 管理员不能进行审批操作（审批是审批人的职责）
            if (uri.contains("/approve")) {
                throw new BusinessException(403, "招聘管理员不能进行审批操作，请联系审批人");
            }
            return true;
        }

        // 未知角色，拒绝访问
        throw new BusinessException(403, "未知用户角色，无法访问");
    }
}
