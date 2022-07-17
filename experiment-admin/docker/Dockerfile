FROM openjdk:11
ARG JAR_FILE=build/libs/experiment-admin-0.0.1.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
