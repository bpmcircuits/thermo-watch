# ---- build stage ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Najpierw wrapper i pliki konfiguracyjne dla cache
COPY gradlew settings.gradle build.gradle /app/
COPY gradle /app/gradle

# Potem całe źródła
COPY . /app

# Zbuduj tylko wybrany moduł
RUN ./gradlew :mqtt-ingest-service:bootJar -x test --no-daemon build

FROM eclipse-temurin:21-jre
WORKDIR /app

# dodaj curl do healthchecków
USER root
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
RUN addgroup --system app && adduser --system --ingroup app app
USER app

COPY --from=build /app/mqtt-ingest-service/build/libs/*-SNAPSHOT.jar /app/app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]