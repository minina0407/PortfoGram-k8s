FROM --platform=linux/amd64 alpine:3.15 AS DOWNLOAD
ARG OTEL_AGENT_VERSION="1.18.0"
RUN apk --no-cache add curl \
    && curl --silent --fail -L "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar" \
    -o "/tmp/opentelemetry-javaagent.jar"

FROM gradle:7.6.1-jdk17 AS BUILD
ARG MYSQL_USERNAME
ARG MYSQL_PASSWORD
ENV SPRING_DATASOURCE_USERNAME=$MYSQL_USERNAME
ENV SPRING_DATASOURCE_PASSWORD=$MYSQL_PASSWORD
COPY --chown=gradle:gradle spring-boot-app /home/gradle/src
RUN chmod +x /home/gradle/src/gradlew
WORKDIR /home/gradle/src
RUN gradle build -x test --no-daemon

FROM  openjdk:17-slim
ENV JAVA_OPTS "-Dspring.config.location=src/main/resources/application.properties"
COPY --from=BUILD /home/gradle/src/build/libs/*.jar /app.jar
COPY --from=DOWNLOAD /tmp/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar
ENTRYPOINT ["java",  "-javaagent:/opentelemetry-javaagent.jar", "-jar", "/app.jar" ]

