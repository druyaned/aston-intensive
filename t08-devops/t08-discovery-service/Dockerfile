# Step#10: write Dockerfile

FROM eclipse-temurin:17-jre

# For Spring Boot temp files
VOLUME /tmp

ARG JAR_FILE=target/t08-discovery-service-1.0.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8761

ENTRYPOINT ["java","-jar","/app.jar"]
