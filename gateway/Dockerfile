FROM gradle:8.5-jdk17 AS build
WORKDIR /build
COPY --chown=gradle:gradle . .
RUN gradle :gateway:bootJar
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /build/gateway/build/libs/gateway-0.0.1-SNAPSHOT.jar app.jar
COPY --from=build /build/gateway/build/libs /app/libs
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh
RUN apt-get update && apt-get install -y curl && apt-get clean
EXPOSE 19091
HEALTHCHECK --interval=30s --timeout=10s --start-period=15s --retries=5 \
  CMD curl -f http://localhost:19091/actuator/health || exit 1
ENTRYPOINT ["/wait-for-it.sh", "eureka-server:19090", "--", "java", "-jar", "app.jar"]
LABEL authors="sparta-msa_chap.02_16"