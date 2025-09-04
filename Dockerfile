FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /messenger-server
COPY . .
RUN chmod +x gradlew
RUN ./gradlew clean shadowJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /messenger-server/build/libs/messenger-server-0.0.1-all.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]