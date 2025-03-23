# 🎬 Popcorn Palace – Movie Ticket Booking System

**Popcorn Palace** is a Spring Boot application built as part of a home assignment. It simulates a movie ticket booking backend system, exposing RESTful APIs to manage movies, showtimes, theaters, and bookings.

> ⚠️ **Note**: This is a toy project developed for educational and evaluation purposes. It is not intended for production use.

---

## 🚀 Features

- 🎞️ Manage movies, showtimes, theaters, and ticket bookings
- 🐘 PostgreSQL support via Docker Compose
- 🧪 H2 in-memory DB support for quick local testing
- 🧾 Swagger/OpenAPI for live API documentation
- ✅ Input validation with JSR 380 annotations
- 🔐 Global exception handling with helpful messages

---

## 📦 Prerequisites

- **Java**: JDK 21+
- **Maven**: 3.8+
- **Docker**: To run PostgreSQL  
  *(H2 can also be used for development)*

---

## ⚙️ Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd popcorn-palace
```

### 2. Build the Project

```bash
mvn clean install
```

---

## 🐳 Running with Docker + PostgreSQL

### 1. Start PostgreSQL Container

Run this from the root of the project:

```bash
docker-compose up -d
```

This will launch a PostgreSQL container with:
- DB: `popcorn-palace`
- User: `popcorn-palace`
- Password: `popcorn-palace`

### 2. Sample `application.yml` Configuration

```yaml
server:
  port: 8080

spring:
  application:
    name: popcorn-palace
  datasource:
    url: jdbc:postgresql://localhost:5432/popcorn-palace
    username: popcorn-palace
    password: popcorn-palace
    driverClassName: org.postgresql.Driver
    platform: postgres
  jpa:
    database: POSTGRESQL
    show-sql: true
    hibernate:
      ddl-auto: update
  sql:
    init:
      mode: always
```

### 3. Run the App

```bash
mvn spring-boot:run
```

---

## ⚡ Running with H2 (In-Memory DB)

### Update `application.yml`:

```yaml
server:
  port: 8081  # Optional: use a different port

spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driverClassName: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update
  h2:
    console:
      enabled: true
```

Then run:

```bash
mvn spring-boot:run
```

---

## 🧪 Running Tests

Run all tests with:

```bash
mvn test
```

---

## 📖 Swagger API Documentation

Once the app is running, open:

```
http://localhost:8080/swagger-ui.html
```

Or for H2 profile:

```
http://localhost:8081/swagger-ui.html
```

---

## 📚 API Reference

### Movies  APIs

| API Description           | Endpoint               | Request Body                          | Response Status | Response Body |
|---------------------------|------------------------|---------------------------------------|-----------------|---------------|
| Get all movies | GET /movies/all | | 200 OK | [ { "id": 12345, "title": "Sample Movie Title 1", "genre": "Action", "duration": 120, "rating": 8.7, "releaseYear": 2025 }, { "id": 67890, "title": "Sample Movie Title 2", "genre": "Comedy", "duration": 90, "rating": 7.5, "releaseYear": 2024 } ] |
| Add a movie | POST /movies | { "title": "Sample Movie Title", "genre": "Action", "duration": 120, "rating": 8.7, "releaseYear": 2025 } | 200 OK | { "id": 1, "title": "Sample Movie Title", "genre": "Action", "duration": 120, "rating": 8.7, "releaseYear": 2025 }|
| Update a movie | POST /movies/update/{movieTitle} | { "title": "Sample Movie Title", "genre": "Action", "duration": 120, "rating": 8.7, "releaseYear": 2025 } | 200 OK | |
| DELETE /movies/{movieTitle} | | 200 OK | |

### Showtimes APIs

| API Description            | Endpoint                           | Request Body                                                                                                                                      | Response Status | Response Body                                                                                                                                                                                                                                                                   |
|----------------------------|------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|-----------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Get showtime by ID | GET /showtimes/{showtimeId} |                                                                                                                                                   | 200 OK | { "id": 1, "price":50.2, "movieId": 1, "theater": "Sample Theater", "startTime": "2025-02-14T11:47:46.125405Z", "endTime": "2025-02-14T14:47:46.125405Z" }                                                                                                                      |
| Add a showtime | POST /showtimes | { "movieId": 1, "price":20.2, "theater": "Sample Theater", "startTime": "2025-02-14T11:47:46.125405Z", "endTime": "2025-02-14T14:47:46.125405Z" } | 200 OK | { "id": 1, "price":50.2,"movieId": 1, "theater": "Sample Theater", "startTime": "2025-02-14T11:47:46.125405Z", "endTime": "2025-02-14T14:47:46.125405Z" }                                                                                                                                    |
| Update a showtime | POST /showtimes/update/{showtimeId}| { "movieId": 1, "price":50.2, "theater": "Sample Theater", "startTime": "2025-02-14T11:47:46.125405Z", "endTime": "2025-02-14T14:47:46.125405Z" } | 200 OK |                                                                                                                                                                                                                                                                                 |
| Delete a showtime | DELETE /showtimes/{showtimeId} |                                                                                                                                                   | 200 OK |                                                                                                                                                                                                                                                                                 |





### bookings APIs

| API Description           | Endpoint       | Request Body                                     | Response Status | Response Body                                                                                                                                          |
|---------------------------|----------------|--------------------------------------------------|-----------------|--------------------------------------------------------------------------------------------------------------------------------------------------------|
| Book a ticket | POST /bookings | { "showtimeId": 1, "seatNumber": 15 , userId:"84438967-f68f-4fa0-b620-0f08217e76af"} | 200 OK | { "bookingId":"d1a6423b-4469-4b00-8c5f-e3cfc42eacae" }                                                                                                 |


---

## 🚀 Quick Start Recap

- `docker-compose up -d` — starts the DB
- `mvn spring-boot:run` — runs the app
- Visit `http://localhost:8080/swagger-ui.html` — see & test the API

---

## 📄 License

This project is provided for educational use only as part of a home assignment. No license is attached.



