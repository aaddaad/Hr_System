package org.example.hr_system.common.enums;

/**
 * 用户角色枚举
 */
public enum UserRole {
    /**
     * 招聘管理员：创建、编辑、删除、批量导入岗位；查看岗位列表与详情；发起审批
     */
    RECRUITER,

    /**
     * 审批人：对处于"待审批"状态的岗位进行审批（通过/驳回）
     */
    APPROVER,

    /**
     * 普通访客/查看者：可浏览已发布的岗位（无需登录）
     */
    VISITOR
}
