FROM gradle:8.9-jdk21 AS build
WORKDIR /app

COPY build.gradle ./
COPY src ./src

RUN gradle --no-daemon clean build

FROM eclipse-temurin:21-jre
WORKDIR /app

# dodaj curl do healthchecków
USER root
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
RUN addgroup --system app && adduser --system --ingroup app app
USER app

COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]