/*
 =============================================================
  幼儿园管理系统 · 统一数据库初始化脚本
  适用范围：PC端 kgms-main + 手机端 App 共用同一个 MySQL 数据库
 
  表分类说明
  ┌─ 共用表（两端都用）────────────────────────────────────────┐
  │  users           用户表（管理员/教师/家长）                 │
  │  children        幼儿信息表                                │
  │  user_children   家长与幼儿关系表                          │
  │  resources       资源表（PC端新增4个字段）                  │
  │  tags            标签表                                    │
  │  resource_tags   资源-标签关联表                           │
  │  tasks           任务表                                    │
  │  task_assignments 任务分配表                               │
  │  albums          相册表                                    │
  │  album_photos    相册照片表                                │
  │  attendance_records 考勤记录表                             │
  │  messages        消息表                                    │
  │  payment_records 缴费记录表                                │
  │  folders         文件夹表                                  │
  │  system_settings 系统设置表                                │
  └───────────────────────────────────────────────────────────┘
  ┌─ PC端专属表（手机端不使用）────────────────────────────────┐
  │  kg_category     三级资源目录树                            │
  │  kg_favorites    收藏记录表                                │
  └───────────────────────────────────────────────────────────┘

  初始化命令：
    mysql -uroot -p你的密码 < sql/init.sql
  或先建库再导入：
    mysql -uroot -p你的密码 -e "CREATE DATABASE IF NOT EXISTS youeryuan CHARACTER SET utf8mb4;"
    mysql -uroot -p你的密码 youeryuan < sql/init.sql
 =============================================================
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- 共用表：users（用户表）
-- role_id: 0=管理员/园长  1=家长  2=教师  3=保育员
-- ============================================================
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id`         INT          NOT NULL AUTO_INCREMENT,
  `username`   VARCHAR(50)  NOT NULL COMMENT '用户名（唯一）',
  `password`   VARCHAR(255) NOT NULL COMMENT '密码（BCrypt 加密）',
  `phone`      VARCHAR(11)  NOT NULL COMMENT '手机号（唯一）',
  `role_id`    INT          NOT NULL DEFAULT 2
               COMMENT '角色: 0=管理员/园长, 1=家长, 2=教师, 3=保育员',
  `name`       VARCHAR(50)  NOT NULL COMMENT '真实姓名',
  `avatar`     VARCHAR(255) NULL     DEFAULT NULL COMMENT '头像URL',
  `status`     ENUM('active','inactive') NULL DEFAULT 'active' COMMENT '账号状态',
  `created_at` TIMESTAMP    NULL     DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP    NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone`    (`phone`),
  INDEX        `idx_role`  (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 初始用户（密码明文均为 123456，BCrypt 加密后的值）
-- 如果你们约定使用 MD5，把 password 值改为 e10adc3949ba59abbe56e057f20f883e
INSERT INTO `users` VALUES
  (1, 'admin',    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '13800138001', 0, '王园长',   NULL, 'active', NOW(), NOW()),
  (2, 'teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '13800138002', 2, '李老师',   NULL, 'active', NOW(), NOW()),
  (3, 'teacher2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '13800138003', 2, '张老师',   NULL, 'active', NOW(), NOW()),
  (4, 'teacher3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '13800138004', 2, '王老师',   NULL, 'active', NOW(), NOW()),
  (5, 'care1',    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '13800138005', 3, '赵保育',   NULL, 'active', NOW(), NOW()),
  (6, 'parent1',  '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '13800138006', 1, '李家长',   NULL, 'active', NOW(), NOW());


-- ============================================================
-- 共用表：children（幼儿信息表）
-- ============================================================
DROP TABLE IF EXISTS `children`;
CREATE TABLE `children` (
  `id`         INT         NOT NULL AUTO_INCREMENT,
  `name`       VARCHAR(50) NOT NULL COMMENT '幼儿姓名',
  `gender`     ENUM('male','female') NOT NULL COMMENT '性别',
  `birthdate`  DATE        NOT NULL COMMENT '出生日期',
  `class`      VARCHAR(50) NOT NULL COMMENT '所在班级',
  `child_code` VARCHAR(20) NOT NULL COMMENT '幼儿编码（唯一）',
  `status`     ENUM('active','inactive') NULL DEFAULT 'active',
  `created_at` TIMESTAMP   NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP   NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_child_code` (`child_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='幼儿信息表';


-- ============================================================
-- 共用表：user_children（家长-幼儿关系表）
-- ============================================================
DROP TABLE IF EXISTS `user_children`;
CREATE TABLE `user_children` (
  `id`           INT         NOT NULL AUTO_INCREMENT,
  `user_id`      INT         NOT NULL COMMENT '家长用户ID',
  `child_id`     INT         NOT NULL COMMENT '幼儿ID',
  `relationship` VARCHAR(50) NOT NULL COMMENT '关系（如：父亲/母亲/祖父）',
  `created_at`   TIMESTAMP   NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_child` (`user_id`, `child_id`),
  INDEX `idx_child` (`child_id`),
  CONSTRAINT `fk_uc_user`  FOREIGN KEY (`user_id`)  REFERENCES `users`(`id`)    ON DELETE RESTRICT,
  CONSTRAINT `fk_uc_child` FOREIGN KEY (`child_id`) REFERENCES `children`(`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家长-幼儿关系表';


-- ============================================================
-- PC端专属表：kg_category（三级资源目录树）
-- 层级: level=1 一级（教育教学/计划与总结/教学资料）
--       level=2 二级（五大领域教学/节庆主题活动/...）
--       level=3 三级叶子节点（健康/语言/科学/...）
-- ============================================================
DROP TABLE IF EXISTS `kg_category`;
CREATE TABLE `kg_category` (
  `id`         INT          NOT NULL AUTO_INCREMENT,
  `parent_id`  INT          NULL     DEFAULT NULL COMMENT '父节点ID，一级目录为NULL',
  `name`       VARCHAR(100) NOT NULL COMMENT '目录名称',
  `level`      TINYINT      NOT NULL DEFAULT 1 COMMENT '层级 1/2/3',
  `sort_order` INT          NOT NULL DEFAULT 0 COMMENT '同级排序（越小越靠前）',
  `enabled`    TINYINT      NOT NULL DEFAULT 1 COMMENT '是否启用 1=是 0=否',
  `created_at` TIMESTAMP    NULL     DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP    NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_parent` (`parent_id`),
  CONSTRAINT `fk_cat_parent` FOREIGN KEY (`parent_id`) REFERENCES `kg_category`(`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='PC端三级资源目录树';

-- 初始化目录数据（完整三级结构）
INSERT INTO `kg_category` (`id`,`parent_id`,`name`,`level`,`sort_order`,`enabled`) VALUES
-- ── 一级目录
(1,  NULL, '教育教学',             1, 1, 1),
(2,  NULL, '计划与总结',           1, 2, 1),
(3,  NULL, '教学资料与过程记录',   1, 3, 1),
-- ── 二级：教育教学
(4,  1,   '五大领域教学',         2, 1, 1),
(5,  1,   '节庆主题活动',         2, 2, 1),
(6,  1,   '园本教研活动',         2, 3, 1),
-- ── 二级：计划与总结
(7,  2,   '学期计划',             2, 1, 1),
(8,  2,   '学期总结',             2, 2, 1),
-- ── 二级：教学资料与过程记录
(9,  3,   '教学设计',             2, 1, 1),
(10, 3,   '观察记录',             2, 2, 1),
(11, 3,   '活动资料',             2, 3, 1),
(12, 3,   '教师成长',             2, 4, 1),
-- ── 三级：五大领域教学
(13, 4,   '健康',                 3, 1, 1),
(14, 4,   '语言',                 3, 2, 1),
(15, 4,   '社会',                 3, 3, 1),
(16, 4,   '科学',                 3, 4, 1),
(17, 4,   '艺术',                 3, 5, 1),
-- ── 三级：节庆主题活动
(18, 5,   '传统节庆活动',         3, 1, 1),
(19, 5,   '校园特色活动',         3, 2, 1),
(20, 5,   '季节主题活动',         3, 3, 1),
(21, 5,   '专属节日活动',         3, 4, 1),
-- ── 三级：园本教研活动
(22, 6,   '健康领域教研',         3, 1, 1),
(23, 6,   '语言领域教研',         3, 2, 1),
(24, 6,   '社会领域教研',         3, 3, 1),
(25, 6,   '科学领域教研',         3, 4, 1),
(26, 6,   '艺术领域教研',         3, 5, 1),
-- ── 三级：学期计划
(27, 7,   '班级务实计划',         3, 1, 1),
(28, 7,   '家长工作计划',         3, 2, 1),
(29, 7,   '健康活动计划',         3, 3, 1),
(30, 7,   '安全计划',             3, 4, 1),
(31, 7,   '环创计划',             3, 5, 1),
(32, 7,   '体格锻炼工作计划',     3, 6, 1),
(33, 7,   '种植活动计划',         3, 7, 1),
(34, 7,   '劳动计划',             3, 8, 1),
(35, 7,   '游戏计划',             3, 9, 1),
(36, 7,   '个人计划',             3, 10, 1),
-- ── 三级：学期总结
(37, 8,   '班级务实总结',         3, 1, 1),
(38, 8,   '家长工作总结',         3, 2, 1),
(39, 8,   '健康活动总结',         3, 3, 1),
(40, 8,   '安全总结',             3, 4, 1),
(41, 8,   '环创总结',             3, 5, 1),
(42, 8,   '体格锻炼工作总结',     3, 6, 1),
(43, 8,   '种植活动总结',         3, 7, 1),
(44, 8,   '劳动总结',             3, 8, 1),
(45, 8,   '游戏总结',             3, 9, 1),
(46, 8,   '个人总结',             3, 10, 1),
-- ── 三级：教学设计
(47, 9,   '课程审议',             3, 1, 1),
(48, 9,   '月计划',               3, 2, 1),
(49, 9,   '周计划',               3, 3, 1),
(50, 9,   '教案',                 3, 4, 1),
-- ── 三级：观察记录
(51, 10,  '生活观察记录',         3, 1, 1),
(52, 10,  '游戏观察记录',         3, 2, 1),
(53, 10,  '活动观察记录',         3, 3, 1),
-- ── 三级：活动资料
(54, 11,  '民间体能活动',         3, 1, 1),
(55, 11,  '非遗美术活动',         3, 2, 1),
(56, 11,  '家长进课堂活动',       3, 3, 1),
-- ── 三级：教师成长
(57, 12,  '教师读书笔记',         3, 1, 1),
(58, 12,  '教育随笔',             3, 2, 1);


-- ============================================================
-- 共用表：tags（标签表）
-- ============================================================
DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags` (
  `id`         INT         NOT NULL AUTO_INCREMENT,
  `name`       VARCHAR(50) NOT NULL COMMENT '标签名称（唯一）',
  `created_at` TIMESTAMP   NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tag_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

INSERT INTO `tags` VALUES
  (1,  '健康',   NOW()),
  (2,  '语言',   NOW()),
  (3,  '社会',   NOW()),
  (4,  '科学',   NOW()),
  (5,  '艺术',   NOW()),
  (6,  '节庆',   NOW()),
  (7,  '教研',   NOW()),
  (8,  '家园共育', NOW()),
  (9,  '安全',   NOW()),
  (10, '环创',   NOW()),
  (11, '游戏',   NOW()),
  (12, '观察',   NOW()),
  (13, '教案',   NOW()),
  (14, '随笔',   NOW()),
  (15, '非遗',   NOW()),
  (16, '体能',   NOW()),
  (17, '劳动',   NOW()),
  (18, '种植',   NOW()),
  (19, '民间游戏', NOW());


-- ============================================================
-- 共用表：resources（资源表）
-- resource_type: 0=图片  1=视频  2=文档/PPT/PDF（与手机端一致）
-- resource_format: PC端细分格式 doc/pdf/ppt/img/vid
-- applicable_grade: 小班/中班/大班/全部
-- ============================================================
DROP TABLE IF EXISTS `resources`;
CREATE TABLE `resources` (
  `id`               INT          NOT NULL AUTO_INCREMENT,
  `title`            VARCHAR(255) NOT NULL COMMENT '资源标题',
  `description`      TEXT         NULL     COMMENT '资源描述',
  `url`              VARCHAR(255) NOT NULL COMMENT '资源相对路径，如 /upload/docs/xxx.pdf',
  `user_id`          INT          NOT NULL COMMENT '上传者ID',
  `status`           ENUM('public','private') NULL DEFAULT 'public' COMMENT '可见性',
  `view_count`       INT          NULL DEFAULT 0   COMMENT '浏览次数',
  `favorite_count`   INT          NULL DEFAULT 0   COMMENT '收藏次数',
  `resource_type`    TINYINT      NOT NULL DEFAULT 2
                     COMMENT '文件大类: 0=图片 1=视频 2=文档(含PPT/PDF)',
  `resource_format`  VARCHAR(10)  NULL DEFAULT NULL
                     COMMENT 'PC端细分格式: doc/pdf/ppt/img/vid',
  `file_name`        VARCHAR(255) NULL DEFAULT NULL COMMENT '原始文件名',
  `file_size`        BIGINT       NULL DEFAULT NULL COMMENT '文件大小（字节）',
  -- PC端专属字段（手机端不使用，保持NULL即可）
  `category_id`      INT          NULL DEFAULT NULL COMMENT '所属目录节点ID（PC端）',
  `applicable_grade` VARCHAR(10)  NULL DEFAULT NULL
                     COMMENT '适用班级: 小班/中班/大班/全部（PC端）',
  `semester`         VARCHAR(30)  NULL DEFAULT NULL
                     COMMENT '学期，如 2025-2026上学期（PC端）',
  `created_at`       TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`       TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_user`     (`user_id`),
  INDEX `idx_category` (`category_id`),
  INDEX `idx_type`     (`resource_type`),
  CONSTRAINT `fk_res_user`     FOREIGN KEY (`user_id`)     REFERENCES `users`(`id`)        ON DELETE RESTRICT,
  CONSTRAINT `fk_res_category` FOREIGN KEY (`category_id`) REFERENCES `kg_category`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源表（PC+手机共用，PC端字段有扩展）';


-- ============================================================
-- 共用表：resource_tags（资源-标签关联表）
-- ============================================================
DROP TABLE IF EXISTS `resource_tags`;
CREATE TABLE `resource_tags` (
  `resource_id` INT NOT NULL,
  `tag_id`      INT NOT NULL,
  PRIMARY KEY (`resource_id`, `tag_id`),
  INDEX `idx_tag` (`tag_id`),
  CONSTRAINT `fk_rt_resource` FOREIGN KEY (`resource_id`) REFERENCES `resources`(`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_rt_tag`      FOREIGN KEY (`tag_id`)      REFERENCES `tags`(`id`)      ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源-标签关联表';


-- ============================================================
-- PC端专属表：kg_favorites（收藏记录表）
-- 收藏时同步 UPDATE resources SET favorite_count = favorite_count + 1
-- ============================================================
DROP TABLE IF EXISTS `kg_favorites`;
CREATE TABLE `kg_favorites` (
  `id`          INT       NOT NULL AUTO_INCREMENT,
  `user_id`     INT       NOT NULL COMMENT '收藏用户ID',
  `resource_id` INT       NOT NULL COMMENT '被收藏的资源ID',
  `created_at`  TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_res` (`user_id`, `resource_id`),
  INDEX `idx_user_fav` (`user_id`),
  CONSTRAINT `fk_fav_user` FOREIGN KEY (`user_id`)     REFERENCES `users`(`id`)     ON DELETE CASCADE,
  CONSTRAINT `fk_fav_res`  FOREIGN KEY (`resource_id`) REFERENCES `resources`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='PC端收藏记录表';


-- ============================================================
-- 共用表：tasks（任务/活动表，主要手机端使用）
-- ============================================================
DROP TABLE IF EXISTS `tasks`;
CREATE TABLE `tasks` (
  `id`          INT          NOT NULL AUTO_INCREMENT,
  `title`       VARCHAR(255) NOT NULL COMMENT '任务标题',
  `description` TEXT         NULL     COMMENT '任务描述',
  `start_date`  DATE         NOT NULL,
  `end_date`    DATE         NOT NULL,
  `status`      ENUM('pending','in_progress','completed','cancelled') NULL DEFAULT 'pending',
  `priority`    ENUM('low','medium','high') NULL DEFAULT 'medium',
  `created_by`  INT          NOT NULL,
  `created_at`  TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`  TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_created_by` (`created_by`),
  CONSTRAINT `fk_task_user` FOREIGN KEY (`created_by`) REFERENCES `users`(`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务/活动表';


-- ============================================================
-- 共用表：task_assignments（任务分配表）
-- ============================================================
DROP TABLE IF EXISTS `task_assignments`;
CREATE TABLE `task_assignments` (
  `id`         INT      NOT NULL AUTO_INCREMENT,
  `task_id`    INT      NOT NULL,
  `user_id`    INT      NOT NULL,
  `status`     ENUM('pending','accepted','completed','rejected') NULL DEFAULT 'pending',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_task` (`task_id`),
  INDEX `idx_user` (`user_id`),
  CONSTRAINT `fk_ta_task` FOREIGN KEY (`task_id`) REFERENCES `tasks`(`id`)  ON DELETE CASCADE,
  CONSTRAINT `fk_ta_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务分配表';


-- ============================================================
-- 共用表：albums（相册表，主要手机端使用）
-- ============================================================
DROP TABLE IF EXISTS `albums`;
CREATE TABLE `albums` (
  `id`          INT         NOT NULL AUTO_INCREMENT,
  `name`        VARCHAR(100) NOT NULL,
  `description` TEXT         NULL,
  `cover_image` VARCHAR(255) NULL,
  `category`    ENUM('activity','daily','performance','sports') NOT NULL,
  `created_by`  INT          NOT NULL,
  `created_at`  TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`  TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_created_by` (`created_by`),
  CONSTRAINT `fk_album_user` FOREIGN KEY (`created_by`) REFERENCES `users`(`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='相册表';


-- ============================================================
-- 共用表：album_photos（相册照片）
-- ============================================================
DROP TABLE IF EXISTS `album_photos`;
CREATE TABLE `album_photos` (
  `id`          INT          NOT NULL AUTO_INCREMENT,
  `album_id`    INT          NOT NULL,
  `photo_url`   VARCHAR(255) NOT NULL,
  `description` TEXT         NULL,
  `taken_at`    DATETIME     NULL,
  `upload_by`   INT          NOT NULL,
  `created_at`  TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_album`  (`album_id`),
  INDEX `idx_upload` (`upload_by`),
  CONSTRAINT `fk_ap_album` FOREIGN KEY (`album_id`)  REFERENCES `albums`(`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_ap_user`  FOREIGN KEY (`upload_by`) REFERENCES `users`(`id`)  ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='相册照片表';


-- ============================================================
-- 共用表：attendance_records（考勤记录）
-- ============================================================
DROP TABLE IF EXISTS `attendance_records`;
CREATE TABLE `attendance_records` (
  `id`          INT      NOT NULL AUTO_INCREMENT,
  `child_id`    INT      NOT NULL,
  `date`        DATE     NOT NULL,
  `status`      ENUM('present','absent','late') NOT NULL,
  `notes`       TEXT     NULL,
  `recorded_by` INT      NOT NULL,
  `created_at`  TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_attendance` (`child_id`, `date`),
  INDEX `idx_recorder` (`recorded_by`),
  CONSTRAINT `fk_ar_child`    FOREIGN KEY (`child_id`)    REFERENCES `children`(`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_ar_recorder` FOREIGN KEY (`recorded_by`) REFERENCES `users`(`id`)   ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤记录表';


-- ============================================================
-- 共用表：messages（消息通知）
-- ============================================================
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages` (
  `id`          INT          NOT NULL AUTO_INCREMENT,
  `sender_id`   INT          NOT NULL,
  `receiver_id` INT          NOT NULL,
  `title`       VARCHAR(255) NOT NULL,
  `content`     TEXT         NOT NULL,
  `category`    ENUM('teacher','system','activity') NOT NULL,
  `is_read`     TINYINT(1)   NULL DEFAULT 0,
  `created_at`  TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_sender`   (`sender_id`),
  INDEX `idx_receiver` (`receiver_id`),
  CONSTRAINT `fk_msg_sender`   FOREIGN KEY (`sender_id`)   REFERENCES `users`(`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_msg_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `users`(`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知表';


-- ============================================================
-- 共用表：payment_records（缴费记录）
-- ============================================================
DROP TABLE IF EXISTS `payment_records`;
CREATE TABLE `payment_records` (
  `id`           INT            NOT NULL AUTO_INCREMENT,
  `child_id`     INT            NOT NULL,
  `amount`       DECIMAL(10,2)  NOT NULL,
  `payment_type` VARCHAR(50)    NOT NULL,
  `payment_date` DATE           NOT NULL,
  `status`       ENUM('paid','unpaid') NULL DEFAULT 'unpaid',
  `paid_by`      INT            NOT NULL,
  `notes`        TEXT           NULL,
  `created_at`   TIMESTAMP      NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`   TIMESTAMP      NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_child`   (`child_id`),
  INDEX `idx_paid_by` (`paid_by`),
  CONSTRAINT `fk_pr_child`   FOREIGN KEY (`child_id`) REFERENCES `children`(`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_pr_paid_by` FOREIGN KEY (`paid_by`)  REFERENCES `users`(`id`)   ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='缴费记录表';


-- ============================================================
-- 共用表：folders（文件夹，手机端使用）
-- ============================================================
DROP TABLE IF EXISTS `folders`;
CREATE TABLE `folders` (
  `id`         INT          NOT NULL AUTO_INCREMENT,
  `name`       VARCHAR(100) NOT NULL,
  `parent_id`  INT          NULL DEFAULT NULL,
  `user_id`    INT          NOT NULL,
  `created_at` TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_parent` (`parent_id`),
  INDEX `idx_user`   (`user_id`),
  CONSTRAINT `fk_folder_parent` FOREIGN KEY (`parent_id`) REFERENCES `folders`(`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_folder_user`   FOREIGN KEY (`user_id`)   REFERENCES `users`(`id`)   ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件夹表';


-- ============================================================
-- 共用表：system_settings（系统配置）
-- ============================================================
DROP TABLE IF EXISTS `system_settings`;
CREATE TABLE `system_settings` (
  `id`          INT          NOT NULL AUTO_INCREMENT,
  `key_name`    VARCHAR(100) NOT NULL,
  `value`       TEXT         NOT NULL,
  `description` TEXT         NULL,
  `created_at`  TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`  TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_key` (`key_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

INSERT INTO `system_settings` VALUES
  (1, 'system_name', '幼儿园资源管理系统', '系统名称', NOW(), NOW()),
  (2, 'school_name', '阳光幼儿园',         '幼儿园名称', NOW(), NOW()),
  (3, 'version',     '2.0.0',             '系统版本',  NOW(), NOW());


SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 初始化完成
-- 默认账号（密码均为 123456）：
--   管理员/园长：admin
--   教师：teacher1 / teacher2 / teacher3
--   保育员：care1
--   家长（手机端）：parent1
-- ============================================================
