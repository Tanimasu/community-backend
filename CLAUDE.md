# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Spring Boot 3.4 / JDK 21 community-forum backend (users, posts, comments, likes, notifications), built as a staged learning project. MyBatis-Plus + MySQL 8.4, Redis, RabbitMQ, Spring Security + JWT (jjwt 0.12). `LEARNING_NOTES.md` holds the stage-by-stage plan and notes. README, code comments, and user-facing error messages are written in Chinese — keep new ones consistent.

## Commands

There is no Maven wrapper; use a system `mvn`. There is no `src/test` yet, so there are no tests to run.

```bash
docker compose up -d mysql redis rabbitmq   # middleware only, run the app locally
mvn spring-boot:run                         # run app on :8080 against localhost middleware
mvn -B clean package -DskipTests            # build jar (what the Dockerfile does)
docker compose up -d                        # full stack incl. app container
docker compose up -d --build app            # rebuild app image after code changes
docker compose logs -f app
```

Local app and the `app` container both bind 8080 — don't run both. `requests.http` contains example requests (IntelliJ HTTP Client) for every endpoint.

## Configuration

- `application.yml` targets `localhost`; `application-docker.yml` (activated by `SPRING_PROFILES_ACTIVE=docker` in compose) only overrides hostnames to compose service names.
- `JWT_SECRET` env var overrides the dev secret. Access token 15m, refresh token 7d.
- MySQL runs at `--default-time-zone=+08:00` and the JDBC URL uses `serverTimezone=Asia/Shanghai`; containers set `TZ=Asia/Shanghai`. Keep these aligned when touching time handling.
- `sql/init.sql` is the only schema definition (no migration tool). It auto-runs only when `docker/mysql/data/` is empty; all statements use `IF NOT EXISTS`, so schema changes must be appended there and applied manually to existing databases.

## Architecture

Package root `com.community.backend`:
- `modules/<feature>/{controller,service,mapper,entity,dto,mq}` — feature modules. Entities are plain classes with hand-written getters/setters (no Lombok); DTOs are Java records. Mappers are bare MyBatis-Plus `BaseMapper`s queried with `LambdaQueryWrapper`/`LambdaUpdateWrapper` — no XML mappers.
- `common/` — `ApiResponse` (every endpoint returns `{code, message, data}`), `PageQuery`/`PageResult` (page clamped to size 1–50; requires the pagination interceptor in `MybatisPlusConfig`), `TransactionUtils`, and exception handling.
- `security/` — stateless JWT auth. `JwtAuthenticationFilter` parses the access token into a `LoginUser` record (id, username, role) without hitting the DB; controllers take `@AuthenticationPrincipal LoginUser`, which is `null` for anonymous callers on public GET routes. Refresh tokens are tracked in Redis (`auth:refresh:<tokenId>`) and rotated on every refresh.
- `config/SecurityConfig` — the public-route allowlist (auth endpoints, `/api/health`, GET `/api/posts/**`, `/api/demo/**`, `/error`); everything else requires auth. New public endpoints must be added here.

Errors: throw `BusinessException(HttpStatus, message)`; `GlobalExceptionHandler` maps it (and validation/parse errors) to the HTTP status plus an `ApiResponse` body. Security 401/403 go through `SecurityErrorHandler` in the same shape.

### Cross-cutting patterns to preserve

- **After-commit side effects**: cache eviction and MQ publishing must go through `TransactionUtils.afterCommit(...)` so they run only after the surrounding `@Transactional` commits (otherwise stale data can be re-cached or consumers can read uncommitted state).
- **Post detail cache** (`PostCacheService`): Cache-Aside on `post:detail:<id>`, with a null-marker for missing posts and TTL jitter. Any write that changes a post's rendered detail (e.g. like/comment counts) must call `postCacheService.evictAfterCommit(postId)`.
- **Likes** (`LikeService`): idempotency relies on the unique key `(user_id, target_type, target_id)` in `like_record` — a `DuplicateKeyException` means "already liked". Counters (`like_count`, `comment_count`) are updated in SQL (`col = col + 1`) in the same transaction, only when state actually changed. `LikeService` uses `PostMapper`/`CommentMapper` directly because `PostService`/`CommentService` depend on `LikeService` — avoid introducing a cycle.
- **Notifications**: comment/like services call `NotificationEventPublisher`, which skips self-notifications and publishes a JSON `NotificationEvent` to `notification.exchange` after commit; `NotificationListener` consumes `notification.queue` and persists via `NotificationService`. Listener retries 3 times then dead-letters to `notification.dlq` (`default-requeue-rejected: false`). Exchanges/queues are declared as beans in `RabbitMqConfig`.
- `modules/demo` is stage-3 scaffolding (Redis/RabbitMQ examples on `demo.queue`), not product features.
