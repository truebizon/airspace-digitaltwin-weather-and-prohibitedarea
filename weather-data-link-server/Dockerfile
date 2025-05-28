FROM gradle:7.6-jdk17 AS build
WORKDIR /workspace
COPY . .
RUN gradle bootJar

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
