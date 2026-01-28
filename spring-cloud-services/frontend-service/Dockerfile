FROM openjdk:11-jre-slim

WORKDIR /app

COPY target/frontend-service-1.0.0.jar app.jar

EXPOSE 3000

ENTRYPOINT ["java", "-jar", "app.jar"]


