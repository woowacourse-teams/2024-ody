FROM openjdk:17-jdk

ARG JAR_FILE=./build/libs/ody-backend-0.0.1-SNAPSHOT.jar

ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}

ENV JASYPT_ENCRYPTOR_PASSWORD=${JASYPT_ENCRYPTOR_PASSWORD}

COPY ${JAR_FILE} ody-backend.jar

ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Seoul", "ody-backend.jar"]
