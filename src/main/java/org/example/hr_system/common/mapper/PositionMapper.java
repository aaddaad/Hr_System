package org.example.hr_system.common.mapper;

import org.example.hr_system.entity.Position;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PositionMapper {

    // 条件查询岗位列表（去掉了<script>动态SQL，避免语法报错）
    @Select("SELECT * FROM position WHERE (title LIKE CONCAT('%', #{keyword}, '%') OR #{keyword} IS NULL) " +
            "AND (status = #{status} OR #{status} IS NULL) ORDER BY create_time DESC")
    List<Position> selectPositionList(String keyword, String status);

    // 根据ID查询
    @Select("SELECT * FROM position WHERE id = #{id}")
    Position selectById(Long id);

    // 新增岗位
    @Insert("INSERT INTO position(title, description, status, create_time, update_time) " +
            "VALUES(#{title}, #{description}, #{status}, #{createTime}, #{updateTime})")
    int insertPosition(Position position);

    // 修改岗位
    @Update("UPDATE position SET title=#{title}, description=#{description}, " +
            "status=#{status}, update_time=#{updateTime} WHERE id=#{id}")
    int updatePosition(Position position);

    // 删除岗位
    @Delete("DELETE FROM position WHERE id=#{id}")
    int deleteById(Long id);

    @Select("SELECT * FROM position WHERE title = #{title}")
    Position selectByTitle(String title);
}