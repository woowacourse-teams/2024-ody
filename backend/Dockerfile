FROM openjdk:17-jdk

ENV SPRING_PROFILES_ACTIVE=dev

ARG JAR_FILE=./build/libs/ody-backend-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} ody-backend.jar

ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Seoul", "ody-backend.jar"]
