# JDK 17 기반 이미지 사용
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# docker/build 디렉토리에서 복사
COPY build/app.jar app.jar

# 포트 설정
EXPOSE 8080

# 실행 명령
ENTRYPOINT ["java","-jar","app.jar"]