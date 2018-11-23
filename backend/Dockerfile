FROM openjdk:8-jdk-alpine

ADD build/libs/cuzoo-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 5000

ENTRYPOINT ["java", "-jar", "app.jar"]
