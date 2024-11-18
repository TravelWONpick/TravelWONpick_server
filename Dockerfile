# 베이스 이미지로 OpenJDK 17 slim 사용
FROM openjdk:17-slim

# 작업 디렉토리 설정
WORKDIR /app

# 애플리케이션 JAR 파일을 컨테이너의 작업 디렉토리로 복사
COPY server-0.0.1-SNAPSHOT.jar app.jar

# 컨테이너가 수신할 포트 정의
EXPOSE 8080

# 메모리 최적화 옵션과 함께 Java 애플리케이션 실행
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]./gradlew build