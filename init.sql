-- ============================================================
-- 宿舍报修管理系统 数据库初始化脚本
-- 实际运行库请以 src/main/resources/application.yml 中的 datasource.url 为准
-- 默认账号：
--   学生：1001 / 123456（张同学）
--   学生：1002 / 123456（李同学）
--   维修：2001 / 123456（李师傅）
--   维修：2002 / 123456（王师傅）
--   管理：3001 / 123456（赵管理员）
-- 状态码：0=待指派 1=待接单 2=维修中 3=已完成 4=已评价
-- 紧急度：0=普通 1=紧急 2=非常紧急
-- ============================================================

CREATE DATABASE IF NOT EXISTS dormitory_repair_system DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE dormitory_repair_system;

-- ============================================
-- 1. 用户表
-- ============================================
DROP TABLE IF EXISTS tb_user;
CREATE TABLE tb_user (
    id INT PRIMARY KEY,
    user_pwd VARCHAR(50) NOT NULL,
    user_name VARCHAR(50) NOT NULL,
    gender INT DEFAULT 0 COMMENT '女=0；男=1',
    email VARCHAR(100),
    room_id INT DEFAULT NULL,
    user_type INT DEFAULT 0 COMMENT '学生=0；维修人员=1；管理员=2'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 学生账号 (user_type=0)，必须绑定房间号才能提交报修
INSERT INTO tb_user (id, user_pwd, user_name, gender, email, room_id, user_type) VALUES
(1001, '123456', '张同学', 1, 'student1@test.com', 100101, 0),
(1002, '123456', '李同学', 0, 'student2@test.com', 100102, 0);

-- 维修人员账号 (user_type=1)
INSERT INTO tb_user (id, user_pwd, user_name, gender, email, room_id, user_type) VALUES
(2001, '123456', '李师傅', 1, 'repair1@test.com', NULL, 1),
(2002, '123456', '王师傅', 1, 'repair2@test.com', NULL, 1);

-- 管理员账号 (user_type=2)
INSERT INTO tb_user (id, user_pwd, user_name, gender, email, room_id, user_type) VALUES
(3001, '123456', '赵管理员', 0, 'admin@test.com', NULL, 2);

-- ============================================
-- 2. 报修表
-- ============================================
DROP TABLE IF EXISTS tb_repair;
CREATE TABLE tb_repair (
    id INT PRIMARY KEY AUTO_INCREMENT,
    rep_item VARCHAR(200) DEFAULT NULL COMMENT '报修项目',
    description VARCHAR(500) DEFAULT NULL COMMENT '故障描述',
    rep_date DATETIME DEFAULT NULL COMMENT '报修时间',
    stu_id INT DEFAULT NULL COMMENT '报修学生ID',
    building_id INT DEFAULT NULL COMMENT '楼栋ID',
    room_id INT DEFAULT NULL COMMENT '房间ID',
    rep_status INT DEFAULT 0 COMMENT '0=待指派 1=待接单 2=维修中 3=已完成 4=已评价',
    rep_man VARCHAR(50) DEFAULT NULL COMMENT '维修人员姓名',
    urgency INT DEFAULT 0 COMMENT '紧急度 0=普通 1=紧急 2=非常紧急',
    repairer_id INT DEFAULT NULL COMMENT '维修人员ID',
    images VARCHAR(1000) DEFAULT NULL COMMENT '报修图片,逗号分隔',
    repair_record TEXT DEFAULT NULL COMMENT '维修记录',
    rating INT DEFAULT NULL COMMENT '评分 1-5',
    feedback VARCHAR(500) DEFAULT NULL COMMENT '评价内容',
    assigned_time DATETIME DEFAULT NULL COMMENT '派单时间',
    accepted_time DATETIME DEFAULT NULL COMMENT '接单时间',
    completed_time DATETIME DEFAULT NULL COMMENT '完成时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 示例工单数据
INSERT INTO tb_repair (rep_item, description, rep_date, stu_id, building_id, room_id, rep_status, rep_man, urgency, repairer_id, images, repair_record, rating, feedback, assigned_time, accepted_time, completed_time) VALUES
('水电维修', '宿舍水龙头漏水，已经滴了两天了', DATE_SUB(NOW(), INTERVAL 0 DAY), 1001, 1, 100101, 0, NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('门窗维修', '宿舍门锁坏了，关不上门', DATE_SUB(NOW(), INTERVAL 1 DAY), 1002, 1, 100102, 1, NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('家具维修', '书桌抽屉滑轨坏了，拉不开', DATE_SUB(NOW(), INTERVAL 2 DAY), 1002, 1, 100102, 2, '李师傅', 0, 2001, NULL, NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NULL),
('电器维修', '空调不制冷，吹出来的是热风', DATE_SUB(NOW(), INTERVAL 4 DAY), 1001, 1, 100101, 3, '李师傅', 2, 2001, NULL, '已更换压缩机电容，测试正常', NULL, NULL, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
('水电维修', '卫生间灯不亮，疑似灯泡烧坏', DATE_SUB(NOW(), INTERVAL 6 DAY), 1001, 1, 100101, 4, '王师傅', 0, 2002, NULL, '已更换LED灯泡', 4, '维修很快，服务态度好', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY));
