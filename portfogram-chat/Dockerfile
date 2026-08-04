FROM --platform=linux/amd64 alpine:3.15 AS DOWNLOAD
ARG OTEL_AGENT_VERSION="1.30.0"
RUN apk --no-cache add curl \
    && curl --silent --fail -L \
    "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar" \
    -o "/tmp/opentelemetry-javaagent.jar"

FROM gradle:7.6.1-jdk17 AS BUILD
ARG MYSQL_USERNAME
ARG MYSQL_PASSWORD
ENV SPRING_DATASOURCE_USERNAME=$MYSQL_USERNAME
ENV SPRING_DATASOURCE_PASSWORD=$MYSQL_PASSWORD
COPY --chown=gradle:gradle . /home/gradle/src
RUN chmod +x /home/gradle/src/gradlew
WORKDIR /home/gradle/src
RUN gradle :portfogram-chat:build -x test --no-daemon

FROM eclipse-temurin:17-jre-focal
COPY --from=BUILD /home/gradle/src/portfogram-chat/build/libs/portfogram-chat-0.0.1-SNAPSHOT.jar /app.jar
COPY --from=DOWNLOAD /tmp/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar
ENTRYPOINT ["java", "-javaagent:/opentelemetry-javaagent.jar", "-jar", "/app.jar"]
