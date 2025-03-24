FROM gradle:8.5-jdk17 AS build
WORKDIR /build
COPY --chown=gradle:gradle . .
RUN gradle :eureka-server:bootJar
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /build/eureka-server/build/libs/eureka-server-0.0.1-SNAPSHOT.jar eureka-server-0.0.1-SNAPSHOT.jar
COPY --from=build /build/eureka-server/build/libs /app/libs
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh
RUN apt-get update && apt-get install -y curl && apt-get clean
EXPOSE 19090
HEALTHCHECK --interval=30s --timeout=10s --start-period=15s --retries=5 \
  CMD curl -f http://localhost:19090/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "eureka-server-0.0.1-SNAPSHOT.jar"]
LABEL authors="sparta-msa_chap.02_16"

# 1단계: 빌드 스테이지
# 2단계: 런타임 스테이지
# 빌드된 JAR 복사
# curl 설치 (헬스 체크에 사용)
# 포트 노출
# 헬스 체크 추가
# 실행 명령
# 메타 정보
