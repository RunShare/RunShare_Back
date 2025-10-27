# JDK 17 런타임만 포함된 가벼운 이미지
FROM eclipse-temurin:17-jre-alpine

# 컨테이너 안 작업 폴더
WORKDIR /app

# 로컬에서 만든 JAR를 컨테이너로 복사
# build/libs 폴더에 있는 첫 번째 .jar 파일을 app.jar로 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 애플리케이션이 사용하는 포트 (스프링 기본 8080)
EXPOSE 8080

# 앱 실행
ENTRYPOINT ["java", "-jar", "app.jar"]