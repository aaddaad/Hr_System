-- 创建数据库
CREATE DATABASE IF NOT EXISTS hr_recruit DEFAULT CHARACTER SET utf8mb4;

USE hr_recruit;

-- 删除已存在的表
DROP TABLE IF EXISTS `sys_user`;
DROP TABLE IF EXISTS `position`;

-- 用户表（登录使用）
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(50) NOT NULL COMMENT '账号',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) COMMENT '真实姓名',
  `role` varchar(20) DEFAULT 'VISITOR' COMMENT '角色 RECRUITER/APPROVER/VISITOR',
  `status` tinyint DEFAULT 1 COMMENT '状态 0禁用 1启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 岗位表（核心业务表）
CREATE TABLE `position` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `title` varchar(100) NOT NULL COMMENT '岗位名称',
  `description` text COMMENT '岗位描述',
  `status` varchar(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态 DRAFT/PENDING/PUBLISHED/CLOSED',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位表';

-- 初始化默认用户
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role`, `status`) VALUES 
('admin', '123456', '管理员', 'RECRUITER', 1),
('test3', '123456', '测试用户', 'VISITOR', 1);
