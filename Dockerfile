# Start a new stage to create a smaller image for the runtime
FROM openjdk:21

# Set the working directory inside the container
WORKDIR /app

# Copy the built jar file from the build stage (using the correct path)
COPY target/joule-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
