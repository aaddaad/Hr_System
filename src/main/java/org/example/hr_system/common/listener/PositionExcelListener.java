package org.example.hr_system.common.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.Getter;
import org.example.hr_system.entity.PositionExcel;
import org.springframework.jdbc.core.JdbcTemplate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PositionExcelListener implements ReadListener<PositionExcel> {

    private final JdbcTemplate jdbcTemplate;
    @Getter
    private int successCount = 0;
    @Getter
    private int failCount = 0;
    @Getter
    private final List<String> errors = new ArrayList<>();

    public PositionExcelListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void invoke(PositionExcel data, AnalysisContext context) {
        try {
            // 1. 校验标题必填
            if (data.getTitle() == null || data.getTitle().trim().isEmpty()) {
                failCount++;
                errors.add("岗位名称不能为空");
                return;
            }

            // 2. 固定时间（解决无默认值问题）
            LocalDateTime now = LocalDateTime.now();

            // 3. 终极SQL：反引号表名 + 传入所有必填字段
            String sql = "INSERT INTO `position`(title,description,status,create_time,update_time) VALUES (?,?,?,?,?)";

            // 4. 执行插入
            jdbcTemplate.update(sql,
                    data.getTitle().trim(),
                    data.getDescription(),
                    "DRAFT",
                    now,
                    now
            );

            successCount++;
        } catch (Exception e) {
            errors.add(e.toString());
            failCount++;
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {}
}