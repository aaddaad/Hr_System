package org.example.hr_system.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.hr_system.entity.SysUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("SELECT id, username, password, real_name AS realName, status, role, create_time AS createTime FROM sys_user WHERE username = #{username}")
    SysUser selectByUsername(String username);

    @Insert("INSERT INTO sys_user(username,password,real_name,role,status,create_time) " +
            "VALUES(#{username},#{password},#{realName},#{role},1,NOW())")
    int insert(SysUser user);
}