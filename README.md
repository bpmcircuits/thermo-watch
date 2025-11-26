# ThermoWatch

A professional microservice-based system for monitoring temperature and humidity from IoT sensors.
Sensors publish measurements via MQTT. The system processes the event stream, persists data in a
database, and exposes APIs for querying. Everything is containerized and orchestrated with Docker Compose.

## Key features
- Ingest measurements from sensors (e.g., Tasmota) via MQTT and normalize payloads.
- Publish unified domain events to RabbitMQ.
- Durable storage of measurements in PostgreSQL.
- Public REST API for querying measurements, sensors, and rooms/locations.
- Service Discovery (Eureka) and centralized configuration (Spring Cloud Config).
- API Gateway and a React-based frontend for data visualization.

## Architecture and components
The project consists of multiple microservices and supporting infrastructure:
- discovery-service (Eureka) – service registry (port 8761).
- config-service – configuration server (port 8081) backed by the config-repo.
- gateway-service – API gateway (port 8080) routing traffic to backend services.
- mqtt-ingest-service – subscribes to MQTT, maps messages to events, and publishes to RabbitMQ (port 8082).
- measurement-storage-service – consumes measurement events; validates and stores data in PostgreSQL (port 8083).
- measurement-query-service – REST API for reading data (port 8084).
- events – shared event contracts (DTO module).
- frontend – React application for browsing data (served on port 80 via Nginx).

Infrastructure services started locally with Docker Compose:
- PostgreSQL (host port 5555 -> container 5432), initialized via init-db.sql.
- RabbitMQ with Management UI (ports 5672 and 15672).

## Data flow (end-to-end)
1. A sensor publishes MQTT messages (e.g., tele/..., stat/...) with measurements or LWT (availability).
2. mqtt-ingest-service subscribes to topics, maps messages to domain objects, and emits events:
   - SensorMeasurementEvent – a single sensor reading.
   - SensorAvailabilityEvent – a change in sensor availability status.
3. Events are published to RabbitMQ with appropriate routing keys.
4. measurement-storage-service consumes measurement events and persists:
   - RoomData (location),
   - Sensor,
   - Measurement in PostgreSQL.
5. measurement-query-service exposes a REST API for data retrieval, which the frontend consumes via the gateway-service.

## Technologies
- Java 21, Gradle
- Spring Boot 3.5.x, Spring Web, Spring Data JPA, Spring AMQP, Spring Boot Actuator
- Spring Cloud 2025.0 (Eureka Client/Server, Config)
- PostgreSQL, RabbitMQ
- Docker, Docker Compose
- React, Vite, TypeScript (frontend)
- Lombok, JUnit, Mockito

## Local run (Docker Compose)
Requirements: Docker and Docker Compose.

1) Prepare a .env file at the repository root with environment variables, for example:

- POSTGRES_USER=user
- POSTGRES_PASSWORD=pass
- RABBITMQ_USER=guest
- RABBITMQ_PASS=guest
- MQTT_BROKER=tcp://*yourServerAddress*:1883
- MQTT_USER=user
- MQTT_PASS=pass

2) Start the stack:

docker compose up -d --build

3) Access points once up:
- Gateway: http://localhost:8080
- Query Service: http://localhost:8084
- Storage Service: http://localhost:8083
- Ingest Service: http://localhost:8082
- Config Server: http://localhost:8081
- Eureka (Discovery): http://localhost:8761
- RabbitMQ Management UI: http://localhost:15672 (user: RABBITMQ_USER)
- PostgreSQL: localhost:5555 (DB: measurement_db, user: POSTGRES_USER)
- Frontend: http://localhost/

Services expose health checks (Spring Boot Actuator), reflected in docker-compose.yml.

## API – measurement-query-service (REST)
All endpoints have CORS enabled and are versioned under /api/v1.

- GET /api/v1/rooms
  Returns a list of locations with aggregates (e.g., current temperature/humidity, sensor count).

- GET /api/v1/locations/{location}/measurements?hours={0..720}
  Returns a list of measurements for the given location within the provided time window (validated range 0–720h).

- GET /api/v1/sensors
  Returns a list of sensors (id, type, identifier, location, etc.).

- GET /api/v1/sensors/{id}
  Returns details of a single sensor.

- GET /api/v1/sensors/{id}/measurements?hours={0..720}
  Returns measurements for the sensor within the provided time window (default 24h, limited to 0–720h).

Sample DTO shapes are available in the measurement-query-service module (dto/mapper packages).

## Event contracts (module: events)
Shared Java records used for inter-service communication:

- SensorMeasurementEvent:
  - sensorType: String
  - sensorId: String
  - location: String
  - temperature: BigDecimal
  - humidity: BigDecimal
  - dewPoint: BigDecimal
  - timestamp: LocalDateTime

- SensorAvailabilityEvent:
  - sensorId: String
  - status: String (e.g., Online/Offline)
  - source: String (e.g., MQTT_LWT)
  - timestamp: LocalDateTime

Events are published by mqtt-ingest-service using RabbitTemplate to an exchange and routing keys
configured via Spring properties (e.g., rabbitmq.exchange.name, rabbitmq.routing.measurement,
rabbitmq.routing.availability).

## Configuration and service discovery
- All services (except EUREKA/CONFIG) read configuration from Spring Cloud Config:
  SPRING_CONFIG_IMPORT=optional:configserver:http://config-service:8081
- Services register with Eureka at: http://discovery-service:8761/eureka
- Default configurations are stored in the config-repo directory.

## Database and domain model
- PostgreSQL (measurement_db) initialized by init-db.sql.
- measurement-storage-service persists entities:
  - RoomData (location + sensor count),
  - Sensor (associated with a location),
  - Measurement (temperature, humidity, dew point, timestamp).

## Frontend
- React (Vite) application built and served by Nginx (port 80).
- Consumes the API via the gateway-service, rendering lists of locations, sensors, and measurement charts.

## Build and development
- Build the entire project: ./gradlew build
- Run tests (executed during build): ./gradlew test
- Run a single service locally (outside Docker): standard Spring Boot profiles (requires dependencies like RabbitMQ/PostgreSQL running).

## License
This project is licensed under the MIT License. See the [LICENSE](#license) file for details.