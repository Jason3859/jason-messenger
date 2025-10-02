FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY . .
WORKDIR /app/messenger-server
RUN chmod +x ../gradlew
RUN ../gradlew clean build --no-daemon
RUN ls -l build/libs

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/messenger-server/build/libs/*-all.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]