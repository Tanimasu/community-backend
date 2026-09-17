# 第一阶段：在容器里编译打包，这样不依赖本机是否装了 Maven 和 JDK
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# 先只复制 pom.xml 下载依赖，单独一层。
# 只要 pom.xml 没变，改代码重新构建时这一层就能复用缓存，不用重新下载依赖
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# 第二阶段：运行镜像只需要 JRE，不需要 Maven 和源码，镜像小很多
FROM eclipse-temurin:21-jre
WORKDIR /app

ENV TZ=Asia/Shanghai

# 不用 root 运行应用，降低容器被攻破后的风险
RUN useradd --create-home --shell /bin/bash app
USER app

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
