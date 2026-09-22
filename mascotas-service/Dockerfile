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

ARG JWT_SECRET
ARG OCI_USER_ID
ARG OCI_TENANCY_ID
ARG OCI_FINGERPRINT
ARG OCI_NAMESPACE
ARG OCI_BUCKET

ENV JWT_SECRET=${JWT_SECRET}
ENV OCI_USER_ID=${OCI_USER_ID}
ENV OCI_TENANCY_ID=${OCI_TENANCY_ID}
ENV OCI_FINGERPRINT=${OCI_FINGERPRINT}
ENV OCI_NAMESPACE=${OCI_NAMESPACE}
ENV OCI_BUCKET=${OCI_BUCKET}
ENV OCI_REGION=sa-santiago-1
ENV OCI_PRIVATE_KEY_PATH=/app/oci_api_key.pem

EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
