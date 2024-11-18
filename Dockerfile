# JDK 17 기반 이미지 사용
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 복사 (build/libs/ 또는 target/ 폴더에서)
# Gradle의 경우
COPY build/libs/*.jar app.jar

# 포트 설정
EXPOSE 8080

# 실행 명령
ENTRYPOINT ["java","-jar","/app/app.jar"]