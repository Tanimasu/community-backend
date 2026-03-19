# community-backend

这是一个按学习步骤推进的 Spring Boot 社区后端项目。

当前已经完成：

- Spring Boot 3.4.x + Maven + Java 21 最小骨架
- 健康检查接口 `GET /api/health`
- 统一返回体 `ApiResponse`
- MySQL、Redis、RabbitMQ 的 `docker-compose.yml`

## 当前项目结构

```text
community-backend/
├─ docker-compose.yml
├─ pom.xml
├─ src/
│  ├─ main/
│  │  ├─ java/com/community/backend/
│  │  └─ resources/application.yml
│  └─ test/
└─ README.md
```

## 第 1 步：本地启动 Spring Boot

在 IDEA 中直接运行 `CommunityBackendApplication`。

或者在你后续配好系统 Maven 后，在项目根目录运行：

```powershell
mvn spring-boot:run
```

启动成功后访问：

```text
http://localhost:8080/api/health
```

当前预期返回：

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

## 第 2 步：用 Docker 启动中间件

在项目根目录运行：

```powershell
docker compose up -d
```

查看容器状态：

```powershell
docker ps
```

查看日志：

```powershell
docker logs community-mysql
docker logs community-redis
docker logs community-rabbitmq
```

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

## 下一步

下一阶段按顺序做：

1. 配置系统 Maven，确保终端里 `mvn -version` 可用
2. 验证 `docker compose up -d` 能正常启动中间件
3. 引入 MyBatis-Plus 和 MySQL 驱动
4. 创建第一张 `user` 表和基础 CRUD
