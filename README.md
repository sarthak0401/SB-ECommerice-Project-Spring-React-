# SB-ECommerce Web Application (Backend)

A production-style e-commerce backend built using Spring Boot.

The goal of this project was not only to build APIs, but to run the backend in a realistic environment with authentication, caching, rate limiting, monitoring and containerized deployment — similar to how real backend services operate.

---

## Tech Stack

**Backend:** Spring Boot (Java)
**Frontend:** React (separate project)
**Database:** PostgreSQL
**Cache & Rate Limiting:** Redis
**Authentication:** JWT (HTTP-only cookies)
**Containerization:** Docker & Docker Compose
**Orchestration:** Kubernetes
**Monitoring:** Prometheus & Grafana
**Deployment:** AWS EC2

---

## Backend Capabilities

The backend supports common e-commerce workflows:

* User authentication & authorization
* Product categories & product listing
* Cart management
* Orders & order items
* Address & checkout flow
* Pagination for large datasets

---

## Authentication and Security

Authentication is implemented using stateless JWT cookies.

### Flow

1. User logs in
2. Server generates JWT
3. Token stored in HTTP-only cookie
4. Custom Spring Security filter validates every request

This removes server sessions and keeps the system horizontally scalable.

---

## Redis Caching

Product listing endpoints are cached using Redis.

### Why caching

Product data is read frequently but rarely updated.
Repeated database queries increase latency and load.

### Behaviour

* First request → database hit → cached
* Next requests → served from Redis
* Product update/delete → cache invalidated automatically

This significantly reduces database load and improves response time.

See [demo.md](./demo.md) for cache hit vs miss behaviour.

---

## Rate Limiting

Redis is also used for API rate limiting to prevent abuse.

### Examples

* Login endpoint → limited attempts per minute
* Public product APIs → higher request limit

If exceeded, server returns:

HTTP 429 Too Many Requests

Demonstration available in [demo.md](./demo.md).

---

## Monitoring (Prometheus & Grafana)

The application exposes metrics using Spring Boot Actuator and Micrometer which are scraped by Prometheus and visualized in Grafana dashboards.

### What is monitored

* HTTP request count
* JVM memory usage
* CPU usage
* Login success/failure rate
* Order Success/Failure rate
* Rate limiter activity

This allows observing system behaviour under load and validating caching and rate limiting effectiveness.

---

## Running Locally (Docker Compose)

The backend runs as multiple coordinated services:

* Spring Boot application
* PostgreSQL database
* Redis cache
* Prometheus
* Grafana

This replicates a real deployed backend environment rather than a single local process.

`docker compose up --build`

Full walkthrough: [demo.md](./demo.md)

---

## Kubernetes Deployment

The application can run with multiple replicas in Kubernetes.

This demonstrates a distributed systems concept:

Local memory cache fails across pods, but Redis works as a shared distributed cache.

### What is shown

* Multiple pods serve requests
* First request hits database
* All other pods use shared Redis cache
* Horizontal scaling without database overload

Full distributed behaviour demo: [k8s-demo.md](./k8s-demo.md)

---

## AWS Deployment

The system is deployed on an AWS EC2 instance using Docker Compose and with Kubernetes system as well

### Deployment Flow

1. Build backend image
2. Pull image on server
3. Run app + database + redis + monitoring stack together

The backend becomes accessible via the instance public IP.

---

## Why This Project

Most tutorials stop after building CRUD APIs.

This project focuses on how backend services actually run in production:

* Stateless authentication
* Distributed caching
* Rate limiting
* Monitoring & observability
* Multi-service container setup
* Horizontal scalability
* Cloud deployment

Every feature is demonstrated with observable behaviour rather than assumptions.

See:

* [Local behaviour demo](./demo.md) — local behaviour
* [Distributed behaviour demo](./k8s-demo.md) — distributed behaviour

---

## Future Improvements

* Async order processing using message queue
* Payment gateway integration
* Distributed tracing
