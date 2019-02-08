FROM gradle:5.1-jdk8-alpine AS build

WORKDIR /workspace
USER root
RUN chown -R gradle:gradle /workspace
USER gradle

COPY --chown=gradle:gradle . .

RUN gradle build --stacktrace

FROM openjdk:8-jdk-alpine AS runtime
WORKDIR /app

ARG FOLDER=/workspace/build

COPY --from=build ${FOLDER}/libs/cuzoo*.jar /app/app.jar

EXPOSE 5000

ENTRYPOINT ["java", "-jar", "app.jar"]