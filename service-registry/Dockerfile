#FROM openjdk:11
#
#ARG JAR_FILE=target/*.jar
#
#COPY ${JAR_FILE} serviceregistry.jar
#
#ENTRYPOINT ["java", "-jar", "/serviceregistry.jar"]
#
#EXPOSE 8761

FROM openjdk:11

WORKDIR /app

#COPY target/service-registry-0.0.1-SNAPSHOT.jar app.jar
ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 8761