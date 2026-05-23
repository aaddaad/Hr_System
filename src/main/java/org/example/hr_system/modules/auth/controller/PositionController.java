package org.example.hr_system.modules.auth.controller;

import org.example.hr_system.common.result.Result;
import org.example.hr_system.entity.Position;
import org.example.hr_system.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/webapi/positions")
public class PositionController {

    // 1. 把 @Resource 改成 @Autowired（解决“无法解析符号'Resource'”）
    @Autowired
    private PositionService positionService;

    // 1. 查询岗位列表
    @GetMapping
    public Result<List<Position>> getList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestHeader(value = "X-User-Role", defaultValue = "VISITOR") String userRole){
        List<Position> list = positionService.getPositionList(keyword, status, userRole);
        return Result.success(list);
    }

    // 2. 查询单个岗位
    @GetMapping("/{id}")
    public Result<Position> getInfo(@PathVariable Long id){
        Position position = positionService.getPositionById(id);
        return Result.success(position);
    }

    // 3. 新增岗位
    @PostMapping
    public Result<Void> add(@RequestBody Position position){
        positionService.addPosition(position);
        return Result.success();
    }

    // 4. 修改岗位
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Position position){
        position.setId(id);
        positionService.updatePosition(position);
        return Result.success();
    }

    // 5. 删除岗位
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id){
        positionService.deletePosition(id);
        return Result.success();
    }

    // 6. 提交审批
    @PostMapping("/{id}/submit")
    public Result<Void> submit(@PathVariable Long id){
        positionService.submitApproval(id);
        return Result.success();
    }

    // 7. 审批操作
    @PostMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id, @RequestParam boolean pass){
        positionService.approvePosition(id, pass);
        return Result.success();
    }

    // 8. 关闭岗位
    @PostMapping("/{id}/close")
    public Result<Void> close(@PathVariable Long id){
        positionService.closePosition(id);
        return Result.success();
    }

    // 9. Excel批量导入
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file){
        String msg = positionService.importPositionData(file);
        return Result.success(msg);
    }
}