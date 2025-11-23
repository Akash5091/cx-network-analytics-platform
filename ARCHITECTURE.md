# System Architecture

This document provides a detailed overview of the Customer Experience & Network Analytics Platform architecture.

## High-Level Architecture

```
┌─────────────────┐
│  Network/App    │
│     Events      │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────────┐
│  Event Ingestion Service        │
│  (Spring Boot :8081)            │
│  - REST API                     │
│  - Event validation             │
└────────┬────────────────────────┘
         │
         ▼
┌─────────────────────────────────┐
│       Apache Kafka              │
│  Topic: network.events.v1       │
└────────┬────────────────────────┘
         │
         ▼
┌─────────────────────────────────┐
│   Analytics Service             │
│   (Spring Boot :8082)           │
│   - Kafka Consumer              │
│   - 5-min Windowing             │
│   - QoE Calculation             │
└────────┬────────────────────────┘
         │
    ┌────┴────┐
    ▼         ▼
┌────────┐ ┌────────┐
│ Postgres│ │ Redis  │
│  :5432  │ │ :6379  │
└────┬───┘ └───┬────┘
     │         │
     └────┬────┘
          │
          ▼
┌─────────────────────────────────┐
│      API Service                │
│   (Spring Boot :8080)           │
│   - REST Endpoints              │
│   - Query KPIs                  │
└────────┬────────────────────────┘
         │
         ▼
┌─────────────────────────────────┐
│    Dashboard Web (React)        │
│         (:3000)                 │
│   - Real-time Charts            │
│   - Filters & KPI Cards         │
└─────────────────────────────────┘
```

## Component Details

### 1. Event Ingestion Service

**Responsibilities:**
- Accept network event data via REST API
- Validate incoming events
- Publish events to Kafka topics

**Technology:**
- Java 17
- Spring Boot 3.3.5
- Spring Kafka

**Key Classes:**
- `EventController` - REST endpoint handler
- `EventProducer` - Kafka message publisher
- `NetworkEvent` - Event data model

**API Endpoints:**
- `POST /events/network` - Ingest network events

### 2. Analytics Service

**Responsibilities:**
- Consume events from Kafka
- Aggregate events into 5-minute windows
- Calculate QoE scores and metrics
- Store aggregated data in PostgreSQL
- Cache latest metrics in Redis

**Technology:**
- Java 17
- Spring Boot 3.3.5
- Spring Kafka
- Spring Data JPA
- Spring Data Redis

**Key Classes:**
- `EventConsumer` - Kafka message consumer
- `WindowAggregator` - Event aggregation logic
- `QoeScoring` - QoE calculation algorithm
- `ExperienceKpi` - JPA entity
- `FlushScheduler` - Periodic window flushing

**Aggregation Strategy:**
- 5-minute tumbling windows
- Group by: region, device type, app name
- Flush when: 200 events accumulated OR window expired

### 3. API Service

**Responsibilities:**
- Provide REST APIs for dashboard
- Query aggregated KPIs from PostgreSQL
- Support filtering and time-range queries

**Technology:**
- Java 17
- Spring Boot 3.3.5
- Spring Data JPA
- Spring Data Redis

**Key Classes:**
- `KpiController` - REST endpoint handler
- `ExperienceKpiRepo` - JPA repository

**API Endpoints:**
- `GET /api/kpis/latest?limit=N` - Get latest N KPIs
- `GET /api/kpis/range?from=T1&to=T2` - Get KPIs in time range

### 4. Dashboard Web

**Responsibilities:**
- Display real-time metrics and charts
- Provide filtering by region and app
- Auto-refresh every 10 seconds

**Technology:**
- React 18
- Vite
- Recharts (visualization)
- Axios (HTTP client)

**Key Components:**
- `App` - Main dashboard component
- `KpiCard` - Metric display card
- `ChartBlock` - Time-series chart

## Data Flow

### Event Ingestion Flow

1. **Event Generation**: Network/app generates performance metrics
2. **HTTP POST**: Client sends event to ingestion service
3. **Validation**: Event structure validated
4. **Kafka Publish**: Event published to `network.events.v1` topic
5. **Response**: HTTP 202 Accepted returned

### Analytics Processing Flow

1. **Kafka Consume**: Analytics service consumes events
2. **Window Assignment**: Event assigned to 5-minute window
3. **Buffering**: Events buffered in-memory per window
4. **Aggregation**: Calculate avg latency, jitter, loss, signal
5. **QoE Calculation**: Compute weighted QoE score (0-100)
6. **Persistence**: Save to PostgreSQL
7. **Caching**: Update latest metrics in Redis

### Dashboard Query Flow

1. **User Opens Dashboard**: React app loads
2. **Initial Fetch**: GET /api/kpis/latest
3. **Data Rendering**: Charts and KPI cards updated
4. **Auto-Refresh**: Polls API every 10 seconds
5. **Filter Applied**: Re-filter data client-side

## Database Schema

### PostgreSQL Table: `experience_kpis`

```sql
CREATE TABLE experience_kpis (
    id BIGSERIAL PRIMARY KEY,
    window_start TIMESTAMP NOT NULL,
    window_end TIMESTAMP NOT NULL,
    region VARCHAR(100) NOT NULL,
    device_type VARCHAR(50) NOT NULL,
    app_name VARCHAR(100) NOT NULL,
    avg_latency_ms DOUBLE PRECISION,
    avg_jitter_ms DOUBLE PRECISION,
    avg_packet_loss_pct DOUBLE PRECISION,
    avg_signal_dbm DOUBLE PRECISION,
    qoe_score DOUBLE PRECISION
);

CREATE INDEX idx_window_end ON experience_kpis(window_end DESC);
CREATE INDEX idx_region_device_app ON experience_kpis(region, device_type, app_name);
```

### Redis Cache Structure

```
Key: "latest:{region}:{deviceType}:{appName}"
Value: QoE score (string)
TTL: No expiration (overwritten on update)
```

## QoE Scoring Algorithm

### Formula

```
QoE = 0.4 × LatencyScore + 0.25 × JitterScore + 0.25 × LossScore + 0.1 × SignalScore
```

### Component Calculations

**Latency Score:**
```
LatencyScore = clamp(100 - (latency / 5.0), 0, 100)
```

**Jitter Score:**
```
JitterScore = clamp(100 - (jitter × 2.0), 0, 100)
```

**Loss Score:**
```
LossScore = clamp(100 - (packetLoss × 15.0), 0, 100)
```

**Signal Score:**
```
SignalScore = clamp((signalDbm + 120) × 1.25, 0, 100)
```

### Interpretation

- **90-100**: Excellent experience
- **75-89**: Good experience
- **60-74**: Fair experience
- **40-59**: Poor experience
- **0-39**: Very poor experience

## Scalability Considerations

### Current Design

- Single instance of each service
- In-memory window buffering
- PostgreSQL for storage
- Redis for caching

### Future Scaling Options

1. **Horizontal Scaling**
   - Multiple analytics service instances
   - Kafka partitioning by region/device
   - Load balancer for API service

2. **Data Storage**
   - TimescaleDB for time-series optimization
   - Partitioning by time range
   - Data retention policies

3. **Stream Processing**
   - Apache Flink for complex event processing
   - Real-time anomaly detection
   - Predictive analytics

4. **Caching Strategy**
   - Redis cluster for high availability
   - Cache warming strategies
   - TTL-based cache invalidation

## Monitoring & Observability

### Recommended Metrics

**Ingestion Service:**
- Events received per second
- Kafka publish latency
- Error rate

**Analytics Service:**
- Kafka consumer lag
- Window flush rate
- Processing time per event

**API Service:**
- Request rate
- Response time
- Cache hit rate

### Logging Strategy

- Structured logging (JSON format)
- Correlation IDs across services
- Log aggregation (ELK/Splunk)

## Security Considerations

### Current State

- No authentication/authorization
- Open CORS policy
- Unencrypted communication

### Future Enhancements

1. **Authentication**
   - JWT-based auth
   - OAuth2 integration
   - API keys for ingestion

2. **Authorization**
   - Role-based access control (RBAC)
   - Tenant isolation
   - Resource-level permissions

3. **Encryption**
   - TLS for all services
   - Encrypted Kafka topics
   - Database encryption at rest

## Deployment

### Docker Compose (Development)

```bash
docker compose up --build
```

### Kubernetes (Production)

- Deploy each service as separate deployment
- Use Kafka operator (Strimzi)
- PostgreSQL operator or managed service
- Redis StatefulSet or managed service
- Ingress for API/Dashboard

## Performance Targets

- **Ingestion Throughput**: 10,000 events/sec
- **Analytics Latency**: < 5 seconds (window to DB)
- **API Response Time**: < 100ms (p95)
- **Dashboard Refresh**: Every 10 seconds

## Disaster Recovery

### Backup Strategy

- PostgreSQL daily backups
- Kafka topic replication (production)
- Redis snapshot backups

### Recovery Procedures

1. Restore PostgreSQL from backup
2. Replay Kafka messages if needed
3. Rebuild Redis cache from PostgreSQL