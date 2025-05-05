# Person Management Application

## Design Decisions & Production Considerations

### Domain Modeling

This application follows a clean, normalized domain model with three key entities:

- **Person** – Represents individuals with contact and demographic information.
- **Address** – A standalone entity, allowing reuse and normalization of location data. Enables advanced features like geolocation or postal validation.
- **Language** – A many-to-many relationship with Person, reflecting multilingual capabilities.

This structure ensures **data consistency**, avoids redundancy, and supports flexible evolution as the system grows. DTOs (Data Transfer Objects) are used to expose clean API contracts and decouple persistence models from external consumers.

##### Why These Tables?

- **Country**
  - Represents the country associated with a person or address.
  - Modeled as a separate entity to support referential integrity, standardized naming, and potential international features like country codes, flags, or region-specific logic.
  - This structure makes filtering, validation, and consistency across records much easier.

- **person_language** (Join Table)
  - Supports the many-to-many relationship between persons and languages.
  - This normalized structure enables each person to have multiple languages and each language to be linked to many people.
  - Using a join table ensures referential integrity and allows efficient querying and filtering (e.g., "find all people who speak German").

- **Country**
  - Represents the country associated with a person or address.
  - Modeled as a separate entity to support referential integrity, standardized naming, and potential international features like country codes, flags, or region-specific logic.
  - This structure makes filtering, validation, and consistency across records much easier.


- **Person**
  - Core entity representing individuals with fields like name, surname, birthdate, email, and phone.
  - Acts as an aggregate root and owns associations with address and language.
  - Keeping it as a separate table enables CRUD operations on individuals independently and cleanly.

- **Address**
  - Stored in a standalone table to avoid duplication, especially if address reuse or multiple addresses per person are introduced in the future.
  - Normalized to make it easier to integrate geolocation services or validate postal information later.

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

#### Security & Resilience
- **Authentication/Authorization**: Easily extensible with **Spring Security** and JWT-based authentication.
- **Input Validation**: DTOs are annotated for validation using constraints, protecting against malformed or malicious input.

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