# SB-ECommerce-Web-Application

A backend for an e-commerce system built using **Spring Boot**.  
The goal of this project was not just to create APIs, but to run the application in a real environment with database, caching and security — similar to how an actual backend service works.

---

## Tech Stack

- **Backend:** Spring Boot (Java)
- **Frontend:** React (separate project)
- **Database:** PostgreSQL
- **Cache:** Redis
- **Authentication:** JWT stored in HTTP-only cookies
- **Containerization:** Docker & Docker Compose
- **Deployment:** AWS EC2

---

## What the backend supports

The application handles typical e-commerce features:

- User authentication & authorization
- Categories & products
- Cart management
- Orders & order items
- Address & payment flow
- Pagination for large product lists

---

## Security

Authentication is implemented using **JWT cookies**.

When a user logs in:

- Server generates a token
- Token stored in HTTP-only cookie
- Every request is validated using a custom filter

This avoids storing passwords in sessions and keeps APIs stateless.

---

## Caching (Redis)

Product listing APIs are cached using Redis.

**Why**
- Product data is read very often
- Database queries become expensive

**What happens**
- First request → data comes from database
- Next requests → served from cache
- Product update/delete → cache cleared automatically

This reduces repeated DB calls and improves response time.

---

## Rate Limiting

To protect APIs from abuse (like too many login attempts), rate limiting is applied.

**Example**
- Login endpoint → limited attempts per minute
- Public product APIs → higher limit

If limit exceeds → server returns **429 Too Many Requests**

---

## Running the system (Docker Compose)

The whole backend runs as multiple services:

- Spring Boot application
- PostgreSQL database
- Redis cache

This allows the backend to behave like a real deployed system instead of a local-only project.

```bash
docker compose up --build
```

---

## Deployment (AWS EC2)

The application is deployed on an EC2 instance using Docker Compose.

**Flow**

1. Build backend image
2. Pull on server
3. Run containers together (app + db + redis)

The backend is accessible using the public IP of the instance.

---

## Why I built this

Most tutorials stop after creating APIs.  
In this project I focused on running the backend the way real services run:

- Authentication without sessions
- Caching frequently accessed data
- Preventing abuse with rate limiting
- Running multiple services together
- Deploying to a cloud machine

---

## Future Improvements

- Async order processing using message queue
- Monitoring with Prometheus & Grafana
- Payment gateway integration

<h4>
Thank you for checking out my project :)
</h4>