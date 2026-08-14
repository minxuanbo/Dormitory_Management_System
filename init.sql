-- ============================================================
-- 宿舍报修管理系统 数据库初始化脚本
-- 实际运行库请以 src/main/resources/application.yml 中的 datasource.url 为准
-- 默认账号：
--   学生：1001 / 123456（张同学，13栋7楼0701）
--   学生：1002 / 123456（李同学，13栋7楼0702）
--   维修：2001 / 123456（李师傅）
--   维修：2002 / 123456（王师傅）
--   管理：3001 / 123456（赵管理员）
-- 状态码：0=待指派 1=待接单 2=维修中 3=已完成 4=已评价
-- 紧急度：0=普通 1=紧急 2=非常紧急
-- 房间ID编码规则：楼栋ID*10000 + 楼层*100 + 房号
--   例：13栋7楼01房 = 13*10000 + 7*100 + 1 = 130701，门牌号(品牌号)为 0701
--   例： 1栋1楼01房 =  1*10000 + 1*100 + 1 =  10101，门牌号(品牌号)为 0101
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
    phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    email VARCHAR(100),
    room_id INT DEFAULT NULL COMMENT '房间ID（关联 tb_room.id）',
    user_type INT DEFAULT 0 COMMENT '学生=0；维修人员=1；管理员=2'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 学生账号 (user_type=0)，必须绑定房间号才能提交报修
INSERT INTO tb_user (id, user_pwd, user_name, gender, phone, email, room_id, user_type) VALUES
(1001, '123456', '张同学', 1, '13800001001', 'student1@test.com', 130701, 0),
(1002, '123456', '李同学', 0, '13800001002', 'student2@test.com', 130702, 0);

-- 维修人员账号 (user_type=1)
INSERT INTO tb_user (id, user_pwd, user_name, gender, phone, email, room_id, user_type) VALUES
(2001, '123456', '李师傅', 1, '13900002001', 'repair1@test.com', NULL, 1),
(2002, '123456', '王师傅', 1, '13900002002', 'repair2@test.com', NULL, 1);

-- 管理员账号 (user_type=2)
INSERT INTO tb_user (id, user_pwd, user_name, gender, phone, email, room_id, user_type) VALUES
(3001, '123456', '赵管理员', 0, '13700003001', 'admin@test.com', NULL, 2);

-- ============================================
-- 2. 楼栋表
-- ============================================
DROP TABLE IF EXISTS tb_building;
CREATE TABLE tb_building (
    id INT PRIMARY KEY COMMENT '楼栋编号',
    building_name VARCHAR(50) NOT NULL COMMENT '楼栋名称（如：国光公寓13栋）',
    floor_num INT DEFAULT 6 COMMENT '总层数',
    liver_gender INT DEFAULT 1 COMMENT '入住学生性别：女=0；男=1',
    manager_id INT DEFAULT NULL COMMENT '维修人员ID（负责本楼栋，关联 tb_user.id，user_type=1）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 示例楼栋：国光公寓13栋（12层，每层20间，7楼有0701-0720）
INSERT INTO tb_building (id, building_name, floor_num, liver_gender, manager_id) VALUES
(1,  '国光公寓1栋',  6, 1, NULL),
(13, '国光公寓13栋', 12, 1, 2001);

-- ============================================
-- 3. 房间表
-- ============================================
DROP TABLE IF EXISTS tb_room;
CREATE TABLE tb_room (
    id INT PRIMARY KEY COMMENT '房间ID编码：楼栋ID*10000+楼层*100+房号（如13栋7楼01房=130701）',
    building_id INT NOT NULL COMMENT '楼栋ID',
    floor INT NOT NULL COMMENT '楼层',
    brand VARCHAR(20) NOT NULL COMMENT '门牌号（如0701）',
    room_capacity INT DEFAULT 4 COMMENT '房间容量（床位数）',
    room_type INT DEFAULT 0 COMMENT '0=学生房间 1=宿管 2=后勤办公室 3=招待所 4=小卖部 5=杂物间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 生成指定楼栋的房间：CALL gen_rooms(楼栋ID, 总层数, 每层房间数)
DELIMITER $$
DROP PROCEDURE IF EXISTS gen_rooms $$
CREATE PROCEDURE gen_rooms(IN bId INT, IN floors INT, IN rooms INT)
BEGIN
    DECLARE f INT DEFAULT 1;
    DECLARE r INT DEFAULT 1;
    WHILE f <= floors DO
        SET r = 1;
        WHILE r <= rooms DO
            INSERT INTO tb_room (id, building_id, floor, brand, room_capacity, room_type)
            VALUES (bId*10000 + f*100 + r, bId, f, LPAD(f*100 + r, 4, '0'), 4, 0);
            SET r = r + 1;
        END WHILE;
        SET f = f + 1;
    END WHILE;
END $$
DELIMITER ;

-- 国光公寓1栋：6层，每层20间（0101-0120 ... 0601-0620）
CALL gen_rooms(1, 6, 20);
-- 国光公寓13栋：12层，每层20间（0701-0720 ... 1201-1220）
CALL gen_rooms(13, 12, 20);
DROP PROCEDURE gen_rooms;

-- ============================================
-- 4. 报修表
-- ============================================
DROP TABLE IF EXISTS tb_repair;
CREATE TABLE tb_repair (
    id INT PRIMARY KEY AUTO_INCREMENT,
    rep_item VARCHAR(200) DEFAULT NULL COMMENT '报修项目',
    description VARCHAR(500) DEFAULT NULL COMMENT '故障描述',
    rep_date DATETIME DEFAULT NULL COMMENT '报修时间',
    stu_id INT DEFAULT NULL COMMENT '报修学生ID',
    building_id INT DEFAULT NULL COMMENT '楼栋ID（关联 tb_building.id）',
    room_id INT DEFAULT NULL COMMENT '房间ID（关联 tb_room.id）',
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

-- 示例工单数据（学生住在13栋）
INSERT INTO tb_repair (rep_item, description, rep_date, stu_id, building_id, room_id, rep_status, rep_man, urgency, repairer_id, images, repair_record, rating, feedback, assigned_time, accepted_time, completed_time) VALUES
('水电维修', '宿舍水龙头漏水，已经滴了两天了', DATE_SUB(NOW(), INTERVAL 0 DAY), 1001, 13, 130701, 0, NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('门窗维修', '宿舍门锁坏了，关不上门', DATE_SUB(NOW(), INTERVAL 1 DAY), 1002, 13, 130702, 1, NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('家具维修', '书桌抽屉滑轨坏了，拉不开', DATE_SUB(NOW(), INTERVAL 2 DAY), 1002, 13, 130702, 2, '李师傅', 0, 2001, NULL, NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NULL),
('电器维修', '空调不制冷，吹出来的是热风', DATE_SUB(NOW(), INTERVAL 4 DAY), 1001, 13, 130701, 3, '李师傅', 2, 2001, NULL, '已更换压缩机电容，测试正常', NULL, NULL, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
('水电维修', '卫生间灯不亮，疑似灯泡烧坏', DATE_SUB(NOW(), INTERVAL 6 DAY), 1001, 13, 130701, 4, '王师傅', 0, 2002, NULL, '已更换LED灯泡', 4, '维修很快，服务态度好', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY));

-- ============================================
-- 5. 已有数据库升级脚本（仅当数据库已存在、不想重建时手动执行）
-- ============================================
-- ALTER TABLE tb_user ADD COLUMN phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话' AFTER gender;
-- CREATE TABLE tb_building (
--     id INT PRIMARY KEY COMMENT '楼栋编号',
--     building_name VARCHAR(50) NOT NULL COMMENT '楼栋名称',
--     floor_num INT DEFAULT 6 COMMENT '总层数',
--     liver_gender INT DEFAULT 1 COMMENT '入住学生性别：女=0；男=1',
--     manager_id INT DEFAULT NULL COMMENT '维修人员ID'
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- CREATE TABLE tb_room (
--     id INT PRIMARY KEY COMMENT '房间ID：楼栋ID*10000+楼层*100+房号',
--     building_id INT NOT NULL,
--     floor INT NOT NULL,
--     brand VARCHAR(20) NOT NULL COMMENT '门牌号',
--     room_capacity INT DEFAULT 4,
--     room_type INT DEFAULT 0
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
