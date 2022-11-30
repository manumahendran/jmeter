#FROM eclipse-temurin:17-jdk-alpine
#VOLUME /tmp
#ARG JAR_FILE
#COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]

FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY run.sh .
COPY target/*.jar app.jar

ENTRYPOINT ["run.sh"]