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

## 项目结构

```text
community-backend/
├─ docker-compose.yml
├─ LEARNING_NOTES.md
├─ pom.xml
├─ requests.http
├─ sql/
│  └─ init.sql
├─ src/
│  └─ main/
│     ├─ java/com/community/backend/
│     └─ resources/application.yml
└─ docker/
   └─ mysql/data/
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

### 5. 启动 Spring Boot

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

当前已实现的用户接口：

```text
POST /api/users
GET /api/users/{id}
```

你也可以直接使用 `requests.http` 在 IDEA 的 HTTP Client 中测试接口。

## 中间件连接信息

### MySQL

- Host: `localhost`
- Port: `3306`
- Database: `community_db`
- Username: `community`
- Password: `community1234`

### Redis

- Host: `localhost`
- Port: `6379`

### RabbitMQ

- AMQP: `localhost:5672`
- Management UI: `http://localhost:15672`
- Username: `community`
- Password: `community1234`

## 说明

- `docker/mysql/data/` 是本地运行数据目录，不会提交到 Git
- 所以新电脑拉代码后不会自动带上你本机已有的数据库数据
- 会带上代码、Docker 编排、初始化 SQL、请求示例和学习笔记
