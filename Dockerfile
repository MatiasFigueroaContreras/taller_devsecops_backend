FROM openjdk:17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} milkstgo-1.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "/milkstgo-1.jar"]