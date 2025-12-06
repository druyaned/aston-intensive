# Step#10: write Dockerfile

FROM eclipse-temurin:17-jre

# For Spring Boot temp files
VOLUME /tmp

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8085

ENTRYPOINT ["java","-jar","/app.jar"]
