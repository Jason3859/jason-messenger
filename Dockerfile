FROM eclipse-temurin:21-jdk

WORKDIR /messenger-server

COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew clean shadowJar -x test -x check

# Rename the generated JAR to a consistent name
RUN cp build/libs/*-all.jar app.jar

# Always run the renamed JAR
CMD ["java", "-jar", "app.jar"]

