# =========================
# 1단계: 빌드 스테이지
# =========================
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# Gradle wrapper 및 설정 복사
COPY gradlew ./
COPY gradle gradle
COPY build.gradle settings.gradle ./

# ./gradlew 권한 설정
RUN chmod +x ./gradlew

# 의존성 캐싱
RUN ./gradlew dependencies --no-daemon || return 0

# 소스 복사 및 빌드
COPY src src
RUN ./gradlew clean bootJar --no-daemon

# =========================
# 2단계: 런타임 스테이지
# =========================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 빌드 결과 JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 환경 변수 설정 (옵션)
ENV SPRING_PROFILES_ACTIVE=prod

# 포트 오픈
EXPOSE 8080

# 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]
