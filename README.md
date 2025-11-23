# Customer Experience & Network Analytics Platform

A real-time, full-stack analytics platform for monitoring network performance and customer experience metrics. Built with Java Spring Boot microservices, Apache Kafka, PostgreSQL, Redis, and React.

## Architecture Overview

**Event Flow:**
Network/app events → Kafka → Analytics aggregation → PostgreSQL/Redis → REST APIs → React Dashboard

**Key Metrics:**
- Average latency, jitter, packet loss
- QoE (Quality of Experience) score (0-100)
- Complaint rate / anomaly flags
- Filters by time range, region, device, app

## Tech Stack

**Backend:**
- Java 17 + Spring Boot 3.3.5
- Apache Kafka (event streaming)
- PostgreSQL (time-series data)
- Redis (caching)

**Frontend:**
- React 18
- Recharts (data visualization)
- Vite (build tool)

**Infrastructure:**
- Docker & Docker Compose
- Nginx (static hosting)

## Project Structure

```
cx-network-analytics-platform/
├─ docker-compose.yml
├─ README.md
├─ event-ingestion-service/    # Receives events, publishes to Kafka
├─ analytics-service/          # Consumes events, aggregates metrics
├─ api-service/                # REST APIs for dashboard
└─ dashboard-web/              # React dashboard with charts
```

## Microservices

### 1. Event Ingestion Service (Port 8081)
Receives network events via REST API and publishes them to Kafka topics.

### 2. Analytics Service (Port 8082)
Consumes events from Kafka, performs 5-minute window aggregations, calculates QoE scores, and stores results in PostgreSQL/Redis.

### 3. API Service (Port 8080)
Provides REST endpoints to query KPIs with filtering and time-range support.

### 4. Dashboard Web (Port 3000)
React-based UI displaying real-time metrics with interactive charts and filters.

## Quick Start

### Prerequisites
- Docker & Docker Compose
- Git

### Run the Platform

```bash
# Clone the repository
git clone https://github.com/Akash5091/cx-network-analytics-platform.git
cd cx-network-analytics-platform

# Start all services
docker compose up --build
```

### Access the Services

- **Web Dashboard:** http://localhost:3000
- **API Service:** http://localhost:8080
- **Ingestion Service:** http://localhost:8081

## Usage

### Send Sample Events

```bash
curl -X POST http://localhost:8081/events/network \
  -H "Content-Type: application/json" \
  -d '{
    "eventId":"e-1001",
    "timestamp":"2025-11-22T20:00:00Z",
    "userId":"u-1",
    "region":"Dallas",
    "deviceType":"Android",
    "appName":"VideoStream",
    "latencyMs":85.2,
    "jitterMs":4.1,
    "packetLossPct":0.4,
    "signalStrengthDbm":-82
  }'
```

### Query KPIs

```bash
# Get latest KPIs
curl http://localhost:8080/api/kpis/latest?limit=50

# Get KPIs for a time range
curl "http://localhost:8080/api/kpis/range?from=2025-11-22T00:00:00Z&to=2025-11-23T00:00:00Z"
```

## Dashboard Features

- **Real-time Updates:** Auto-refreshes every 10 seconds
- **Filters:** Region and Application selection
- **KPI Cards:** Latest QoE, Latency, Jitter, Packet Loss
- **Time-series Charts:** Visual trends over time

## QoE Scoring Algorithm

Weighted calculation based on:
- Latency (40%)
- Jitter (25%)
- Packet Loss (25%)
- Signal Strength (10%)

Score range: 0-100 (higher is better)

## Development

### Build Individual Services

```bash
# Event Ingestion Service
cd event-ingestion-service
mvn clean package
java -jar target/*.jar

# Analytics Service
cd analytics-service
mvn clean package
java -jar target/*.jar

# API Service
cd api-service
mvn clean package
java -jar target/*.jar

# Dashboard Web
cd dashboard-web
npm install
npm run dev
```

## Future Enhancements

- **Time-series DB:** Migrate to TimescaleDB for faster range queries
- **Anomaly Detection:** Z-score based detection, later ML models (Isolation Forest)
- **User Complaints:** Ticket correlation system
- **Authentication:** JWT + role-based access (admin, analyst, viewer)
- **Geo Heatmap:** Visual representation of regional performance
- **Alerting:** Threshold-based notifications
- **Advanced Analytics:** Predictive models for network degradation

## License

MIT

## Author

Built by Akash5091