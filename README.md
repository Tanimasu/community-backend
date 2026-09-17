# community-backend

Spring Boot 3.x 社区后端学习项目。

当前技术栈：
- Spring Boot 3.4.x
- MyBatis-Plus
- MySQL
- Redis
- RabbitMQ
- Docker
- JDK 21

## 当前已完成

- 最小 Spring Boot 项目骨架
- `GET /api/health` 健康检查接口
- 统一返回体 `ApiResponse`
- `user` 模块最小 CRUD 首轮验证
- `docker-compose.yml` 启动 MySQL / Redis / RabbitMQ
- `sql/init.sql` 提供数据库初始化脚本
- Redis set/get 示例（`/api/demo/redis`）
- RabbitMQ 发送与消费示例（`/api/demo/mq`）
- Spring Security + JWT 认证：注册、登录、刷新令牌、登出、获取当前用户
- 全局异常处理与参数校验
- 帖子：发帖、分页列表、详情
- 评论：发表评论、分页列表
- 点赞：帖子/评论点赞与取消点赞，列表和详情返回当前用户是否已点赞
- 帖子详情 Redis 缓存（Cache Aside，数据变更后删除缓存）
- 通知：评论/点赞后通过 RabbitMQ 异步生成通知，支持通知列表、未读数、标记已读
- RabbitMQ 消费失败重试 3 次后进入死信队列
- 应用容器化：`Dockerfile` 多阶段构建，`docker compose up -d` 一键拉起全套环境

## 项目结构

```text
community-backend/
├─ Dockerfile
├─ .dockerignore
├─ docker-compose.yml
├─ LEARNING_NOTES.md
├─ pom.xml
├─ requests.http
├─ sql/
│  └─ init.sql
├─ src/
│  └─ main/
│     ├─ java/com/community/backend/
│     └─ resources/
│        ├─ application.yml          # 本地开发：连 localhost
│        └─ application-docker.yml   # 容器内运行：连服务名
└─ docker/
   └─ mysql/data/
```

## 两种运行方式

| 方式 | 说明 | 适合场景 |
|---|---|---|
| 应用本地跑 + 中间件 Docker 跑 | `docker compose up -d mysql redis rabbitmq` + IDEA 运行 | 日常开发，改代码可以热重启 |
| 全部 Docker 跑 | `docker compose up -d` | 验证部署效果、给别人演示 |

两种方式都占用 8080 端口，不能同时启动。

### 一键启动（全部容器化）

```bash
docker compose up -d
```

首次执行会构建应用镜像，需要几分钟。之后改了代码要重新构建：

```bash
docker compose up -d --build app
```

查看应用日志：

```bash
docker compose logs -f app
```

停止全部容器：

```bash
docker compose down
```

生产环境务必替换 JWT 密钥：

```bash
JWT_SECRET=你的密钥 docker compose up -d
```

## 新电脑启动步骤

### 1. 准备环境

至少需要：
- JDK 21
- IntelliJ IDEA 或可用的 Maven 环境
- Docker Desktop

建议先验证：

```powershell
java -version
docker --version
docker compose version
```

如果你已经配置了系统 Maven，也可以验证：

```powershell
mvn -version
```

### 2. 拉取代码

```powershell
git clone <你的仓库地址>
cd community-backend
```

### 3. 启动中间件

```powershell
docker compose up -d
```

查看状态：

```powershell
docker ps
```

查看日志：

```powershell
docker logs community-mysql
docker logs community-redis
docker logs community-rabbitmq
```

### 4. 数据库初始化说明

项目已提供初始化脚本：

- `sql/init.sql`

并且已经挂载到 MySQL 容器的初始化目录。

注意：
- 这个脚本只会在 MySQL 数据目录为空时自动执行
- 如果你之前已经启动过 MySQL，并且 `docker/mysql/data/` 里已经有旧数据，初始化脚本不会再次自动执行

如果你想在新机器上重新初始化数据库，可以先停止容器，再删除本地 MySQL 数据目录，然后重新启动：

```powershell
docker compose down
Remove-Item -Recurse -Force .\docker\mysql\data\*
docker compose up -d
```

如果你不想删除数据，也可以手动执行 `sql/init.sql` 里的 SQL。

如果数据库是第 4 阶段之前初始化的，`user` 表还没有 `role` 字段，需要手动补上：

```sql
ALTER TABLE `user` ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER' AFTER nickname;
```

同理，第 5 阶段新增的 `post`、`comment`、`like_record` 表也需要手动执行 `sql/init.sql` 中对应的 `CREATE TABLE` 语句（脚本使用了 `IF NOT EXISTS`，整份重新执行也是安全的）。

### 5. 启动 Spring Boot（本地开发方式）

在 IDEA 中直接运行：

- `CommunityBackendApplication`

或者在系统 Maven 已配置好的前提下执行：

```powershell
mvn spring-boot:run
```

### 6. 验证接口

健康检查：

```text
GET http://localhost:8080/api/health
```

预期返回：

```json
{
  "code": 200,
  "message": "OK",
  "data": {
    "service": "community-backend",
    "status": "UP"
  }
}
```

认证接口（无需 Token）：

```text
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh
POST /api/auth/logout
```

用户接口（需要请求头 `Authorization: Bearer <accessToken>`）：

```text
GET /api/users/me
GET /api/users/{id}
```

帖子与评论接口（GET 游客可访问，POST 需要 Token）：

```text
GET  /api/posts?page=1&pageSize=10
GET  /api/posts/{id}
POST /api/posts
GET  /api/posts/{postId}/comments?page=1&pageSize=20
POST /api/posts/{postId}/comments
```

点赞接口（需要 Token）：

```text
POST /api/likes
{ "targetType": "POST | COMMENT", "targetId": 1, "liked": true }
```

通知接口（需要 Token）：

```text
GET  /api/notifications?page=1&pageSize=20&unreadOnly=false
GET  /api/notifications/unread-count
POST /api/notifications/{id}/read
```

分页返回格式：

```json
{ "page": 1, "pageSize": 10, "total": 3, "list": [] }
```

- Access Token 有效期 15 分钟，Refresh Token 有效期 7 天
- Refresh Token 状态保存在 Redis（`auth:refresh:<tokenId>`），每次刷新都会换发新的，旧的立即失效

中间件示例接口：

```text
POST /api/demo/redis?key=hello&value=world&ttlSeconds=60
GET  /api/demo/redis/{key}
POST /api/demo/mq?content=hello
GET  /api/demo/mq/received
```

你也可以直接使用 `requests.http` 在 IDEA 的 HTTP Client 中测试接口。

## 中间件连接信息

### MySQL

- Host: `localhost`
- Port: `3306`
- Database: `community_db`
- 时区: `+08:00`（在 `docker-compose.yml` 中通过 `--default-time-zone` 配置）
- Username: `community`
- Password: `community1234`

### Redis

- Host: `localhost`
- Port: `6379`

### RabbitMQ

队列说明：

- `notification.queue`：评论、点赞产生的通知消息
- `notification.dlq`：死信队列，消费重试 3 次仍失败的消息会进这里
- `demo.queue`：第 3 阶段的最小收发示例

- AMQP: `localhost:5672`
- Management UI: `http://localhost:15672`
- Username: `community`
- Password: `community1234`

## 说明

- `docker/mysql/data/` 是本地运行数据目录，不会提交到 Git
- 所以新电脑拉代码后不会自动带上你本机已有的数据库数据
- 会带上代码、Docker 编排、初始化 SQL、请求示例和学习笔记
