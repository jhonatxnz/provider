# Provider API

Java + Spring Boot service that owns customer registration and subscription management. It exposes a
JWT-secured REST API consumed by the `consumer` API, publishes subscription lifecycle events to Kafka,
and ships a full local observability stack (metrics, logs, dashboards) via Docker Compose.

## Stack

| Layer | Technology |
|---|---|
| Language / runtime | Java 21 (LTS) |
| Framework | Spring Boot 4.1.0 |
| Build | Gradle 9.7 (wrapper, `./gradlew`) |
| Web | `spring-boot-starter-webmvc` |
| Persistence | Spring Data JPA + MySQL 8.4 (`com.mysql:mysql-connector-j`) |
| Messaging | Spring for Apache Kafka (`spring-boot-starter-kafka`) |
| Auth | Spring Security + JJWT (`io.jsonwebtoken:jjwt`) — stateless client-credentials JWT flow |
| API docs | springdoc-openapi (Swagger UI + OpenAPI 3) |
| Email | Spring Mail (`JavaMailSender`, SMTP) |
| Observability | Spring Boot Actuator + Micrometer (Prometheus registry) |
| Logging | Log4j2, console + JSON file appender (Logstash layout) |
| Log shipping | Fluent Bit → OpenSearch → OpenSearch Dashboards |
| Local infra | Docker Compose (MySQL, Kafka, Kafka UI, Prometheus, Grafana, OpenSearch, OpenSearch Dashboards, Fluent Bit) |
| Object mapping / boilerplate | Lombok |
| Tests | JUnit 5, Spring Boot Test (web, data-jpa, kafka slices), integration tests under `src/test/java/integration` |

## Package structure

```
br.com.jhonatan.provider
├── config              # OpenAPI/Swagger configuration
├── controller           # @RestController (customers, subscriptions, auth)
├── dto                  # request/response payloads
├── enums                 # SubscriptionStatus, Actions
├── event                 # Kafka event payloads
├── exception              # domain exceptions
├── infra
│   ├── exceptions        # @RestControllerAdvice + error response (active handler)
│   └── security           # JwtService, JwtAuthenticationFilter, SecurityConfig, SecurityProperties
├── kafka
│   ├── config             # topic declarations
│   ├── consumer           # @KafkaListener (subscription lifecycle → email)
│   └── producer            # publishes subscription lifecycle events
├── model                 # JPA entities
├── repository             # Spring Data JPA repositories
├── service                # business logic
└── utils                  # document/email/name/phone validation & normalization
```

## Prerequisites

- JDK 21
- Docker + Docker Compose (the app drives `compose.yaml` for you via Spring Boot's Docker Compose
  support — you don't need to run `docker compose up` manually)

## Configuration

Two layers of configuration are used:

1. **`.env`** (project root, already git-ignored) — consumed by `compose.yaml` for the containers'
   own credentials:

   ```
   MYSQL_PASSWORD=...
   MYSQL_ROOT_PASSWORD=...
   OPENSEARCH_ADMIN_PASSWORD=...
   GRAFANA_ADMIN_PASSWORD=...
   ```

2. **Application environment variables** (set them in your IDE run configuration, shell profile, or a
   second `.env` loaded by your IDE) — consumed directly by `application.yml`:

   | Variable | Purpose | Notes |
   |---|---|---|
   | `SERVER_PORT` | HTTP port | defaults to `8080` |
   | `SPRING_DATASOURCE_URL` / `_USERNAME` / `_PASSWORD` | MySQL connection | defaults match `compose.yaml` |
   | `SPRING_KAFKA_BOOTSTRAP_SERVERS` | Kafka broker | defaults to `localhost:9092` |
   | `MAIL_HOST` / `MAIL_PORT` / `MAIL_USERNAME` / `MAIL_PASSWORD` | SMTP for outgoing email | for Gmail, `MAIL_PASSWORD` must be an [app password](https://myaccount.google.com/apppasswords), not your login password |
   | `JWT_SECRET` | HMAC signing key for issued JWTs | **required, no default** — pick a long random string (32+ bytes) |
   | `CONSUMER_CLIENT_SECRET` | credential for the `consumer-api` client | **must be stored as a BCrypt hash**, since `AuthController` validates it with `PasswordEncoder.matches(raw, stored)` — encode the plaintext secret once (e.g. `new BCryptPasswordEncoder().encode("your-secret")`) and put the hash here, never the plaintext |

   Since `JWT_SECRET` and `CONSUMER_CLIENT_SECRET` have no defaults in `application.yml`, the app
   **will fail to start** if they're missing — that's intentional (fail-fast instead of running with a
   predictable/empty secret).

## Running locally

```bash
./gradlew bootRun
```

Spring Boot's Docker Compose integration brings up every service declared in `compose.yaml`
automatically (MySQL gets automatic service connection — host/port/credentials are wired into the
`DataSource` for you). First boot creates the schema via Hibernate (`ddl-auto: update`).

| Service | URL | Notes |
|---|---|---|
| Provider API | http://localhost:8080 | |
| Swagger UI | http://localhost:8080/swagger-ui.html | |
| OpenAPI JSON | http://localhost:8080/v3/api-docs | |
| MySQL | localhost:3308 | mapped from container port 3306 |
| Kafka | localhost:9092 | |
| Kafka UI | http://localhost:8083 | topic/message inspector |
| Prometheus | http://localhost:9090 | scrapes `/actuator/prometheus` every 15s |
| Grafana | http://localhost:3000 | login `admin` / `$GRAFANA_ADMIN_PASSWORD` |
| OpenSearch | http://localhost:9200 | |
| OpenSearch Dashboards | http://localhost:5601 | receives app logs shipped by Fluent Bit |

> For production, switch `spring.jpa.hibernate.ddl-auto` from `update` to `validate`/`none` and manage
> the schema with a migration tool (Flyway/Liquibase) — `ddl-auto: update` is convenient for local dev
> only.

## Authentication

The API uses a stateless, service-to-service **client-credentials** flow:

1. `POST /api/auth/token` with `{ "clientId": "consumer-api", "clientSecret": "..." }` (the plaintext
   secret, matched server-side against the configured BCrypt hash).
2. Response: a Bearer JWT (`accessToken`), valid for `security.jwt.expiration-ms` (1 hour by default).
3. Send it as `Authorization: Bearer <token>` on every other request.

`/api/auth/token`, `/swagger-ui/**`, `/v3/api-docs/**`, `/actuator/health` and `/actuator/prometheus`
are public; everything else requires a valid token.

## API overview

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/auth/token` | Issue a JWT for a registered client |
| `GET` | `/api/customers/{username}` | Fetch a customer by username |
| `GET` | `/api/customers/document/{document}` | Fetch a customer by document (CPF/CNPJ) |
| `POST` | `/api/customers` | Register a new customer |
| `PUT` | `/api/customers/{document}` | Update a customer's name/email/phone |
| `GET` | `/api/customers/{document}/subscriptions` | List a customer's subscriptions |
| `POST` | `/api/customers/{document}/subscriptions` | Create (or reactivate) a subscription |
| `DELETE` | `/api/customers/{document}/subscriptions/{subscription}` | Cancel a subscription |

Full request/response schemas are in Swagger UI once the app is running.

## Events (Kafka)

Subscription lifecycle changes are published as JSON events; topics are auto-created on startup:

| Topic | Published when |
|---|---|
| `subscription.created` | a new subscription is activated |
| `subscription.canceled` | a subscription is canceled |
| `subscription.reactivated` | a previously canceled subscription is reactivated |

`SubscriptionEventListener` consumes these internally to trigger transactional emails via
`EmailService`. **The actual `emailService.send...()` calls are currently commented out** pending Gmail
app-password setup — finish wiring `MAIL_USERNAME`/`MAIL_PASSWORD` and uncomment them once ready.

## Observability

- **Metrics**: Actuator + Micrometer expose `/actuator/prometheus`; Prometheus scrapes it and Grafana
  reads from Prometheus.
- **Logs**: Log4j2 writes to the console and to a rolling JSON file (`logs/provider.log`, Logstash
  layout); Fluent Bit tails that file and ships entries to OpenSearch, browsable in OpenSearch
  Dashboards.

## Tests

```bash
./gradlew test
```

Unit tests cover controllers, security (JWT/auth) and repositories; integration tests
(`src/test/java/integration`) exercise the customers and subscriptions flows end-to-end.
