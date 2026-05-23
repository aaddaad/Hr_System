package org.example.hr_system.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.hr_system.entity.SysUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    SysUser selectByUsername(String username);

    @Insert("INSERT INTO sys_user(username,password,real_name,status,create_time) " +
            "VALUES(#{username},#{password},#{realName},1,NOW())")
    int insert(SysUser user);
}