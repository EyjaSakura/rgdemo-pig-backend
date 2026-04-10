-- 仿学习通教学管理平台 - 最终精简数据库结构
-- 基于 pig 原生权限表 + 业务核心表
-- 创建时间：2026-04-08
-- 适用于计算机专业大三课程设计

-- 1. 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `xuexitong_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `xuexitong_db`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- pig 原生权限表（精简版）
-- ============================================

-- 部门表（用于组织架构）
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  `name` varchar(50) DEFAULT NULL COMMENT '部门名称',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '修改人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `parent_id` bigint DEFAULT NULL COMMENT '父级部门ID',
  `dept_category` char(1) DEFAULT '0' COMMENT '层级类型(0-校级 1-职能划分 2-学院/部门 3-专业 4-班级)',
  PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门管理';

-- 用户表（精简核心字段）
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `username` varchar(64) DEFAULT NULL COMMENT '用户名//学号/教工号',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `phone` varchar(20) DEFAULT NULL COMMENT '电话号码',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `name` varchar(64) DEFAULT NULL COMMENT '姓名',
  `dept_id` bigint DEFAULT NULL COMMENT '所属部门ID//所在班级/所属学院',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '修改人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `lock_flag` char(1) DEFAULT '0' COMMENT '锁定标记，0未锁定，9已锁定',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标记，0未删除，1已删除',
  `user_type` varchar(5) DEFAULT 'stu' COMMENT '用户类型(adm管理 tch教师 stu学生，可扩展)',
  PRIMARY KEY (`user_id`) USING BTREE,
  KEY `user_idx1_username` (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `role_name` varchar(64) DEFAULT NULL COMMENT '角色名称',
  `role_code` varchar(64) DEFAULT NULL COMMENT '角色编码',
  `role_desc` varchar(255) DEFAULT NULL COMMENT '角色描述',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '修改人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标记，0未删除，1已删除',
  PRIMARY KEY (`role_id`) USING BTREE,
  KEY `role_idx1_role_code` (`role_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色表';

-- 用户角色关联表
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色表';

-- 菜单表（精简，只保留必要字段）
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  `parent_id` bigint DEFAULT NULL COMMENT '父级菜单ID',
  `name` varchar(32) DEFAULT NULL COMMENT '菜单名称',
  `path` varchar(128) DEFAULT NULL COMMENT '菜单路径',
  `permission` varchar(32) DEFAULT NULL COMMENT '菜单权限标识',
  `sort_order` int DEFAULT '0' COMMENT '排序值',
  `type` char(1) DEFAULT NULL COMMENT '菜单类型 (0菜单 1按钮)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单管理';

-- 角色菜单关联表
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单表';

-- ============================================
-- 业务核心表（精简版，去掉冗余字段）
-- ============================================

-- 学院表（独立于 sys_dept）
DROP TABLE IF EXISTS `biz_college`;
CREATE TABLE `biz_college` (
  `college_id` bigint NOT NULL AUTO_INCREMENT COMMENT '学院ID',
  `college_name` varchar(100) NOT NULL COMMENT '学院名称',
  `college_code` varchar(20) DEFAULT NULL COMMENT '学院编码',
  `description` varchar(500) DEFAULT NULL COMMENT '学院介绍',
  `sort_order` int DEFAULT '0' COMMENT '排序值',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`college_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学院信息表';

-- 课程基本信息表（主键改为 course_code，字符串）
DROP TABLE IF EXISTS `biz_course`;
CREATE TABLE `biz_course` (
  `course_code` varchar(20) NOT NULL COMMENT '课程代码（主键）',
  `course_name` varchar(100) NOT NULL COMMENT '课程名称',
  `college_id` bigint DEFAULT NULL COMMENT '所属学院ID',
  `credit` int DEFAULT '2' COMMENT '学分',
  `total_hours` int DEFAULT '32' COMMENT '总学时',
  `is_required` tinyint(1) DEFAULT '0' COMMENT '是否必修 (0选修 1必修)',
  `description` text COMMENT '课程描述',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图片URL',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`course_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程信息表';

-- 任课表（核心：课程指派给教师，用于数据隔离）
DROP TABLE IF EXISTS `biz_teaching`;
CREATE TABLE `biz_teaching` (
  `teaching_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任课ID（代理主键）',
  `course_code` varchar(20) NOT NULL COMMENT '课程代码',
  `teach_year` varchar(9) NOT NULL COMMENT '授课学年（如2025-2026）',
  `seq_no` int DEFAULT '1' COMMENT '开课序号（同课程同年可多次开设）',
  `teacher_id` bigint NOT NULL COMMENT '授课教师ID',
  `max_students` int DEFAULT '100' COMMENT '最大选课人数',
  `time_info` varchar(100) DEFAULT NULL COMMENT '上课时间（如：周一 1-2节）',
  `location` varchar(100) DEFAULT NULL COMMENT '上课地点',
  `status` tinyint(1) DEFAULT '0' COMMENT '状态 (0未开始 1进行中 2已结课)',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`teaching_id`),
  UNIQUE KEY `uk_teaching_course_year_seq` (`course_code`, `teach_year`, `seq_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任课安排表';

-- 选课表（学生关联到 teaching_id 而非 course_id）
DROP TABLE IF EXISTS `biz_enrollment`;
CREATE TABLE `biz_enrollment` (
  `enrollment_id` bigint NOT NULL COMMENT '选课记录ID',
  `teaching_id` bigint NOT NULL COMMENT '任课ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否有效 (0退课 1在修)',
  `progress` int DEFAULT '0' COMMENT '学习进度 (0-100)',
  `completed_at` datetime DEFAULT NULL COMMENT '完成时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`enrollment_id`),
  UNIQUE KEY `uk_enrollment_teaching_student` (`teaching_id`, `student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生选课表';

-- 助教表（一课一助教，用于数据隔离）
DROP TABLE IF EXISTS `biz_teaching_assistant`;
CREATE TABLE `biz_teaching_assistant` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `teaching_id` bigint NOT NULL COMMENT '任课ID',
  `assistant_id` bigint NOT NULL COMMENT '助教ID',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ta_teaching_assistant` (`teaching_id`, `assistant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程助教表';

-- 作业发布表（关联 teaching_id）
DROP TABLE IF EXISTS `biz_homework`;
CREATE TABLE `biz_homework` (
  `homework_id` bigint NOT NULL COMMENT '作业ID',
  `teaching_id` bigint NOT NULL COMMENT '任课ID',
  `title` varchar(100) NOT NULL COMMENT '作业标题',
  `description` text COMMENT '作业描述',
  `publish_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `deadline` datetime NOT NULL COMMENT '提交截止时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`homework_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作业发布表';

-- 作业附件表（多文件独立存储）
DROP TABLE IF EXISTS `biz_homework_attachment`;
CREATE TABLE `biz_homework_attachment` (
  `attachment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '附件ID',
  `homework_id` bigint NOT NULL COMMENT '作业ID',
  `file_name` varchar(255) NOT NULL COMMENT '原文件名',
  `file_path` varchar(500) NOT NULL COMMENT '存储路径',
  `file_size` bigint DEFAULT '0' COMMENT '文件大小(字节)',
  `file_type` varchar(50) DEFAULT NULL COMMENT '文件类型',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`attachment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作业附件表';

-- 作业提交表（核心：学生提交记录）
DROP TABLE IF EXISTS `biz_homework_submission`;
CREATE TABLE `biz_homework_submission` (
  `submission_id` bigint NOT NULL COMMENT '提交记录ID',
  `homework_id` bigint NOT NULL COMMENT '作业ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `comment` text COMMENT '学生提交时的备注',
  `teacher_comment` text COMMENT '教师批阅评语',
  `score` decimal(5,2) DEFAULT NULL COMMENT '得分',
  `graded_by` bigint DEFAULT NULL COMMENT '批阅人ID（教师或助教）',
  `status` tinyint(1) DEFAULT '0' COMMENT '状态 (0未交 1已交待批 2已批阅 3逾期)',
  `submit_time` datetime DEFAULT NULL COMMENT '实际提交时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`submission_id`),
  UNIQUE KEY `uk_submission_homework_student` (`homework_id`, `student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作业提交表';

-- 提交文件表（学生提交的多个文件）
DROP TABLE IF EXISTS `biz_submission_file`;
CREATE TABLE `biz_submission_file` (
  `file_id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `submission_id` bigint NOT NULL COMMENT '提交记录ID',
  `file_name` varchar(255) NOT NULL COMMENT '原文件名',
  `file_path` varchar(500) NOT NULL COMMENT '存储路径',
  `file_size` bigint DEFAULT '0' COMMENT '文件大小(字节)',
  `file_type` varchar(50) DEFAULT NULL COMMENT '文件类型',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提交文件表';

-- 公告表（可发布课程内或全校公告）
DROP TABLE IF EXISTS `biz_announcement`;
CREATE TABLE `biz_announcement` (
  `announcement_id` bigint NOT NULL COMMENT '公告ID',
  `teaching_id` bigint DEFAULT NULL COMMENT '任课ID（NULL表示全校公告）',
  `title` varchar(100) NOT NULL COMMENT '公告标题',
  `content` text NOT NULL COMMENT '公告内容',
  `scope` tinyint(1) DEFAULT '0' COMMENT '范围 (0课程内 1全校)',
  `is_pinned` tinyint(1) DEFAULT '0' COMMENT '是否置顶 (0否 1是)',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`announcement_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告管理表';

-- 消息表（系统推送各类消息）
DROP TABLE IF EXISTS `biz_message`;
CREATE TABLE `biz_message` (
  `message_id` bigint NOT NULL COMMENT '消息ID',
  `user_id` bigint NOT NULL COMMENT '接收用户ID',
  `msg_type` tinyint(1) DEFAULT '0' COMMENT '消息类型 (0系统 1新作业 2批改完成 3截止提醒 4课程公告 5全校公告)',
  `title` varchar(100) NOT NULL COMMENT '消息标题',
  `content` text COMMENT '消息内容',
  `ref_type` varchar(50) DEFAULT NULL COMMENT '关联类型（homework/announcement等）',
  `ref_id` bigint DEFAULT NULL COMMENT '关联ID',
  `is_read` tinyint(1) DEFAULT '0' COMMENT '是否已读 (0未读 1已读)',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息通知表';

-- 课件表（教学资料管理）
DROP TABLE IF EXISTS `biz_courseware`;
CREATE TABLE `biz_courseware` (
  `courseware_id` bigint NOT NULL AUTO_INCREMENT COMMENT '课件ID',
  `teaching_id` bigint NOT NULL COMMENT '任课ID',
  `folder_name` varchar(50) DEFAULT '默认分组' COMMENT '文件夹/分组名称',
  `title` varchar(100) NOT NULL COMMENT '课件标题',
  `file_path` varchar(500) NOT NULL COMMENT '课件存储路径',
  `sort_order` int DEFAULT '0' COMMENT '排序值',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`courseware_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课件管理表';

SET FOREIGN_KEY_CHECKS = 1;

-- 索引建议
ALTER TABLE `biz_teaching` ADD INDEX `idx_teaching_teacher` (`teacher_id`);
ALTER TABLE `biz_enrollment` ADD INDEX `idx_enrollment_student` (`student_id`);
ALTER TABLE `biz_homework` ADD INDEX `idx_homework_teaching` (`teaching_id`);
ALTER TABLE `biz_homework_submission` ADD INDEX `idx_submission_student` (`student_id`);
ALTER TABLE `biz_message` ADD INDEX `idx_message_user` (`user_id`);

-- 注释说明
-- 本数据库设计针对仿学习通教学管理平台课程设计需求
-- 核心特点：
-- 1. 课程与任课分离（biz_course + biz_teaching）
-- 2. 数据隔离通过 teaching_id 外键链实现
-- 3. 附件多对多独立存储，避免逗号分隔串
-- 4. 消息推送机制完整
-- 5. 与 pig 原生权限体系完全集成