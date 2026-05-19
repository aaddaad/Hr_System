package org.example.hr_system.modules.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class ExcelImportVO {
    private int successCount;
    private int failCount;
    private List<String> errors;
}