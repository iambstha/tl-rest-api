# Use a specific OpenJDK version for consistency
FROM openjdk:17-alpine AS build

# Install Maven
RUN apk add --no-cache maven

# Create and set the working directory
WORKDIR /app

# Copy only the necessary files first for better caching
COPY pom.xml ./
COPY src ./src
# Copy the keys into the container


# Build the application (using Maven)
RUN mvn clean package -DskipTests

# Use a slimmer image for the final run
FROM openjdk:17-alpine

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Run the applicationc
ENTRYPOINT ["java", "-jar", "/app.jar"]

# Expose the application port
EXPOSE 8080
