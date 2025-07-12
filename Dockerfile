FROM eclipse-temurin:21-jdk

WORKDIR /workspace

COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew :messenger-server:shadowJar -x test -x check

CMD ["java", "-jar", "messenger-server/build/libs/messenger-server-0.0.1-all.jar"]