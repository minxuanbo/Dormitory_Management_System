# 宿舍报修系统

基于 Spring Boot + MyBatis + Layui 的宿舍报修管理系统，支持 **学生、维修人员、管理员** 三个角色的协同工作：学生在线提交报修并评价服务，维修人员接单、处理并填写维修记录，管理员指派工单、管理紧急程度并查看数据统计与满意度分析。三个角色均可在个人中心查看 / 修改个人信息并修改登录密码。

## 功能概览

- **学生端**：提交报修（可上传图片）、查看报修进度、对已完成的工单进行 1-5 星评分与文字评价、个人中心
- **维修人员端**：查看指派给自己的工单、接单、填写维修记录、查询历史记录、个人中心
- **管理员端**：查看全部工单、指派维修人员、设置紧急程度、数据统计（ECharts 可视化）、满意度分析、用户管理（维修人员 / 学生）、宿舍管理（楼栋 / 房间）

## 技术栈

| 分类   | 技术                                           |
| ------ | ---------------------------------------------- |
| 后端   | Spring Boot 2.4.3 + Spring MVC + MyBatis + JWT |
| 前端   | Layui 2.5.5 + Axios + ECharts                  |
| 数据库 | MySQL 5.7+                                     |
| 构建   | Maven                                          |
| 语言   | Java 8+                                        |

## 环境要求

- JDK 1.8 及以上
- Maven 3.6 及以上
- MySQL 5.7 及以上

## 快速开始

### 1. 初始化数据库

项目根目录提供了 `init.sql` 初始化脚本（会自动创建 `dormitory_repair_system` 数据库、建表并插入示例数据与测试账号）。

```bash
# 使用你自己的 MySQL 账号执行
mysql -u root -p < init.sql
```

> 数据库名默认为 `dormitory_repair_system`，如需修改请同步修改 `init.sql` 中的 `CREATE DATABASE` 语句与 `application.yml` 中的连接地址。

### 2. 配置数据库连接

每个人的 MySQL 环境（地址、账号、密码）都不同，请根据自己本机情况修改
`src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dormitory_repair_system?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false
    username: # 改成你自己的数据库账号
    password: # 改成你自己的数据库密码
```

### 3. 打包并启动后端

```bash
# 在项目根目录执行
mvn clean package -DskipTests
java -jar target/dormitory-0.0.1-SNAPSHOT.jar
```

后端运行在 `http://localhost:8081/dormitory`。前端静态页面已通过 Maven 打包进 jar（`dormitoryfront/` → `classpath:/static/`），由 Spring Boot 统一提供，无需单独启动前端服务器。

> 如需修改端口或上下文路径，请修改 `application.yml` 中的 `server.port` 与 `server.servlet.context-path`。

### 4. 访问系统

浏览器打开登录页：

```
http://localhost:8081/dormitory/page/login.html
```

登录成功后根据账号角色自动跳转到学生端 / 维修端 / 管理员端首页（无需手动选择角色）。

## 测试账号

> 登录使用「用户名」（中文姓名），非数字 ID。

| 角色     | 用户名   | 密码   | 说明                                         |
| -------- | -------- | ------ | -------------------------------------------- |
| 学生     | 张同学   | 123456 | 提交报修、查看进度、服务评价                 |
| 学生     | 李同学   | 123456 | 备用学生账号                                 |
| 维修人员 | 李师傅   | 123456 | 接收工单、更新状态、填写维修记录             |
| 维修人员 | 王师傅   | 123456 | 备用维修人员账号                             |
| 管理员   | 赵管理员 | 123456 | 工单指派、紧急程度管理、数据统计、满意度分析 |

## 功能介绍

### 学生端

- **提交报修**：填写报修物品、描述、上传图片，系统自动获取所在房间信息
- **我的报修**：查看个人所有报修记录及进度
- **服务评价**：对已完成的维修工单进行 1-5 星评分和文字评价
- **个人中心**：查看个人资料（用户名、性别、宿舍等栏目只读），修改联系电话、邮箱，修改登录密码

### 维修人员端

- **工单列表**：查看指派给自己的工单，按状态筛选（待接单/维修中/已完成）
- **接单操作**：确认接收工单，状态自动变更为「维修中」
- **完成维修**：填写维修记录，完成工单
- **历史记录**：查看所有历史维修记录
- **个人中心**：查看个人资料（只读栏目），修改联系电话、邮箱，修改登录密码

### 管理员端

- **全部工单**：查看系统所有工单，支持按状态、紧急程度筛选
- **工单指派**：将待指派工单分配给维修人员
- **紧急程度管理**：设置工单紧急程度（普通/紧急/非常紧急）
- **数据统计**：ECharts 可视化统计各状态、各紧急程度工单数量及近 7 日趋势
- **满意度分析**：查看整体评分均值、评分分布与已评价工单列表
- **用户管理**：维修人员管理 / 学生管理（按角色维护系统用户）
- **宿舍管理**：楼栋管理 / 房间管理（维护楼栋与房间信息）

## 维修状态流转

```
学生提交 → [待指派] → 管理员指派 → [待接单] → 维修人员接单 → [维修中]
                                                    ↓
                  [已评价] ← 学生评价 ← [已完成] ← 完成维修
```

状态码：`0=待指派`，`1=待接单`，`2=维修中`，`3=已完成`，`4=已评价`

紧急度：`0=普通`，`1=紧急`，`2=非常紧急`

## 项目结构

```
├── src/main/java/com/silk/
│   ├── controller/     # 控制器层
│   │   ├── LoginController.java      # 登录（自动识别角色）
│   │   ├── RepairController.java     # 报修工单核心 API
│   │   ├── UserController.java       # 用户管理 + 个人中心（改密）
│   │   ├── BuildingController.java   # 楼栋管理
│   │   ├── RoomController.java       # 房间管理
│   │   ├── FileController.java       # 图片上传
│   │   └── ...
│   ├── entity/         # 实体类
│   ├── mapper/         # MyBatis Mapper 接口及 XML
│   ├── service/        # 业务逻辑层
│   ├── framework/      # 框架配置（JWT、拦截器、CORS）
│   └── utils/          # 工具类
├── src/main/resources/
│   ├── application.yml              # 应用配置（含数据库连接）
│   ├── banner.txt                   # 启动横幅
│   └── public/                      # 静态资源
├── dormitoryfront/                  # 前端页面（打包进 jar 的 classpath:/static/）
│   ├── page/
│   │   ├── login.html               # 登录页（自动识别角色跳转）
│   │   ├── student-index.html       # 学生端主页
│   │   ├── repairer-index.html      # 维修人员端主页
│   │   ├── admin-index.html         # 管理员端主页
│   │   ├── user-center.html         # 个人中心（各角色共用）
│   │   ├── student/                 # 学生端页面（报修列表 / 提交报修）
│   │   ├── repairer/                # 维修人员端页面（我的工单 / 历史记录）
│   │   ├── admin/                   # 管理员端页面（工单管理 / 统计 / 满意度）
│   │   ├── user/                    # 用户管理页面
│   │   ├── building/                # 楼栋管理页面
│   │   └── room/                    # 房间管理页面
│   ├── js/                          # JavaScript
│   └── lib/                         # 第三方库
└── init.sql                         # 数据库初始化脚本（建库建表 + 示例数据）
```

## API 接口

> 实际访问时需加上上下文路径，如 `http://localhost:8081/dormitory/user/query`。

### 通用

| 方法 | 路径         | 说明                   |
| ---- | ------------ | ---------------------- |
| POST | /login       | 登录（自动识别角色）   |
| POST | /file/upload | 图片上传               |

### 报修工单

| 方法 | 路径                 | 说明                           |
| ---- | -------------------- | ------------------------------ |
| POST | /repair/create       | 提交报修（JSON）               |
| POST | /repair/stu_create   | 学生端提交报修（含房间自动带出）|
| GET  | /repair/detail       | 工单详情                       |
| POST | /repair/query        | 全部工单查询（管理员）         |
| POST | /repair/my_list      | 我的报修列表（学生）           |
| POST | /repair/my_orders    | 我的工单列表（维修人员）       |
| POST | /repair/my_history   | 历史工单（维修人员）           |
| POST | /repair/assign       | 指派维修人员                   |
| POST | /repair/set_urgency  | 设置紧急程度                   |
| POST | /repair/accept       | 接单                           |
| POST | /repair/complete     | 完成维修（填写维修记录）       |
| POST | /repair/rate         | 学生评价工单                   |
| POST | /repair/statistics   | 数据统计                       |
| POST | /repair/satisfaction | 满意度分析                     |
| GET  | /repair/repairer_list| 维修人员列表                   |
| POST | /repair/update       | 更新工单（JSON）               |
| GET  | /repair/delete       | 删除工单                       |

### 用户管理 / 个人中心

| 方法 | 路径             | 说明                         |
| ---- | ---------------- | ---------------------------- |
| POST | /user/query      | 用户查询（支持按类型、ID 过滤）|
| POST | /user/create     | 新增用户（JSON）             |
| POST | /user/update     | 更新用户信息（JSON）         |
| GET  | /user/detail     | 用户详情                     |
| GET  | /user/delete     | 删除用户                     |
| POST | /user/update_pwd | 个人中心修改密码（原密码校验）|

### 宿舍管理

| 方法 | 路径                               | 说明                       |
| ---- | ---------------------------------- | -------------------------- |
| POST | /building/query                    | 楼栋查询                   |
| POST | /building/create                   | 新增楼栋（JSON）           |
| POST | /building/update                   | 更新楼栋（JSON）           |
| GET  | /building/delete                   | 删除楼栋                   |
| GET  | /building/query_floor_num          | 查询楼栋层数               |
| GET  | /building/occupancy_rate_and_gender| 入住率与性别统计           |
| POST | /room/query                        | 房间查询                   |
| POST | /room/create                       | 新增房间（JSON）           |
| POST | /room/update                       | 更新房间（JSON）           |
| GET  | /room/delete                       | 删除房间                   |
| GET  | /room/query_liver_amount           | 查询房间入住人数           |
| GET  | /room/query_livers                 | 查询房间入住学生           |
| GET  | /room/capacity_plus_one            | 入住人数 +1                |
| GET  | /room/capacity_minus_one           | 入住人数 -1                |
