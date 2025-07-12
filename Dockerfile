FROM eclipse-temurin:21-jdk

WORKDIR /messenger-server

COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew clean shadowJar -x test -x check

CMD ["java", "-jar", "build/libs/messenger-server-0.0.1-all.jar"]