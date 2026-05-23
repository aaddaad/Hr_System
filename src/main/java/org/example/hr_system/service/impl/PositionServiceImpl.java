package org.example.hr_system.service.impl;

import org.example.hr_system.common.enums.PositionStatus;
import org.example.hr_system.common.enums.UserRole;
import org.example.hr_system.common.exception.BusinessException;
import org.example.hr_system.common.exception.ResourceNotFoundException;
import org.example.hr_system.entity.Position;
import org.example.hr_system.common.mapper.PositionMapper;
import org.example.hr_system.service.PositionService;
import org.example.hr_system.utils.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PositionServiceImpl implements PositionService {

    @Autowired
    private PositionMapper positionMapper;

    @Override
    public List<Position> getPositionList(String keyword, String status, String userRole) {
        if(UserRole.VISITOR.name().equals(userRole)){
            status = PositionStatus.PUBLISHED.name();
        }
        return positionMapper.selectPositionList(keyword, status);
    }

    @Override
    public Position getPositionById(Long id) {
        Position position = positionMapper.selectById(id);
        if(position == null){
            throw new ResourceNotFoundException("该岗位信息不存在");
        }
        return position;
    }

    // ===================== 【修复：岗位名称重复判断】 =====================
    @Override
    public void addPosition(Position position) {
        // 1. 判断岗位是否已存在
        Position exist = positionMapper.selectByTitle(position.getTitle());
        if (exist != null) {
            throw new BusinessException(400, "岗位名称已存在！");
        }

        // 2. 正常赋值（保留你原来的逻辑）
        position.setStatus(PositionStatus.DRAFT.name());
        position.setCreateTime(LocalDateTime.now());
        position.setUpdateTime(LocalDateTime.now());
        positionMapper.insertPosition(position);
    }

    @Override
    public void updatePosition(Position position) {
        Position oldPos = getPositionById(position.getId());
        if(PositionStatus.CLOSED.name().equals(oldPos.getStatus())
                && PositionStatus.PUBLISHED.name().equals(position.getStatus())){
            throw new BusinessException(400,"已关闭岗位不能修改为已发布状态");
        }
        position.setUpdateTime(LocalDateTime.now());
        positionMapper.updatePosition(position);
    }

    @Override
    public void deletePosition(Long id) {
        Position position = getPositionById(id);
        if(PositionStatus.PUBLISHED.name().equals(position.getStatus())){
            throw new BusinessException(400,"已发布岗位无法删除");
        }
        positionMapper.deleteById(id);
    }

    @Override
    public void submitApproval(Long id) {
        Position pos = getPositionById(id);
        if(!PositionStatus.DRAFT.name().equals(pos.getStatus())){
            throw new BusinessException(400,"仅草稿状态岗位可提交审批");
        }
        pos.setStatus(PositionStatus.PENDING.name());
        pos.setUpdateTime(LocalDateTime.now());
        positionMapper.updatePosition(pos);
    }

    @Override
    public void approvePosition(Long id, boolean pass) {
        Position pos = getPositionById(id);
        if(!PositionStatus.PENDING.name().equals(pos.getStatus())){
            throw new BusinessException(400,"非待审批岗位无法进行审批操作");
        }
        if(pass){
            pos.setStatus(PositionStatus.PUBLISHED.name());
        }else{
            pos.setStatus(PositionStatus.DRAFT.name());
        }
        pos.setUpdateTime(LocalDateTime.now());
        positionMapper.updatePosition(pos);
    }

    @Override
    public void closePosition(Long id) {
        Position pos = getPositionById(id);
        if(!PositionStatus.PUBLISHED.name().equals(pos.getStatus())){
            throw new BusinessException(400,"仅已发布岗位可执行关闭操作");
        }
        pos.setStatus(PositionStatus.CLOSED.name());
        pos.setUpdateTime(LocalDateTime.now());
        positionMapper.updatePosition(pos);
    }

    @Override
    public String importPositionData(MultipartFile file) {
        List<Position> list = ExcelUtil.readExcelToPosition(file);
        int count = 0;
        for(Position p : list){
            p.setStatus(PositionStatus.DRAFT.name());
            p.setCreateTime(LocalDateTime.now());
            p.setUpdateTime(LocalDateTime.now());
            positionMapper.insertPosition(p);
            count++;
        }
        return "成功导入"+count+"条岗位数据";
    }
}