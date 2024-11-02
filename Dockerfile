FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar joule.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "joule.jar"]
