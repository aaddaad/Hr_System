package org.example.hr_system.service;

import org.example.hr_system.common.result.Result;
import org.example.hr_system.entity.Position;
import org.example.hr_system.modules.auth.vo.ExcelImportVO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface PositionService {
    Result<List<Position>> list(String keyword, String status);
    Result<Position> getById(Long id);
    Result<Void> add(Position position);
    Result<Void> update(Position position);
    Result<Void> delete(Long id);
    Result<Void> submitApproval(Long id);
    Result<Void> approve(Long id, String action, String comment);
    Result<Void> closePosition(Long id);
    // Excel导入（最终版）
    Result<ExcelImportVO> uploadExcel(MultipartFile file);
}