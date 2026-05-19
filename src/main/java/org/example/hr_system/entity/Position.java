package org.example.hr_system.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Position {
    private Long id;
    private String title;       // 岗位名称（必填）
    private String description; // 岗位描述
    private String status;      // 状态 DRAFT/PENDING/PUBLISHED/CLOSED
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}