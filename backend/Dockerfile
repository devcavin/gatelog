FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true

COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S gatelog && adduser -S gatelog -G gatelog
USER gatelog

COPY --from=build --chown=gatelog:gatelog /app/build/libs/*.jar app.jar
EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "/app/app.jar" ]