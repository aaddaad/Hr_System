package org.example.hr_system.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class PositionExcel {
    @ExcelProperty(index = 0)
    private String title;

    @ExcelProperty(index = 1)
    private String description;

    @ExcelProperty(index = 2)
    private String status;
}