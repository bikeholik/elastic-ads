FROM gradle:6.5.1-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:11.0.8-jre

EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/elastic-ads.jar

ENTRYPOINT ["java", "-jar", "/app/elastic-ads.jar"]