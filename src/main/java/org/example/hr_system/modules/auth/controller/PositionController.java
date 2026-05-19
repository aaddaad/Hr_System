package org.example.hr_system.modules.auth.controller;

import lombok.RequiredArgsConstructor;
import org.example.hr_system.common.result.Result;
import org.example.hr_system.modules.auth.dto.ApprovalDTO;
import org.example.hr_system.entity.Position;
import org.example.hr_system.modules.auth.vo.ExcelImportVO;
import org.example.hr_system.service.PositionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/webapi/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    // 组合查询：关键词+状态 TC-API-001~004
    @GetMapping
    public Result<List<Position>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        return positionService.list(keyword, status);
    }

    // 查询单个 TC-API-005~006
    @GetMapping("/{id}")
    public Result<Position> getById(@PathVariable Long id) {
        return positionService.getById(id);
    }

    // 新增 TC-API-007~011
    @PostMapping
    public Result<Void> add(@RequestBody Position position) {
        return positionService.add(position);
    }

    // 修改 TC-API-012~014
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Position position) {
        position.setId(id);
        return positionService.update(position);
    }

    // 删除 TC-API-015~017
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return positionService.delete(id);
    }

    // 提交审批（新增：DRAFT → PENDING）
    @PostMapping("/{id}/submit")
    public Result<Void> submit(@PathVariable Long id) {
        return positionService.submitApproval(id);
    }

    // 审批通过/驳回 TC-API-018~021
    @PostMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id, @RequestBody ApprovalDTO dto) {
        return positionService.approve(id, dto.getAction(), dto.getComment());
    }

    // 关闭岗位（PUBLISHED → CLOSED）TC-FLOW-003
    @PostMapping("/{id}/close")
    public Result<Void> close(@PathVariable Long id) {
        return positionService.closePosition(id);
    }

    // Excel批量上传（最终版）
    @PostMapping("/upload")
    public Result<ExcelImportVO> upload(@RequestParam("file") MultipartFile file) {
        return positionService.uploadExcel(file);
    }
}