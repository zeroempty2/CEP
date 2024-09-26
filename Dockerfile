# 1. JDK 이미지 사용
FROM eclipse-temurin:17-jdk-alpine

# 2. 작업 디렉터리 설정
WORKDIR /app

# 3. JAR 파일 복사
COPY build/libs/*.jar app.jar

# 4. 스크립트 파일 복사
COPY start.sh /app/start.sh

# 5. 실행 권한 부여
RUN chmod +x /app/start.sh

# 6. 애플리케이션 실행
ENTRYPOINT ["/app/start.sh"]
