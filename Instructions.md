# Popcorn Palace – Instructions

## Overview

**Popcorn Palace** is a Spring Boot application that manages movies, showtimes, and ticket bookings. It uses PostgreSQL for data persistence (with a Docker container recommended for a production‑like environment) and includes RESTful APIs for managing movies, theaters, bookings and showtimes.

---

## Prerequisites

- **Java**: JDK 21 or later
- **Maven**: Version 3.8+
- **Docker**: To run PostgreSQL  
  *(Alternatively, you can modify the configuration to use an in‑memory H2 database for local testing)*

---

## Building the Project

### Clone the Repository

```bash
git clone <repository-url>
cd popcorn-palace
```

### Build the Project

Run the following command to clean and build the project:

```bash
mvn clean install
```

This command creates an executable JAR file in the `target` directory.

---

## Running the Application

### Option 1: Using Docker with PostgreSQL

#### Start PostgreSQL Using Docker Compose

A file named `compose.yml` is provided with the following content:

```yaml
services:
  db:
    image: postgres
    restart: always
    environment:
      POSTGRES_USER: popcorn-palace
      POSTGRES_PASSWORD: popcorn-palace
      POSTGRES_DB: popcorn-palace
    #  volumes:
    #   - type: bind
    #     source: ./data
    #     target: /var/lib/postgresql/data --> in case u need to save data to local storage
    ports:
      - target: 5432
        published: 5432
        protocol: tcp
```

Run the file using the following command:

```bash
docker-compose up -d
```

#### Verify PostgreSQL is Running

Ensure that PostgreSQL is accessible on port `5432` and that the database, user, and password match those specified in `src/main/resources/application.yml`:

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

#### Run the Application

Start the application using Maven:

```bash
mvn spring-boot:run
```

---

### Option 2: Using an In‑Memory H2 Database (for testing/development)

If you prefer not to use Docker or PostgreSQL for local testing, temporarily update your `application.yml` as follows:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driverClassName: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update
```

Then run:

```bash
mvn spring-boot:run
```

---

## Running the Tests

To run all tests, use:

```bash
mvn test
```

This will execute the tests defined in the project and display the results in the console.

---

## API Documentation

The project uses Swagger/OpenAPI for API documentation. Once the application is running, open your web browser and navigate to:

```text
http://localhost:8080/swagger-ui.html
```

This page provides a complete list of available APIs, including details about endpoints, required parameters, request and response formats, and error messages.

---

## Error Handling & Data Validation

- The application uses global exception handlers to return informative error messages.
- Input validation is performed at the DTO level using JSR 380 annotations (e.g., `@NotNull`, `@NotBlank`, `@Size`).

---

## Troubleshooting

### Database Connectivity Errors

Verify your PostgreSQL container is running and that the credentials in `application.yml` match your PostgreSQL configuration.

### Swagger UI Issues

Ensure all Maven dependencies (including `springdoc-openapi`) are correctly reloaded. If problems persist, try cleaning your Maven cache and re-importing the project.

### Validation or API Errors

Check the error messages in the API response body for details on what might be causing the problem.

---

## Additional Notes

### Persistence

The project uses JPA/Hibernate for ORM and PostgreSQL as the database. Docker is used to run PostgreSQL in a containerized environment.

### Validation

DTO classes use validation annotations (e.g., @NotBlank, @Size) to enforce constraints on incoming data. Custom exception handling returns informative error messages.

### Testing

Tests are provided and can be run using Maven. Each test runs in its own transaction which is rolled back after completion.

### API Behavior

Movies, showtimes, and theaters cannot be deleted if they are associated with active relationships.



