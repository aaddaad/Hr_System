package org.example.hr_system.common.enums;

import lombok.Getter;

@Getter
public enum RoleEnum {
    RECRUITER("招聘管理员", "RECRUITER"),
    APPROVER("审批人", "APPROVER"),
    VISITOR("普通访客", "VISITOR");

    private final String desc;
    private final String code;

    RoleEnum(String desc, String code) {
        this.desc = desc;
        this.code = code;
    }

    public static RoleEnum getByCode(String code) {
        for (RoleEnum role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("无效的角色: " + code);
    }
}