FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve -B -q
COPY src ./src
RUN mvn package -DskipTests -B -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY oci_api_key.pem /app/oci_api_key.pem
ENV OCI_PRIVATE_KEY_PATH=/app/oci_api_key.pem
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
