# Banking Microservices System

A production-grade distributed banking backend built with Java and Spring Boot, demonstrating microservices architecture, event-driven communication, and real-world banking patterns.

---

## Architecture

```
Client
  │
  ▼
API Gateway (Rate Limiting + JWT Validation)
  │
  ├──► Auth Service         (JWT issuing, refresh tokens, RBAC)
  ├──► Account Service      (account management, Redis caching)
  ├──► Transaction Service  (fund transfers, idempotency, Kafka producer)
  └──► Notification Service (Kafka consumer, email/SMS simulation)

Infrastructure:
  - Eureka Server     → Service Discovery
  - Docker Compose    → Container orchestration
  - Redis             → Caching + Idempotency
  - Kafka             → Async event streaming
  - MySQL             → Persistent storage
```

---

## Services

| Service | Responsibility |
|---|---|
| **API Gateway** | Single entry point, JWT validation, rate limiting per user |
| **Auth Service** | User registration/login, JWT access + refresh token issuance, RBAC |
| **Account Service** | Account CRUD, balance management, Redis-cached balance reads |
| **Transaction Service** | Fund transfers with idempotency, Kafka event publishing |
| **Notification Service** | Kafka consumer, processes transaction events, simulates alerts |
| **Eureka Server** | Service registry and discovery for all microservices |

---

## Tech Stack

| Category | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Security | Spring Security, JWT (Access + Refresh tokens), RBAC |
| Microservices | Spring Cloud (Eureka, OpenFeign, API Gateway, Circuit Breaker) |
| Messaging | Apache Kafka |
| Caching | Redis (Cache-aside pattern, TTL-based eviction) |
| Database | MySQL + Spring Data JPA / Hibernate |
| Containerization | Docker, Docker Compose |
| API | REST APIs, Idempotency |

---

## Key Design Decisions

### 1. Idempotency in Transaction Service
Every fund transfer request requires an `Idempotency-Key`. The key is stored in Redis with a TTL. Duplicate requests within the TTL window return the cached response — preventing double debits in distributed environments.

### 2. Event-Driven Notification
Transaction Service publishes a `TransactionEvent` to a Kafka topic after every successful transfer. Notification Service consumes this event asynchronously — keeping services fully decoupled.

### 3. Circuit Breaker
Inter-service calls (e.g., Transaction → Account) are wrapped with Resilience4j Circuit Breaker. If Account Service is unavailable, the circuit opens and a fallback response is returned — preventing cascading failures.

### 4. Rate Limiting at Gateway
API Gateway enforces per-user rate limits using Redis token bucket. Exceeding the limit returns `429 Too Many Requests` — protecting downstream services from abuse.

### 5. JWT + RBAC
Auth Service issues JWTs with embedded roles (`CUSTOMER`, `ADMIN`). API Gateway validates the token on every request. Role-based access is enforced at controller level using Spring Security.

### 6. Redis Caching for Account Reads
Account balance reads are cached in Redis with a short TTL. Cache is evicted on every balance update — reducing DB load for high-frequency read operations.

---

## Running Locally

### Prerequisites
- Docker + Docker Compose installed
- Java 17+
- Maven

### Steps

```bash
# Clone the repo
git clone https://github.com/sep-2909/banking-microservices-system.git
cd banking-microservices-system

# Start all services
cd docker-compose
docker-compose up --build
```

### Service Ports

| Service | Port |
|---|---|
| API Gateway | 8000 |
| Auth Service | 8081 |
| Account Service | 8082 |
| Transaction Service | 8083 |
| Notification Service | 8084 |
| Eureka Dashboard | 8761 |

---

## API Highlights

### Auth
```
POST /auth/register    → Register new user
POST /auth/login       → Get JWT access + refresh token
POST /auth/refresh     → Rotate refresh token
```

### Account
```
POST   /accounts          → Create account
GET    /accounts/{id}     → Get account (cached)
GET    /accounts/{id}/balance → Get balance
```

### Transaction
```
POST /transactions/transfer → Fund transfer (requires Idempotency-Key)
GET  /transactions/{accountNumber}  → Paginated transaction history
```

---

## Author

**Sakshi Jaiswal**  
Software Engineer | Java Backend 
[LinkedIn](https://www.linkedin.com/in/sakshi-jaiswal-nit) | sakshijaiswal1304@gmail.com
