FROM gradle:7.6.1-jdk17 AS build

WORKDIR /app

# 의존성 캐시 최적화
COPY build.gradle settings.gradle ./
RUN gradle dependencies --no-daemon

# 소스 복사
COPY . .

# 빌드
RUN gradle bootJar --no-daemon

# 2️⃣ 실행 단계 (가벼운 런타임)
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]



