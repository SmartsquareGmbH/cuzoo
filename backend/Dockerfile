FROM openjdk:8-jdk-alpine
ADD build/libs/cuzoo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 5000
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=5000", "--spring.datasource.url=jdbc:postgresql://postgre-db:5432/postgres", "--spring.datasource.username=postgres", "--spring.datasource.password=secure"]