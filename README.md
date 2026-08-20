# provider

Java + Spring Boot API skeleton, generated without a CRUD example (just the package structure).

## Stack

| Item | Version / detail |
|---|---|
| Java | 21 (LTS) — Spring Boot 4.x requires Java 17+ |
| Spring Boot | 4.1.0 |
| Build | Gradle 9.7.0 (via wrapper, `./gradlew`) |
| Web | `spring-boot-starter-webmvc` (renamed from the old `spring-boot-starter-web` in Boot 4) |
| Persistence | `spring-boot-starter-data-jpa` + MySQL driver (`com.mysql:mysql-connector-j`) |
| Messaging | `spring-boot-starter-kafka` (Spring for Apache Kafka) |
| Lombok | `org.projectlombok:lombok` |
| Docs | `springdoc-openapi-starter-webmvc-ui` (Swagger UI + OpenAPI 3, works with no extra config) |
| Dev | Docker Compose Support (`spring-boot-docker-compose`, `developmentOnly` scope) |

> Spring Boot 4 renamed several starters (e.g. `spring-boot-starter-web` → `spring-boot-starter-webmvc`,
> `spring-boot-starter-kafka` as a dedicated starter). I used the current Boot 4.1 names; if you
> ever migrate to Boot 3.x, swap `spring-boot-starter-webmvc` back for `spring-boot-starter-web`.

## Package structure

```
br.com.jhonatan.provider
├── config       # @Configuration (beans, CORS, security, etc.)
├── controller   # @RestController
├── service      # business logic
├── repository   # Spring Data JPA repositories
├── model        # JPA entities
├── dto          # transfer objects (request/response)
├── exception    # custom exceptions + @ControllerAdvice
└── kafka
    ├── producer
    ├── consumer
    └── config   # @Configuration specific to topics/serializers
```

`config`, `exception` and the `kafka` sub-packages are still empty (just `.gitkeep`). `model`, `dto`,
`repository`, `service` and `controller` already have the skeleton for the customers/subscriptions
endpoints — the service implementations just throw `UnsupportedOperationException("TODO: ...")` for now,
which is where you plug in the real logic.

## Running locally (without Docker for now)

⚠️ While Docker isn't installed, `application.yml` has:

- `spring.docker.compose.enabled: false` — prevents Spring from trying to run `docker compose up`
  on its own (which would fail without Docker installed).
- The datasource pointing at **in-memory H2** instead of MySQL, just so the app can start without
  needing a real database.

So:

```bash
./gradlew bootRun
```

...should start without errors (Kafka doesn't block startup since there's no `@KafkaListener`
or topic configured yet — the connection is only attempted once you actually publish/consume something).

### Once Docker is installed

In `application.yml`:

1. Delete (or set to `true`) `spring.docker.compose.enabled: false`.
2. Comment out the H2 datasource block and uncomment the MySQL block right below it.
3. Run `./gradlew bootRun` again — Spring Boot will bring up MySQL and Kafka from
   `compose.yaml` automatically.

- MySQL has **automatic service connection**: Spring Boot discovers the container's host/port/credentials
  and configures the `DataSource` on its own.
- Kafka doesn't have that native auto-connection yet, so the `bootstrap-servers` in
  `application.yml` (`localhost:9092`) is already aligned with the port exposed in `compose.yaml`.

## API docs (Swagger / OpenAPI)

With the app running:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Tests

```bash
./gradlew test
```

## Renaming the project

If you want to rename it again:

1. `settings.gradle` → `rootProject.name`
2. `build.gradle` → `group`
3. Package `br.com.jhonatan.provider` (rename the folder and the `package` declaration in every file,
   including `ProviderApplication.java` and `ProviderApplicationTests.java`)
