FROM ibm-semeru-runtimes:open-21-jre-focal
EXPOSE 8080
ARG JAR_FILE
ADD $JAR_FILE /app.jar
ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Shanghai", "/app.jar"]
