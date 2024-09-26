# 1. JDK 이미지 사용
FROM eclipse-temurin:17-jdk-alpine

# 2. 작업 디렉터리 설정
WORKDIR /app

# 3. JAR 파일 복사
COPY build/libs/*.jar app.jar
