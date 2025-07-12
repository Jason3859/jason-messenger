# Use a JDK 21 base image
FROM eclipse-temurin:21-jdk

# Set the working directory in the container
WORKDIR /messenger-server

# Copy your project files into the container
COPY . .

# Give permission to Gradle wrapper to run
RUN chmod +x ./gradlew

# Build the project (skip tests for now)
RUN ./gradlew clean build -x test -x check

# Set the default command to run your app
CMD ["java", "-jar", "build/libs/messenger-server-0.0.1-all.jar"]
