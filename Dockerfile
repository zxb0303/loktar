FROM ibm-semeru-runtimes:open-21.0.6_7-jre-focal
EXPOSE 8080
ARG JAR_FILE
ADD $JAR_FILE /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.config.additional-location=/loktar/deployments/"]
