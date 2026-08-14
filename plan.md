# 宿舍报修系统 改造计划

## Context

将现有的「宿舍管理系统」改造为专注于**宿舍报修**的场景，拆分为三个核心角色：学生、维修人员、管理员。保留现有 Spring Boot + MyBatis + layui 技术栈和代码模式，最大化复用现有代码，扩展而非重写。

## 核心改造点

### 1. 登录改造 — 自动识别角色

**问题**：现有登录需要用户手动在下拉框选择角色（学生/宿管员/后勤中心）

**方案**：
- 后端 `LoginController` 不再要求 `userType` 参数，只根据 `userName` + `userPwd` 查询
- 修改 `UserMapper.xml` 的 login 查询，去掉 `user_type = #{userType}` 条件
- 前端 `login.html` 移除角色下拉框，登录成功后根据返回的 `userType` 自动跳转

### 2. 角色重新定义

| 旧角色 | 旧 userType | 新角色 | 新 userType |
|--------|------------|--------|------------|
| 学生 | 0 | 学生 | 0 |
| 宿管员 | 1 | 维修人员 | 1 |
| 后勤中心 | 2 | 管理员 | 2 |

### 3. 数据库改造

#### 3.1 扩展 tb_repair 表（核心）
```sql
ALTER TABLE tb_repair ADD COLUMN urgency INT DEFAULT 0 COMMENT '0=普通 1=紧急 2=非常紧急';
ALTER TABLE tb_repair ADD COLUMN repairer_id INT DEFAULT NULL COMMENT '维修人员ID';
ALTER TABLE tb_repair ADD COLUMN images VARCHAR(1000) DEFAULT NULL COMMENT '报修图片,逗号分隔';
ALTER TABLE tb_repair ADD COLUMN repair_record TEXT DEFAULT NULL COMMENT '维修记录';
ALTER TABLE tb_repair ADD COLUMN rating INT DEFAULT NULL COMMENT '评分 1-5';
ALTER TABLE tb_repair ADD COLUMN feedback VARCHAR(500) DEFAULT NULL COMMENT '评价内容';
ALTER TABLE tb_repair ADD COLUMN assigned_time DATETIME DEFAULT NULL COMMENT '派单时间';
ALTER TABLE tb_repair ADD COLUMN accepted_time DATETIME DEFAULT NULL COMMENT '接单时间';
ALTER TABLE tb_repair ADD COLUMN completed_time DATETIME DEFAULT NULL COMMENT '完成时间';
```

#### 3.2 更新默认用户
```sql
-- 学生
INSERT INTO tb_user VALUES (1001,'123456','张同学',1,'student@test.com',NULL,0);
-- 维修人员（两个）
INSERT INTO tb_user VALUES (2001,'123456','李师傅',1,'repair1@test.com',NULL,1);
INSERT INTO tb_user VALUES (2002,'123456','王师傅',1,'repair2@test.com',NULL,1);
-- 管理员
INSERT INTO tb_user VALUES (3001,'123456','赵管理',0,'admin@test.com',NULL,2);
```

#### 3.3 重建菜单体系
```sql
-- 学生菜单（user_type=0）
-- 我的报修、提交报修、服务评价

-- 维修人员菜单（user_type=1）
-- 工单列表、我的维修记录

-- 管理员菜单（user_type=2）
-- 工单管理、工单指派、紧急程度管理、数据统计、满意度分析
```

### 4. 后端改动

#### 4.1 修改文件
| 文件 | 改动 |
|------|------|
| `LoginController.java` | 移除 userType 参数要求 |
| `UserMapper.xml` | login 查询去掉 user_type 条件 |
| `entity/Repair.java` | 新增 urgency, repairerId, images, repairRecord, rating, feedback, assignedTime, acceptedTime, completedTime 字段 |
| `mapper/RepairMapper.xml` | 全部 CRUD 查询补齐新字段 |
| `controller/RepairController.java` | 新增：工单指派、状态流转、评分评价、统计接口 |

#### 4.2 新增文件
| 文件 | 用途 |
|------|------|
| `controller/FileController.java` | 报修图片上传接口（存储到 /uploads/ 目录） |

#### 4.3 关键 API 设计

**学生端**：
- `POST /repair/stu_create` — 提交报修（含图片路径）
- `POST /repair/my_list` — 我的报修列表（含进度时间线）
- `POST /repair/rate` — 对已完成工单评分评价

**维修人员端**：
- `POST /repair/my_orders` — 我的工单（被指派的）
- `POST /repair/accept` — 接单（状态 → 维修中）
- `POST /repair/complete` — 完成维修（填写维修记录）
- `POST /repair/my_history` — 我的历史工单

**管理员端**：
- `POST /repair/assign` — 指派维修人员
- `POST /repair/set_urgency` — 设置紧急程度
- `POST /repair/statistics` — 数据统计（按状态/紧急度/维修人员）
- `POST /repair/satisfaction` — 满意度分析（平均分/评价分布）

**通用**：
- `POST /file/upload` — 图片上传

### 5. 前端改动

#### 5.1 修改文件
| 文件 | 改动 |
|------|------|
| `login.html` | 移除角色选择下拉框，只保留用户名+密码 |
| `index.html` | 更新系统标题为「宿舍报修系统」 |

#### 5.2 新增文件（学生端）
| 文件 | 内容 |
|------|------|
| `page/student/repair-list.html` | 我的报修列表：状态标签+进度时间线+评分入口 |
| `page/student/repair-add.html` | 提交报修表单：物品、描述、图片上传、房间信息 |
| `page/student/repair-rate.html` | 评分弹窗：1-5星 + 文字评价 |

#### 5.3 新增文件（维修人员端）
| 文件 | 内容 |
|------|------|
| `page/repairer/order-list.html` | 工单列表：待接单/维修中/已完成，操作按钮 |
| `page/repairer/order-complete.html` | 完成维修弹窗：维修记录表单 |
| `page/repairer/history.html` | 历史工单记录 |

#### 5.4 新增文件（管理员端）
| 文件 | 内容 |
|------|------|
| `page/admin/repair-manage.html` | 全部工单管理：筛选、指派、设置紧急程度 |
| `page/admin/repair-assign.html` | 指派弹窗：选择维修人员 |
| `page/admin/statistics.html` | 数据统计面板：echarts图表 |
| `page/admin/satisfaction.html` | 满意度分析：评分分布、评价列表 |

#### 5.5 前端公共改动
- `js/lay-config.js` 保持不变
- axios 拦截器保持不变
- 图片上传使用 layui upload 组件

### 6. 维修状态流转

```
学生提交 → [待指派] → 管理员指派 → [待接单] → 维修人员接单 → [维修中] → 完成维修 → [已完成] → 学生评价 → [已评价]
                                                                                    └→ 学生不评价(保持已完成)
```

状态码: 0=待指派, 1=待接单, 2=维修中, 3=已完成, 4=已评价

### 7. 实现顺序

1. **数据库** — 修改表结构 + 更新用户数据 + 更新菜单数据
2. **后端实体/Mapper** — Repair 实体 + RepairMapper.xml 更新
3. **登录改造** — LoginController + UserMapper + login.html
4. **后端 API** — RepairController 新接口 + FileController
5. **前端页面** — 按角色：学生端 → 维修人员端 → 管理员端
6. **联调测试** — 全流程测试

### 8. 验证方法

1. 启动 MySQL → 执行数据库迁移脚本 → 启动后端 → 启动前端
2. 用学生账号登录 → 提交报修（含图片）→ 查看报修进度
3. 用管理员登录 → 查看工单 → 指派维修人员 → 设置紧急程度
4. 用维修人员登录 → 查看工单 → 接单 → 填写记录 → 完成
5. 切回学生 → 查看已完成的工单 → 评分评价
6. 管理员 → 查看统计面板和满意度分析
