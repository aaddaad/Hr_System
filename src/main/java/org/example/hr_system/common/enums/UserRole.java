package org.example.hr_system.common.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    RECRUITER("招聘管理员"),
    APPROVER("审批人"),
    VISITOR("普通访客");

    private final String desc;

    UserRole(String desc) {
        this.desc = desc;
    }
}