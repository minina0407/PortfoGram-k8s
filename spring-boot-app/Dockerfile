FROM curlimages/curl:7.81.0 AS download
ARG OTEL_AGENT_VERSION="1.18.0"
RUN curl --silent --fail -L "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar" \
    -o "$HOME/opentelemetry-javaagent.jar"

FROM gradle:7.6.1-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
RUN chmod +x /home/gradle/src/gradlew
WORKDIR /home/gradle/src
RUN gradle build -x test --no-daemon


FROM openjdk:17-oracle
ENV JAVA_OPTS "-Dspring.config.location=src/main/resources/application.properties"
COPY --from=BUILD /home/gradle/src/build/libs/*.jar /app.jar
COPY --from=download /home/curl_user/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar
ENTRYPOINT ["java",  "-javaagent:/opentelemetry-javaagent.jar", "-jar", "/app.jar" ]

