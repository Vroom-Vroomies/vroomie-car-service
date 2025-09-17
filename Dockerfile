FROM gradle:7.6.2-jdk17

WORKDIR /app

# gradlew 및 관련 파일 복사
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# 소스 코드 복사
COPY src ./src

# 실행 권한 부여
RUN chmod +x gradlew
RUN chmod +x ./gradlew

EXPOSE 8080

# 개발용 실행 (bootRun)
CMD ["./gradlew", "bootRun", "-x", "test", "--no-daemon"]
