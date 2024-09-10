FROM openjdk:17-jdk

ARG JASYPT_ENCRYPTOR_PASSWORD
ARG JAR_FILE=./build/libs/ody-backend-0.0.1-SNAPSHOT.jar

ENV SPRING_PROFILES_ACTIVE=dev

COPY ${JAR_FILE} ody-backend.jar

ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Seoul", "ody-backend.jar"]
