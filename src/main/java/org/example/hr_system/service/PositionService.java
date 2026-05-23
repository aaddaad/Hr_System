package org.example.hr_system.service;

import org.example.hr_system.entity.Position;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface PositionService {

    // 1. 条件查询岗位列表（关键词+状态+角色权限过滤）
    List<Position> getPositionList(String keyword, String status, String userRole);

    // 2. 根据id查询单个岗位
    Position getPositionById(Long id);

    // 3. 新增岗位
    void addPosition(Position position);

    // 4. 修改岗位
    void updatePosition(Position position);

    // 5. 删除岗位（带状态校验）
    void deletePosition(Long id);

    // 6. 提交审批 DRAFT→PENDING
    void submitApproval(Long id);

    // 7. 审批操作 PENDING→通过/驳回
    void approvePosition(Long id, boolean pass);

    // 8. 关闭岗位 PUBLISHED→CLOSED
    void closePosition(Long id);

    // 9. Excel批量导入岗位（需求文档管理员功能）
    String importPositionData(MultipartFile file);
}