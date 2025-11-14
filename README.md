# Thermo Watch

This is a microservice-based system designed to collect 
and analyze temperature measurements from distributed sensors(e.g., Tasmota devices) 
publishing data via MQTT. A dedicated ingest-service subscribes to selected MQTT topics, 
normalizes incoming payloads into a unified data model (such as SensorMessage), 
and enriches them with metadata like warehouse ID or sensor identifier. 
The processed messages are then forwarded to a data-analysis service, responsible for detecting 
anomalies (e.g., abnormally high or low temperatures) and to a persistence layer where the data 
is stored in a time-series or relational database. All components run as independent microservices 
(Spring Boot + Spring Integration MQTT + message broker) containerized with Docker, 
monitored using Prometheus and Grafana. This architecture ensures horizontal scalability 
and reliability as the number of sensors and warehouses grows.