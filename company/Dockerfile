FROM gradle:8.5-jdk17 AS build
WORKDIR /build
COPY --chown=gradle:gradle . .
RUN gradle :company:bootJar
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /build/company/build/libs/company-0.0.1-SNAPSHOT.jar app.jar
COPY --from=build /build/company/build/libs /app/libs
COPY common/build.gradle /build/common/
COPY common/src /build/common/src
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh
RUN apt-get update && apt-get install -y curl && apt-get clean
EXPOSE 19020
HEALTHCHECK --interval=30s --timeout=10s --start-period=15s --retries=5 \
  CMD curl -f http://localhost:19020/actuator/health || exit 1
ENTRYPOINT ["/wait-for-it.sh", "eureka-server:19090", "--", "java", "-jar", "app.jar"]
LABEL authors="sparta-msa_chap.02_16"
