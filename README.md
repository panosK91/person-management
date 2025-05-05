# Person Management Application

## Design Decisions & Production Considerations

### Domain Modeling

This application follows a clean, normalized domain model with three key entities:

- **Person** – Represents individuals with contact and demographic information.
- **Address** – A standalone entity, allowing reuse and normalization of location data. Enables advanced features like geolocation or postal validation.
- **Language** – A many-to-many relationship with Person, reflecting multilingual capabilities.

This structure ensures **data consistency**, avoids redundancy, and supports flexible evolution as the system grows. DTOs (Data Transfer Objects) are used to expose clean API contracts and decouple persistence models from external consumers.

### Application Architecture

The backend uses a **layered architecture** with:

- **Controllers** handling HTTP requests.
- **Services** encapsulating business logic.
- **Repositories** performing database operations (via Spring Data JPA).
- **DTO mappers** converting between entities and API models.


### Production-Ready Scaling & Observability

#### Scalability
- **Stateless Architecture**: Easily horizontally scalable using Docker, Kubernetes, or cloud platforms like AWS ECS/GKE.
- **Read Optimization**: Frequently accessed resources (e.g., language lists) can be cached using **Spring Cache + Redis**.
- **Efficient DB Access**: Indexed fields, connection pooling (via **HikariCP**), and `@Transactional` boundaries keep DB interaction efficient.

#### Observability
- **Metrics Collection**: Integrated with **Micrometer** for application-level metrics (request latency, active DB connections, memory usage).
- **Monitoring Stack**: Metrics can be scraped by **Prometheus** and visualized via **Grafana** dashboards. Alerting thresholds (e.g., error rates, latency) can be set for proactive response.
- **Structured Logging**: Centralized via **ELK Stack** or **Grafana Loki**, enabling full-text search and traceability across services.

#### Security & Resilience
- **Authentication/Authorization**: Easily extensible with **Spring Security** and JWT-based authentication.
- **Fault Tolerance**: **Resilience4j** or **Spring Cloud Circuit Breaker** can be used for retries, fallback strategies, and bulkhead isolation.
- **Input Validation**: DTOs are annotated for validation using JSR-303 (`@Valid`) constraints, protecting against malformed or malicious input.

#### CI/CD and Testing
- **Automated Testing**: Unit tests (via JUnit 5) and integration tests (via **Testcontainers**) ensure correctness across environments.
- **DevOps-Ready**: Compatible with CI/CD tools like **GitHub Actions**, **GitLab CI**, or **Jenkins**, allowing automated build, test, and deploy pipelines.

## Startup Instructions

### 1. Start PostgreSQL Database

From the root of the project, run:

```bash
docker compose up -d
```

This spins up PostgreSQL with the schema and seed data (defined in `init.sql`).

---

### 2. Start the Backend

Navigate into the backend directory:

```bash
cd person-management-backend
```

Then run:

#### On Windows (Command Prompt or PowerShell):
```bash
mvnw spring-boot:run
```

#### On Linux/Mac or Git Bash:
```bash
./mvnw spring-boot:run
```

This starts the Spring Boot API at:

```
http://localhost:8080
```

---

### 3. Start the Frontend

Navigate into the frontend directory:

```bash
cd person-management-frontend
```

Install dependencies (only once):

```bash
yarn install
```

Then start the frontend:

```bash
yarn dev
```

The UI will be available at:

```
http://localhost:8081
```