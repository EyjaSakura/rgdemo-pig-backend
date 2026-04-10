-- ============================================================
-- 仿学习通平台 — 业务初始化数据
-- V3.0  日期: 2026-04-08
-- 执行顺序：在 pig.sql 导入后执行本文件
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- 1. 路由注册（pig-gateway 动态路由，写入 Nacos 或直接写 DB 均可）
--    pig-gateway 默认用 Nacos 配置，以下供参考
-- ============================================================
-- 如果项目使用 sys_oauth_client_details 管理客户端，追加业务服务：
INSERT IGNORE INTO `sys_oauth_client_details`
    (`client_id`, `resource_ids`, `client_secret`, `scope`, `authorized_grant_types`,
     `web_server_redirect_uri`, `authorities`, `access_token_validity`, `refresh_token_validity`,
     `additional_information`, `autoapprove`)
VALUES
    ('eyja', NULL, '$2a$10$VFcrihkSXEA9WUlUiuZgZ.ZpFMBYoA3K4w6tBgJCFJMjRQ2UrJT7K',
     'server', 'password,refresh_token',
     NULL, NULL, 43200, 2592000, NULL, 'true');

-- ============================================================
-- 2. 顶级菜单 — 教学管理
-- ============================================================
INSERT IGNORE INTO `sys_menu`
    (`menu_id`, `name`, `permission`, `path`, `parent_id`, `icon`, `sort_order`, `type`)
VALUES
    (100, '教学管理',   NULL,              '/biz',                  -1, 'BookOpen',   10, '0'),

    -- 学院管理
    (101, '学院管理',   NULL,              '/biz/college',           100, 'Building',    1, '1'),
    (1011,'查看学院',   'biz_college_view', NULL,                    101, NULL, 0, '2'),
    (1012,'新增学院',   'biz_college_add',  NULL,                    101, NULL, 0, '2'),
    (1013,'修改学院',   'biz_college_edit', NULL,                    101, NULL, 0, '2'),
    (1014,'删除学院',   'biz_college_del',  NULL,                    101, NULL, 0, '2'),

    -- 课程管理
    (102, '课程管理',   NULL,              '/biz/course',            100, 'Layers',      2, '1'),
    (1021,'查看课程',   'biz_course_view',  NULL,                    102, NULL, 0, '2'),
    (1022,'新增课程',   'biz_course_add',   NULL,                    102, NULL, 0, '2'),
    (1023,'修改课程',   'biz_course_edit',  NULL,                    102, NULL, 0, '2'),
    (1024,'删除课程',   'biz_course_del',   NULL,                    102, NULL, 0, '2'),

    -- 任课管理
    (103, '任课管理',   NULL,              '/biz/teaching',          100, 'Users',       3, '1'),
    (1031,'查看任课',   'biz_teaching_view', NULL,                   103, NULL, 0, '2'),
    (1032,'新增任课',   'biz_teaching_add',  NULL,                   103, NULL, 0, '2'),
    (1033,'修改任课',   'biz_teaching_edit', NULL,                   103, NULL, 0, '2'),
    (1034,'删除任课',   'biz_teaching_del',  NULL,                   103, NULL, 0, '2'),
    (1035,'选课操作',   'biz_teaching_enroll', NULL,                 103, NULL, 0, '2'),

    -- 选课名单
    (104, '选课名单',   NULL,              '/biz/enrollment',        100, 'UserCheck',   4, '1'),
    (1041,'查看名单',   'biz_enrollment_view', NULL,                 104, NULL, 0, '2'),

    -- 助教管理
    (105, '助教管理',   NULL,              '/biz/ta',                100, 'UserPlus',    5, '1'),
    (1051,'查看助教',   'biz_ta_view',      NULL,                    105, NULL, 0, '2'),
    (1052,'指派助教',   'biz_ta_add',       NULL,                    105, NULL, 0, '2'),
    (1053,'撤销助教',   'biz_ta_del',       NULL,                    105, NULL, 0, '2'),

    -- 课件管理
    (106, '课件管理',   NULL,              '/biz/courseware',        100, 'FileText',    6, '1'),
    (1061,'查看课件',   'biz_courseware_view', NULL,                 106, NULL, 0, '2'),
    (1062,'上传课件',   'biz_courseware_add',  NULL,                 106, NULL, 0, '2'),
    (1063,'修改课件',   'biz_courseware_edit', NULL,                 106, NULL, 0, '2'),
    (1064,'删除课件',   'biz_courseware_del',  NULL,                 106, NULL, 0, '2'),

    -- 作业管理
    (107, '作业管理',   NULL,              '/biz/homework',          100, 'ClipboardList', 7, '1'),
    (1071,'查看作业',   'biz_homework_view',   NULL,                 107, NULL, 0, '2'),
    (1072,'发布作业',   'biz_homework_add',    NULL,                 107, NULL, 0, '2'),
    (1073,'修改作业',   'biz_homework_edit',   NULL,                 107, NULL, 0, '2'),
    (1074,'删除作业',   'biz_homework_del',    NULL,                 107, NULL, 0, '2'),
    (1075,'提交作业',   'biz_homework_submit', NULL,                 107, NULL, 0, '2'),
    (1076,'批改作业',   'biz_homework_grade',  NULL,                 107, NULL, 0, '2'),

    -- 提交列表
    (108, '提交管理',   NULL,              '/biz/submission',        100, 'CheckCircle', 8, '1'),
    (1081,'查看提交',   'biz_submission_view', NULL,                 108, NULL, 0, '2'),

    -- 公告管理
    (109, '公告管理',   NULL,              '/biz/announcement',      100, 'Bell',        9, '1'),
    (1091,'查看公告',   'biz_announcement_view', NULL,               109, NULL, 0, '2'),
    (1092,'发布公告',   'biz_announcement_add',  NULL,               109, NULL, 0, '2'),
    (1093,'修改公告',   'biz_announcement_edit', NULL,               109, NULL, 0, '2'),
    (1094,'删除公告',   'biz_announcement_del',  NULL,               109, NULL, 0, '2'),

    -- 消息中心
    (110, '消息中心',   NULL,              '/biz/message',           100, 'MessageSquare', 10, '1'),
    (1101,'查看消息',   'biz_message_view', NULL,                    110, NULL, 0, '2'),

    -- 学习记录
    (111, '学习记录',   NULL,              '/biz/learning',          100, 'Activity',   11, '1'),
    (1111,'查看记录',   'biz_learning_view',   NULL,                 111, NULL, 0, '2'),
    (1112,'上报记录',   'biz_learning_report', NULL,                 111, NULL, 0, '2');


-- ============================================================
-- 3. 角色-菜单绑定
-- ============================================================

-- ROLE_ADMIN (1)：拥有所有权限
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, menu_id FROM sys_menu WHERE menu_id BETWEEN 100 AND 1112;

-- ROLE_COLLEGE_ADMIN (2)：可管理学院/课程/任课/名单，不能提交/批改作业
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(2, 100),(2, 101),(2, 1011),(2, 1012),(2, 1013),(2, 1014),
(2, 102),(2, 1021),(2, 1022),(2, 1023),(2, 1024),
(2, 103),(2, 1031),(2, 1032),(2, 1033),(2, 1034),
(2, 104),(2, 1041),
(2, 109),(2, 1091),(2, 1092),(2, 1093),(2, 1094);

-- ROLE_TEACHER (3)：查课程/任课、管理课件作业、查看名单、指派助教、发公告
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(3, 100),
(3, 103),(3, 1031),
(3, 104),(3, 1041),
(3, 105),(3, 1051),(3, 1052),(3, 1053),
(3, 106),(3, 1061),(3, 1062),(3, 1063),(3, 1064),
(3, 107),(3, 1071),(3, 1072),(3, 1073),(3, 1074),(3, 1076),
(3, 108),(3, 1081),
(3, 109),(3, 1091),(3, 1092),(3, 1093),(3, 1094),
(3, 110),(3, 1101),
(3, 111),(3, 1111);

-- ROLE_STUDENT (4)：选课/查课件作业/提交作业/查公告/消息/学习记录上报
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(4, 100),
(4, 103),(4, 1031),(4, 1035),
(4, 106),(4, 1061),
(4, 107),(4, 1071),(4, 1075),
(4, 109),(4, 1091),
(4, 110),(4, 1101),
(4, 111),(4, 1111),(4, 1112);

-- ROLE_TA (5)：助教，可查看/批改所授权任课的作业提交
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(5, 100),
(5, 107),(5, 1071),(5, 1076),
(5, 108),(5, 1081),
(5, 110),(5, 1101),
(5, 111),(5, 1111);


-- ============================================================
-- 4. 组织架构初始数据（示例）
-- ============================================================
INSERT IGNORE INTO `sys_dept` (`dept_id`, `name`, `parent_id`, `dept_category`) VALUES
(1,  '某某大学',       NULL, '0'),
(2,  '教务处',         1,    '1'),
(10, '计算机科学学院', 1,    '2'),
(11, '电子信息学院',   1,    '2'),
(20, '计算机科学与技术专业', 10, '3'),
(21, '软件工程专业',         10, '3'),
(30, '2022级计科1班', 20,   '4'),
(31, '2022级计科2班', 20,   '4'),
(32, '2022级软工1班', 21,   '4');

INSERT IGNORE INTO `biz_college` (`college_id`, `college_code`, `college_name`, `dept_id`) VALUES
(1, 'CS',  '计算机科学学院', 10),
(2, 'EE',  '电子信息学院',   11);


-- ============================================================
-- 5. 课程示例数据
-- ============================================================
INSERT IGNORE INTO `biz_course`
    (`course_code`, `course_name`, `college_id`, `credit`, `total_hours`, `is_required`)
VALUES
    ('CS101001', 'Java程序设计',        1, 3.0, 48, 1),
    ('CS101002', '数据结构与算法',      1, 3.0, 48, 1),
    ('CS101003', '操作系统原理',        1, 3.0, 48, 1),
    ('CS200001', '机器学习基础',        1, 2.0, 32, 0),
    ('CS200002', 'Web前端开发',         1, 2.0, 32, 0),
    ('EE101001', '电路分析基础',        2, 3.0, 48, 1);

SET FOREIGN_KEY_CHECKS = 1;
