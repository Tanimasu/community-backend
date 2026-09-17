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

### 十三、开发环境换到 macOS

- 项目最早固定在 `D:\CodeField\community-backend`，现在在 macOS 上也跑通了，路径是 `~/Codefield/community-backend`。
- macOS 上用 Homebrew 装 JDK 21、Maven 和 Docker Desktop。
- Homebrew 装的 JDK 不会自动注册给系统，要在 `~/.zshrc` 里配置 `JAVA_HOME`。
- macOS 自带的 `/usr/bin/java` 会读 `JAVA_HOME`，所以配好 `JAVA_HOME` 后 `java -version` 就是对的。
- 装 Maven 时会顺带装最新版 JDK，所以更要显式指定 `JAVA_HOME`，否则用的不是 21。
- 结论：代码是跨平台的，真正需要适配的是环境变量和命令行工具。

### 十四、Redis 最小使用

- `spring-boot-starter-data-redis` 会自动提供 `StringRedisTemplate`。
- `StringRedisTemplate` 把 key 和 value 都当字符串存，在 `redis-cli` 里能直接看懂。
- 另一个 `RedisTemplate` 默认用 Java 序列化，存进去在 `redis-cli` 里是乱码。
- `opsForValue()` 对应 Redis 的 String 类型，`set` 就是 `SET`，带过期时间的 `set` 就是 `SET key value EX 秒数`。
- `getExpire` 返回 `-1` 表示永不过期，`-2` 表示 key 不存在。
- Redis 没有"表"的概念，所以 key 要用 `业务:xxx` 这种前缀区分，例如 `post:detail:1`、`auth:refresh:xxx`。

### 十五、RabbitMQ 最小使用

- 生产者不直接发给队列，而是发给交换机（Exchange），交换机按 routing key 投递到队列。
- `DirectExchange` 是最简单的交换机：routing key 完全匹配才投递。
- Exchange、Queue、Binding 都注册成 `@Bean`，应用启动时会自动在 RabbitMQ 里创建。
- `Jackson2JsonMessageConverter` 让消息体是 JSON，管理后台里能看懂；不配的话默认用 Java 序列化。
- `@RabbitListener` 的方法运行在独立线程里，日志中的线程名是 `ntContainer#x-x`，不是处理 HTTP 请求的线程。
- 接口把消息发出去就返回了，不代表消息已经被处理，这就是异步。

### 十六、Spring Security + JWT

- 加了 Spring Security 依赖之后，默认所有接口都需要登录，要在 `SecurityFilterChain` 里显式放行。
- 前后端分离 + Token 认证时，CSRF、表单登录、Basic 认证都可以关掉，Session 也设为 `STATELESS`。
- 放行 `/error` 很重要：Controller 抛异常时 Spring Boot 会转发到 `/error`，不放行的话真实错误会被 401 盖住。
- JWT 由 `header.payload.signature` 三段组成，payload 只是 Base64 编码，**不是加密**，绝对不能放密码。
- JWT 的安全性来自签名：改了 payload 签名就对不上。
- 自定义的 JWT 过滤器不要加 `@Component`，否则会被 Spring Boot 再注册一次，一个请求被处理两遍。
- 过滤器只负责"认出你是谁"，Token 无效时不抛异常，放行与否交给授权规则决定。
- 401 和 403 发生在过滤器阶段，`@RestControllerAdvice` 捕获不到，要实现 `AuthenticationEntryPoint` 和 `AccessDeniedHandler` 自己写 JSON。

### 十七、Access Token 与 Refresh Token

- JWT 一旦签发，在过期前无法撤销，所以 Access Token 要设得短（15 分钟）。
- Refresh Token 设得长（7 天），用来换新的 Access Token，用户不用频繁登录。
- Refresh Token 的状态存在 Redis 里，它就变成可撤销的：登出时删掉 key 即可。
- 每个 Refresh Token 只能用一次，刷新时换发新的，旧的立即失效。
- 判断有效性用 Redis `delete` 的返回值，而不是"先查再删"。`delete` 是原子操作，并发时只有一个能成功。
- Token 里要有 `type` 字段区分 access / refresh，否则长期有效的 Refresh Token 能直接当 Access Token 用。
- 登出后 Access Token 在剩余有效期内仍然能用，这是"无状态"的取舍。要立刻失效就得每次请求查黑名单，那就不是无状态了。

### 十八、密码与安全细节

- 密码用 BCrypt 加密后存库，同样的密码每次加密结果都不一样（自动加随机盐）。
- 验证密码要用 `matches`，不能自己加密一遍再比较字符串。
- 登录失败时，"用户不存在"和"密码错误"要返回同一句话，避免别人试探哪些用户名已注册。
- 实体类（Entity）对应数据库，DTO 对应接口，两者要分开，否则数据库加字段可能不小心暴露到接口上（比如 password）。
- 接口需要登录，不等于登录了就能操作任何数据。标记通知已读时要判断这条通知是不是自己的，否则就是越权访问漏洞。

### 十九、分页

- MyBatis-Plus 要注册 `PaginationInnerInterceptor` 才能分页。
- **忘了注册不会报错**，只是不加 `LIMIT`，把整张表都查出来，数据量小的时候完全看不出来。
- `selectPage` 会执行两条 SQL：一条 `COUNT` 算总数，一条带 `LIMIT` 取当前页。
- `pageSize` 必须限制上限（比如 50），否则有人传 `pageSize=100000` 就能拖垮数据库。
- 分页返回统一为 `page / pageSize / total / list`。

### 二十、N+1 查询

- 列表里每条数据都单独查一次关联信息（比如每个帖子查一次作者），就是 N+1 查询，数据量一大就很慢。
- 正确做法：先收集这一页所有 `userId`，用 `IN` 一次查出来，放进 Map 再逐个取。
- 不管一页有多少条，查作者都只有一条 SQL。
- 查之前记得 `distinct` 去重。

### 二十一、事务与并发

- `@Transactional` 保证多个写操作要么都成功、要么都回滚，比如"插入评论"和"帖子评论数 +1"。
- 事务默认只在 `RuntimeException` 时回滚。
- 同一个类里方法互相调用，`@Transactional` 不会生效，因为走不到 Spring 的代理对象。
- 计数要用 `UPDATE ... SET count = count + 1` 在数据库里原子地加，不能"先查出来、Java 里加 1、再写回去"，否则并发时会丢失更新。
- 唯一索引是并发场景下的最后一道防线：重复点赞、重复注册都靠它兜底。
- "先查有没有，再插入"在并发下不可靠，两个请求可能都查到"没有"。

### 二十二、表设计经验

- 冗余字段（`comment_count`、`like_count`）是用写入时多做一次更新，换取读取时不用 `COUNT(*)`。社区类应用读远多于写，这样划算。
- 联合索引要匹配查询方式：评论列表查询是 `WHERE post_id=? ORDER BY id`，就建 `(post_id, id)`。
- 自增 id 的顺序等于创建时间顺序，按主键倒序排即可，不用再建时间索引。
- 互联网项目一般不用外键：外键影响写入性能，也不方便分库分表，数据完整性由代码保证。
- 表名和字段名要避开 SQL 关键字：点赞表叫 `like_record`，已读字段叫 `is_read`。
- 通知只存 `type` 和 `target_id`，不存拼好的文案，改文案和做多语言时不用动数据库。

### 二十三、缓存（Cache Aside）

- 最常用的缓存模式：读时先查缓存，没命中再查库并写入缓存；**写时先更新数据库，再删除缓存**。
- 是"删除缓存"而不是"更新缓存"：并发更新时，更新缓存的顺序可能和更新数据库的顺序相反，导致缓存里留下旧值。
- **删缓存必须在事务提交之后**，否则别的请求可能在提交前读到旧数据，又把旧值写回缓存。用 `TransactionSynchronization.afterCommit` 实现。
- 缓存穿透：反复查不存在的 id。解决办法是缓存一个空标记，设较短的过期时间。
- 缓存雪崩：大量缓存同时过期。解决办法是过期时间加随机值。
- 用户相关的数据（比如"我是否点过赞"）不能放进公共缓存，否则会串号。
- 缓存只是加速，删掉不影响正确性。真实数据要以数据库为准。

### 二十四、幂等

- 点赞接口传明确的目标状态 `liked: true/false`，而不是"点一下切换"。切换不幂等，连点两下或超时重试会出错。
- 只有状态真的发生变化时才更新计数，重复点赞、重复取消都不会让计数出错。
- 标记已读也要幂等，重复调用返回同样的结果。

### 二十五、消息队列在业务里的真实用法

- 核心链路（发帖、评论）和附属功能（通知）用消息队列解耦，附属功能不会拖慢或拖垮核心链路。
- **消息必须在事务提交之后再发**，否则消费者可能比事务提交更快，查不到刚写入的数据。
- 事务回滚时消息不会发出去，也就不会产生"幽灵通知"。
- **默认情况下消费失败的消息会被无限重投**，日志会被刷爆。必须配置重试次数和 `default-requeue-rejected: false`。
- 死信队列（DLQ）用来隔离重试多次仍然失败的消息：既不会丢，也不会拖垮队列，可以事后人工排查。
- RabbitMQ 保证"至少送达一次"，消息可能重复消费，生产环境要做消费端幂等。

### 二十六、时区问题

- MySQL 容器默认时区是 UTC，`DEFAULT CURRENT_TIMESTAMP` 生成的时间会比北京时间早/晚 8 小时。
- JDBC 连接串里的 `serverTimezone` 只是告诉驱动"服务器是什么时区"，不会去改 MySQL 本身的时区。
- 正确做法是在 `docker-compose.yml` 里给 MySQL 加 `--default-time-zone=+08:00`。
- 要写偏移量 `+08:00`，不要写 `Asia/Shanghai`：全新初始化时时区表还没导入，写时区名会导致 MySQL 启动失败。

### 二十七、循环依赖

- Spring Boot 从 2.6 开始默认禁止循环依赖，A 依赖 B、B 又依赖 A 会直接启动失败。
- 遇到循环依赖时，让其中一方依赖更底层的东西。例如 `LikeService` 不依赖 `PostService`，而是直接用 `PostMapper`。

### 二十八、应用容器化

- Dockerfile 用多阶段构建：第一阶段用 Maven 镜像编译打包，第二阶段只用 JRE 镜像跑 jar。
- 这样最终镜像里没有 Maven、没有源码、没有编译缓存，体积小很多。
- 在容器里编译的好处是别人不装 JDK 和 Maven 也能构建，环境完全一致。
- `COPY pom.xml` 和 `COPY src` 要分开写：Docker 一层层构建，只要 pom.xml 没变，下载依赖那一层就复用缓存，改代码时不用重新下载依赖。
- 如果一开始就 `COPY . .`，改一行代码都要重新下载全部依赖。
- 用 `USER` 指定普通用户运行应用，不要用 root。
- 容器默认时区是 UTC，要设 `ENV TZ=Asia/Shanghai`，否则日志时间和数据库时间差 8 小时。
- `.dockerignore` 的作用和 `.gitignore` 类似，避免把 `target/`、`.git/`、`.idea/` 发给 Docker 拖慢构建。

### 二十九、容器内的网络

- **容器里的 `localhost` 指的是容器自己**，不是宿主机，所以应用容器里连 `localhost:3306` 是连不上 MySQL 的。
- Docker Compose 会自动建网络，并把服务名注册成域名，所以容器之间用服务名互相访问：`mysql`、`redis`、`rabbitmq`。
- 本地开发时能用 `localhost`，是因为 compose 做了端口映射，把容器端口映射到了宿主机。
- 用 Spring Profile 区分两种环境：`application.yml` 是本地开发（localhost），`application-docker.yml` 只写不同的部分（服务名），用 `SPRING_PROFILES_ACTIVE=docker` 激活。
- 启动日志里能看到 `The following 1 profile is active: "docker"`。

### 三十、depends_on 和健康检查

- `depends_on` 默认只保证"容器已启动"，**不保证"服务可用"**。
- MySQL 容器启动后内部初始化还要几十秒，应用这时去连会直接启动失败。
- 正确做法是给中间件配 `healthcheck`，再用 `depends_on: condition: service_healthy`。
- `start_period` 用来给初始化留时间，这段时间内的失败不计入重试次数。

### 三十一、配置与密钥

- 密钥、密码不要写死在镜像里，要用环境变量注入：`JWT_SECRET: ${JWT_SECRET:-默认值}`。
- `${VAR:-默认值}` 是 compose 的默认值语法，不设环境变量时用默认值。
- 生产环境启动：`JWT_SECRET=真实密钥 docker compose up -d`。

### 三十二、MySQL 8 认证的坑

- 报错 `Public Key Retrieval is not allowed` 的原因：MySQL 8 默认用 `caching_sha2_password` 认证，不走 SSL 时驱动需要向服务端索取公钥来加密密码，而驱动默认不允许。
- 本地开发时一直没遇到，是因为之前用命令行登录过，服务端缓存了认证信息，应用走的是快速通道。重建 MySQL 容器后缓存清空，问题才暴露。
- 解决办法：连接串加 `allowPublicKeyRetrieval=true`（仅限本地开发，生产环境应该用 SSL）。
- 教训：**有些问题一直存在，只是被缓存掩盖了**，换电脑或重建容器时才会突然冒出来。

## 当前进度

第 1 到第 6 阶段已全部完成，首版规划的功能都实现了。

- 已完成 Spring Boot 骨架、健康检查接口、统一返回体
- 已完成 Docker 中间件（MySQL / Redis / RabbitMQ）的启动和验证
- 已完成 MySQL 接入和 `user` 表最小 CRUD
- 已完成 Redis 接入（set/get 示例、帖子详情缓存、Refresh Token 状态）
- 已完成 RabbitMQ 接入（最小收发示例、通知异步生成、死信队列）
- 已完成 Spring Security + JWT：注册、登录、刷新令牌、登出、获取当前用户
- 已完成全局异常处理与参数校验
- 已完成帖子模块：发帖、分页列表、详情
- 已完成评论模块：发表评论、分页列表
- 已完成点赞模块：帖子/评论点赞与取消，计数与状态正确
- 已完成通知模块：通知列表、未读数、标记已读
- 已修复 MySQL 时区问题
- 已完成应用容器化：`docker compose up -d` 一键拉起 app + 三个中间件
- 开发环境在 Windows 和 macOS 上都能跑

现在项目有两种运行方式：

- 应用本地跑 + 中间件 Docker 跑：日常开发用，改代码能快速重启
- 全部 Docker 跑：验证部署效果、给别人演示

两种方式都占用 8080 端口，不能同时启动。

## 下一步

六个阶段的计划已经走完，后面可以按兴趣选方向：

1. 补测试：目前一行测试都没有。可以用 `@SpringBootTest` 加 Testcontainers，自动起临时 MySQL 跑集成测试
2. 接口文档：接入 SpringDoc（Swagger），自动生成在线文档
3. 镜像优化：用 Spring Boot 的分层 jar，改代码时只重建最后一层
4. CI：GitHub Actions，push 时自动构建和测试
5. 补业务：删除/编辑帖子、用户主页、关注、搜索
6. 性能优化：高并发点赞改用 Redis 计数定时回写、未读数缓存、消费端幂等、Redis 故障降级
