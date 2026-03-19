# 社区后端学习笔记

## 项目计划

### 总体说明

目标是在 `D:\CodeField\community-backend` 从 0 搭一个 Spring Boot 3.x 社区平台后端，技术栈为 Spring Boot 3.x、MyBatis-Plus、MySQL、Redis、RabbitMQ、Spring Security + JWT、Docker，JDK 固定为 21。

推进方式不是一次性全部做完，而是按“先能跑、再接依赖、再做业务、最后容器化”的顺序，一步一步搭。每个阶段都要求可以独立验证，同时也要求我真正理解这一阶段的开发流程和知识点。

### 第 1 阶段：准备目录和最小 Spring Boot 骨架

目标：
- 先让项目本地跑起来

你要做的事：
- 把目录名固定为 `community-backend`
- 创建 Maven 工程
- 设置 Java 21
- 只接入最小依赖：Web、Validation、Lombok、Test
- 建立最基础结构：
  - 启动类
  - `GET /api/health`
  - 统一返回体
  - 基础 `application.yml`

手把手学习内容：
1. 检查环境
   - `java -version`
   - `mvn -version`
2. 创建项目骨架
3. 理解 `pom.xml` 的核心配置
4. 写第一个接口
5. 用 `mvn spring-boot:run` 启动
6. 用浏览器或 Apifox/Postman 访问 `/api/health`
7. 再学会打包成 jar，并用 `java -jar` 启动一次

这一阶段要学会：
- Spring Boot 项目最小结构
- Maven 项目怎么启动
- 本地开发时最基本的运行方式

阶段产出：
- 一个能启动、能访问健康检查接口的后端项目

### 第 2 阶段：用 Docker 跑 MySQL、Redis、RabbitMQ

目标：
- 不在 Windows 手动安装中间件，而是用 Docker 启动依赖服务

你要做的事：
- 安装并检查 Docker Desktop
- 学会最基本命令：
  - `docker --version`
  - `docker compose version`
  - `docker ps`
  - `docker logs`
- 编写 `docker-compose.yml`
- 拉起 3 个依赖：
  - MySQL
  - Redis
  - RabbitMQ

手把手学习内容：
1. 解释什么是镜像、容器、端口映射、数据卷
2. 写 `docker-compose.yml`
3. 理解每个服务为什么这样配
4. 执行 `docker compose up -d`
5. 检查容器是否真的启动成功
6. 学会看日志定位错误
7. 访问 RabbitMQ 管理后台
8. 确认 MySQL 和 Redis 端口是否可用

这一阶段要学会：
- 为什么开发时常用“本地应用 + Docker 中间件”
- Docker Compose 的基本使用
- 如何验证容器服务是否真的可用

阶段产出：
- 一个能稳定启动 MySQL、Redis、RabbitMQ 的 `docker-compose.yml`

### 第 3 阶段：Spring Boot 连接 MySQL / Redis / RabbitMQ

目标：
- 让本地 Spring Boot 应用连上 Docker 中间件

你要做的事：
- 增加数据库、缓存、消息队列相关依赖
- 配置 `application.yml`
- 先做“连接验证”，不急着做完整业务

手把手学习内容：
1. 接入 MySQL 驱动和 MyBatis-Plus
2. 配置数据源
3. 学会建第一个数据库和表
4. 做一个最简单的数据库读写接口
5. 接入 Redis
6. 做一个简单缓存接口，演示 set/get
7. 接入 RabbitMQ
8. 做一个最小消息发送与消费示例

建议：
- 这一阶段的最小业务对象先用 `user`，因为后面鉴权也要用它

这一阶段要学会：
- Spring Boot 怎么连接外部依赖
- `localhost` 连接 Docker 容器暴露端口的原理
- MyBatis-Plus 最基本 CRUD 流程
- Redis 和 RabbitMQ 的最小使用方法

阶段产出：
- MySQL 可连接
- Redis 可读写
- RabbitMQ 可收发消息
- 第一个可落库的简单模块

### 第 4 阶段：接入 Spring Security + JWT，完成认证闭环

目标：
- 先完成认证系统，再进入社区业务

你要做的事：
- 接入 Spring Security
- 实现 JWT 认证
- 采用 Access Token + Refresh Token
- 完成认证相关接口：
  - 注册
  - 登录
  - 刷新令牌
  - 获取当前用户信息

手把手学习内容：
1. 理解 Spring Security 的核心链路
2. 配置放行路径和受保护路径
3. 编写 JWT 工具类
4. 实现登录成功签发令牌
5. 实现鉴权过滤器
6. 测试带 Token 与不带 Token 的区别
7. 实现 Refresh Token 逻辑
8. 如果需要，用 Redis 存储刷新令牌状态

这一阶段要学会：
- JWT 适合解决什么问题
- Spring Security 的最小实战配置
- 一个后端项目里登录鉴权是怎么串起来的

阶段产出：
- 可注册
- 可登录
- 可刷新令牌
- 可访问受保护接口

### 第 5 阶段：完成社区 MVP 业务

目标：
- 把社区后端最核心的业务做出来

首版业务范围固定为：
- 用户
- 帖子
- 评论
- 点赞
- 通知

实现顺序固定为：
1. 帖子
   - 发帖
   - 帖子列表
   - 帖子详情
2. 评论
   - 发表评论
   - 评论列表
3. 点赞
   - 帖子点赞/取消点赞
   - 评论点赞/取消点赞
4. 通知
   - 评论通知
   - 点赞通知
   - 通知列表
   - 已读处理

RabbitMQ 的使用方式固定为：
- 用户评论或点赞时发送消息
- 消费者异步生成站内通知
- 先不做邮件、短信、推送

Redis 的使用方式固定为：
- 热点帖子缓存
- 点赞计数缓存
- 登录/刷新令牌辅助状态

这一阶段要学会：
- 常见社区业务的数据模型怎么拆
- Controller / Service / Mapper 的职责分工
- 哪些场景适合缓存
- 哪些场景适合消息异步化

阶段产出：
- 一个可用的社区 MVP 后端 API

### 第 6 阶段：应用容器化和一键启动

目标：
- 在已经理解本地开发方式后，再把应用本身也放进 Docker

你要做的事：
- 编写 Dockerfile
- 为应用增加 `docker` profile
- 调整 `docker-compose.yml`
- 把 `app + mysql + redis + rabbitmq` 一起编排

手把手学习内容：
1. 区分开发态和部署态配置
2. 写应用镜像构建文件
3. 理解为什么容器内不能再用 `localhost` 连其他容器
4. 改成容器服务名互联
5. 执行整套 `docker compose up -d`
6. 验证完整环境是否可跑通

这一阶段要学会：
- 本地运行和容器运行的差异
- Dockerfile 的基本结构
- Docker Compose 编排多个服务的实际方法

阶段产出：
- 整套项目可通过 Docker 一键启动

### 首版接口规划

首版接口范围固定为：
- `GET /api/health`
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `GET /api/users/me`
- `GET /api/posts`
- `GET /api/posts/{id}`
- `POST /api/posts`
- `GET /api/posts/{postId}/comments`
- `POST /api/posts/{postId}/comments`
- `POST /api/likes`
- `GET /api/notifications`
- `POST /api/notifications/{id}/read`

返回格式固定为统一结构：
- `code`
- `message`
- `data`

分页返回固定包含：
- `page`
- `pageSize`
- `total`
- `list`

### 分阶段验收标准

每一阶段都单独验收，不跳步。

第 1 阶段：
- `mvn spring-boot:run` 能启动
- `/api/health` 返回 200

第 2 阶段：
- `docker compose up -d` 后 3 个容器都在线
- 能查看容器日志
- RabbitMQ 管理后台可打开
- MySQL 和 Redis 端口能从本机访问

第 3 阶段：
- Spring Boot 能连接 MySQL
- 一个简单表可正常增删查
- Redis set/get 成功
- RabbitMQ 生产和消费样例跑通

第 4 阶段：
- 注册成功
- 登录成功并拿到 Access/Refresh Token
- 受保护接口在无 Token 时被拒绝
- 带有效 Token 时可访问
- 刷新令牌可正常换发

第 5 阶段：
- 发帖、评论、点赞接口可用
- 点赞计数和状态正确
- 评论/点赞会生成通知
- 通知列表和已读逻辑可用

第 6 阶段：
- 应用镜像可构建
- 整套 Compose 可拉起
- 接口在容器化环境下可正常访问

### 固定前提

- 项目根目录固定为 `D:\CodeField\community-backend`
- 使用 Maven
- JDK 固定为 21
- 开发阶段采用“Spring Boot 本地运行 + 中间件 Docker 运行”
- 首版不包含前端、文件上传、搜索、关注、审核后台
- 认证模型固定为 Access Token + Refresh Token
- 角色先保留 USER 和 ADMIN，首版只实现普通用户主流程

## 目前学到的知识点

### 一、环境配置

- JDK 不只是“在 IDEA 里看得到”就行，还必须真正配置正确。
- 如果 IDEA 提示 `Project JDK is misconfigured`，说明 JDK 记录可能还在，但实际已经失效。
- 用 `Add JDK from disk...` 重新添加 JDK，可以修复 SDK 配置问题。
- 很多看起来像代码报错的问题，本质上其实是环境问题。

### 二、项目初始化

- 项目根目录从一开始就要建对。
- 要避免出现 `community-backend/community-backend` 这种重复嵌套目录，除非是有意这样设计。
- `pom.xml` 用来定义项目依赖和构建方式。
- Spring Boot 做 REST 接口最基础的依赖是 `spring-boot-starter-web`。

### 三、Spring Boot 基础

- `@SpringBootApplication` 表示这是 Spring Boot 的启动类。
- `SpringApplication.run(...)` 用来启动整个应用。
- `application.yml` 用来写项目配置。
- `server.port` 决定应用监听的端口。

### 四、Controller 基础

- `@RestController` 表示这个类直接返回 HTTP 响应数据。
- `@GetMapping("/api/health")` 表示把 GET 请求映射到一个方法上。
- 用 `Map.of(...)` 可以很快构造一个简单的 JSON 返回。

### 五、统一返回格式

- 统一返回格式可以让所有接口风格一致，后面前端对接也更方便。
- `ApiResponse<T>` 用来包装真正的业务数据。
- 常见结构如下：

```json
{
  "code": 200,
  "message": "OK",
  "data": {}
}
```

### 六、Docker 中间件基础

- `docker-compose.yml` 用来一次性定义多个服务。
- `image` 指定使用哪个镜像。
- `ports` 用来把容器端口映射到宿主机端口。
- `environment` 用来配置用户名、密码和初始化参数。
- `volumes` 用来做数据持久化。

### 七、Docker 环境排查

- `docker --version` 正常，说明 Docker 命令已安装。
- `docker compose version` 正常，说明 Compose 功能可用。
- 如果 `docker ps` 报无法连接 `dockerDesktopLinuxEngine`，通常表示 Docker Desktop 还没有完全启动。
- 当 Docker Desktop 完全启动后，再执行 `docker ps` 就会恢复正常。
- `docker ps` 结果为空，不代表 Docker 有问题，只表示当前没有运行中的容器。

### 八、当前已掌握的 Docker 实操

- 已安装并启动 Docker Desktop。
- 已确认 `docker --version` 可用。
- 已确认 `docker compose version` 可用。
- 已确认 `docker ps` 可正常执行。
- 已完成 `docker-compose.yml` 的创建。
- 已能通过 `docker compose up -d` 启动中间件。
- 已能访问 RabbitMQ 管理后台。

### 九、数据库接入与最小 CRUD 排错

- 应用能启动，不代表数据库一定可用；如果 MySQL 容器没启动，访问数据库接口时仍然会报 `500`。
- 排查数据库相关 `500` 时，不能只看 HTTP 响应，还要看 IDEA 控制台里的异常栈。
- `GET /api/users/{id}` 返回 `200 + data: null`，通常说明查询执行成功了，但数据库里没有这条 id 对应的数据。
- 这类场景下不要先怀疑代码错，应该先去数据库里执行 `SELECT` 看真实数据。
- `POST /api/users` 报 `Duplicate entry ... for key 'user.username'`，说明不是插入逻辑坏了，而是数据库唯一约束生效了。
- `UNIQUE` 约束会阻止重复用户名插入，这是数据库在帮应用做数据约束。

### 十、主键与数据观察

- 用户表里的 id 不一定是 `1、2、3` 这种自增值。
- 如果插入后看到很大的 Long 类型 id，通常说明主键是程序生成的分布式 id，而不是简单自增。
- 所以接口测试时，`GET /api/users/1` 查不到数据，不代表查询接口错了，可能只是 id 用错了。
- 正确做法是先查数据库里真实存在的 id，再用真实 id 访问查询接口。

### 十一、日志与真实原因

- HTTP 接口只会告诉我“结果错了”，但不会总是告诉我“为什么错了”。
- 控制台异常栈中的 `Cause:` 和 SQL 错误行是定位问题的关键。
- 常见需要重点看的报错类型：
  - `DuplicateKeyException`
  - `SQLIntegrityConstraintViolationException`
  - `Table doesn't exist`
  - `Unknown column`
  - 数据源连接失败相关异常

### 十二、当前 user 模块阶段性理解

- 现在已经验证过：Spring Boot -> Controller -> Service -> Mapper -> MySQL 这条链路是通的。
- 当前最小 CRUD 已基本跑通，问题更多集中在“数据状态”和“数据库约束”，而不是基础连接。
- `POST` 和 `GET` 接口表现不一样时，要分别判断，不要简单认为整个模块都坏了。
- 后端排错时，先确认：
  - 应用是否启动
  - Docker 是否启动
  - MySQL 容器是否在线
  - 表是否存在
  - 数据是否真的插入成功
  - 控制台异常栈说了什么

## 当前进度

- 已创建 Spring Boot 项目
- 已解决 JDK 配置问题
- 已成功运行健康检查接口
- 已创建 `ApiResponse`
- 已把 `HealthController` 改为统一返回格式
- 已安装 Docker Desktop
- 已启动并验证 Docker 环境
- 已完成 Docker 中间件阶段基础验证
- 已完成 MySQL 接入
- 已完成 `user` 表最小 CRUD 首轮验证
- 已掌握数据库约束与控制台日志排错的基础方法
- 已准备继续完善 user 模块或进入 Redis / RabbitMQ 接入阶段

## 下一步

1. 学会查看容器状态和日志
2. 引入 MySQL 驱动和 MyBatis-Plus
3. 配置数据源
4. 创建第一张 `user` 表并完成最小 CRUD
