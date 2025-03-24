FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/common-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 19092
ENTRYPOINT ["java", "-jar", "app.jar"]
LABEL authors="sparta-msa_chap.02_16"


