# 빌드 스테이지
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build

# pom.xml과 소스 코드 복사
COPY pom.xml .
COPY src ./src

# 빌드
RUN mvn clean package -DskipTests

# 실행 스테이지
FROM eclipse-temurin:17-jre

WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /build/target/bulletin-board-1.0.0.jar app.jar

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
