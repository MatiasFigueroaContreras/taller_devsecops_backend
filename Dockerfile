# Build Stage
FROM --platform=linux/amd64 maven:3.8.4-openjdk-17  AS build
COPY pom.xml .
COPY src ./src
COPY secrets.properties /secrets.properties
RUN mvn clean package -DskipTests

# Run Stage
FROM --platform=linux/amd64 azul/zulu-openjdk-alpine:17-jre
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} milkstgo-1.jar
COPY --from=build /secrets.properties /secrets.properties
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "/milkstgo-1.jar"]