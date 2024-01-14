#1. basic
FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/exchange-portal-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]