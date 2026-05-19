package org.example.hr_system.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.hr_system.common.enums.ApprovalAction;
import org.example.hr_system.common.enums.PositionStatus;
import org.example.hr_system.common.exception.BusinessException;
import org.example.hr_system.common.listener.PositionExcelListener;
import org.example.hr_system.common.result.Result;
import org.example.hr_system.entity.Position;
import org.example.hr_system.entity.PositionExcel;
import org.example.hr_system.modules.auth.vo.ExcelImportVO;
import org.example.hr_system.service.PositionService;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Result<List<Position>> list(String keyword, String status) {
        String sql = "SELECT * FROM position WHERE 1=1 ";
        if (StringUtils.hasText(keyword)) {
            sql += "AND title LIKE '%" + keyword + "%' ";
        }
        if (StringUtils.hasText(status)) {
            sql += "AND status = '" + status.toUpperCase() + "' ";
        }
        sql += "ORDER BY create_time DESC";
        List<Position> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Position.class));
        return Result.success(list);
    }

    @Override
    public Result<Position> getById(Long id) {
        try {
            String sql = "SELECT * FROM position WHERE id = ?";
            Position position = jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper<>(Position.class));
            return Result.success(position);
        } catch (Exception e) {
            throw new IllegalArgumentException("岗位不存在：id=" + id);
        }
    }

    @Override
    public Result<Void> add(Position position) {
        // 严格匹配测试用例校验
        if (position.getTitle() == null || position.getTitle().trim().isEmpty()) {
            throw new BusinessException(400, "岗位名称不能为空");
        }
        if (position.getTitle().length() > 100) {
            throw new BusinessException(400, "岗位名称长度不能超过100字符");
        }
        // 状态小写自动转大写
        String status = position.getStatus();
        if (!StringUtils.hasText(status)) {
            position.setStatus(PositionStatus.DRAFT.name());
        } else {
            try {
                position.setStatus(PositionStatus.valueOf(status.toUpperCase()).name());
            } catch (Exception e) {
                throw new BusinessException(400, "状态值无效");

                
            }
        }
        String sql = "INSERT INTO `position`(`title`,`description`,`status`,`create_time`,`update_time`) VALUES (?,?,?,?,?)";
        jdbcTemplate.update(sql, position.getTitle(), position.getDescription(), position.getStatus(), LocalDateTime.now(), LocalDateTime.now());
        return Result.success();
    }

    @Override
    public Result<Void> update(Position position) {
        if (position.getId() == null) throw new BusinessException(400, "岗位ID不能为空");
        Position old = getById(position.getId()).getData();
        // 状态约束：CLOSED不能直接改为PUBLISHED
        if (PositionStatus.CLOSED.name().equals(old.getStatus()) && PositionStatus.PUBLISHED.name().equals(position.getStatus())) {
            throw new BusinessException(400, "已关闭岗位需重新提交审批，不可直接发布");
        }
        // 已发布编辑 → 变回草稿
        if (PositionStatus.PUBLISHED.name().equals(old.getStatus())) {
            position.setStatus(PositionStatus.DRAFT.name());
        }
        String sql = "UPDATE position SET title=?,description=?,status=?,update_time=? WHERE id=?";
        jdbcTemplate.update(sql, position.getTitle(), position.getDescription(), position.getStatus(), LocalDateTime.now(), position.getId());
        return Result.success();
    }

    @Override
    public Result<Void> delete(Long id) {
        Position position = getById(id).getData();
        if (PositionStatus.PUBLISHED.name().equals(position.getStatus())) {
            throw new BusinessException(400, "已发布的岗位不可直接删除，请先关闭");
        }
        jdbcTemplate.update("DELETE FROM position WHERE id=?", id);
        return Result.success();
    }

    // 提交审批 DRAFT → PENDING
    @Override
    public Result<Void> submitApproval(Long id) {
        Position p = getById(id).getData();
        if (!PositionStatus.DRAFT.name().equals(p.getStatus())) {
            throw new BusinessException(400, "只有草稿可提交审批");
        }
        jdbcTemplate.update("UPDATE position SET status=?,update_time=? WHERE id=?", PositionStatus.PENDING.name(), LocalDateTime.now(), id);
        return Result.success();
    }

    // 审批（支持备注）
    @Override
    public Result<Void> approve(Long id, String action, String comment) {
        Position p = getById(id).getData();
        if (!PositionStatus.PENDING.name().equals(p.getStatus())) {
            throw new BusinessException(400, "只有待审批状态的岗位才能进行审批操作");
        }
        try {
            ApprovalAction.valueOf(action);
        } catch (Exception e) {
            throw new BusinessException(400, "审批动作无效");
        }
        String newStatus = ApprovalAction.APPROVE.name().equals(action) ? PositionStatus.PUBLISHED.name() : PositionStatus.DRAFT.name();
        jdbcTemplate.update("UPDATE position SET status=?,update_time=? WHERE id=?", newStatus, LocalDateTime.now(), id);
        return Result.success();
    }

    // 关闭岗位 PUBLISHED → CLOSED
    @Override
    public Result<Void> closePosition(Long id) {
        Position p = getById(id).getData();
        if (!PositionStatus.PUBLISHED.name().equals(p.getStatus())) {
            throw new BusinessException(400, "仅已发布岗位可关闭");
        }
        jdbcTemplate.update("UPDATE position SET status=?,update_time=? WHERE id=?", PositionStatus.CLOSED.name(), LocalDateTime.now(), id);
        return Result.success();
    }

    // Excel上传（最简实现，匹配测试用例）
    // ====================== Excel 批量导入（完整实现） ======================
    @Override
    public Result<ExcelImportVO> uploadExcel(MultipartFile file) {
        try {
            // 文件类型校验
            String name = file.getOriginalFilename();
            if (name == null || (!name.endsWith(".xlsx") && !name.endsWith(".xls"))) {
                throw new BusinessException(415, "不支持的文件类型");
            }

            // 读取Excel（修复所有标红）
            PositionExcelListener listener = new PositionExcelListener(jdbcTemplate);

            // 极简写法，无报错
            com.alibaba.excel.EasyExcel.read(file.getInputStream(), PositionExcel.class, listener)
                    .sheet()
                    .doRead();

            ExcelImportVO vo = new ExcelImportVO(
                    listener.getSuccessCount(),
                    listener.getFailCount(),
                    listener.getErrors()
            );
            return Result.success(vo);

        } catch (Exception e) {
            throw new BusinessException(500, "导入失败");
        }
    }
}