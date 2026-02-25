# 1단계: 빌드 환경 (Builder) - 여기도 21이어야 합니다!
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Gradle Wrapper 및 소스 코드 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src

# gradlew 실행 권한 부여 및 빌드 (테스트 제외로 빌드 속도 향상)
RUN chmod +x ./gradlew
RUN ./gradlew bootJar -x test

# 2단계: 실행 환경 (Runner)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 빌드된 jar 파일을 실행 환경으로 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# Render 무료 티어 메모리 제한 설정
ENV JAVA_OPTS="-Xms256m -Xmx256m"

# 컨테이너 포트 8080 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]