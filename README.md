# FableFit Backend

FableFit is the backend foundation for a clothing e-commerce application. It starts with three deliberately small, production-shaped services:

- gateway is the single HTTP entry point and routes client requests.
- identity owns users, credentials, roles, authentication, and authorization.
- commerce owns the store domain: catalog, variants, inventory, carts, orders, and checkout orchestration.

This repository follows the Nodified Gradle multi-project layout.

## Architecture

    Storefront / Admin / API client
                 |
                 v
          gateway :8080
             |       |
             v       v
    identity :8081  commerce :8082
             |       |
             +--- PostgreSQL: fablefit ---+
                  identity   commerce

Only the gateway should be exposed publicly in a deployed environment. Identity and commerce remain independent executable applications, but clients normally access them through the gateway.

## Services

| Service | Gradle module | Responsibility | Port | Database ownership |
|---|---|---|---:|---|
| gateway | :services:apps:gateway | Edge routing; future home for CORS, rate limits, and edge authentication. | 8080 | Stateless |
| identity | :services:apps:identity | Users, passwords, roles, JWT issuance, and authorization. | 8081 | identity schema |
| commerce | :services:apps:commerce | Products, variants, stock, carts, orders, and checkout orchestration. | 8082 | commerce schema |

## Shared database, separate schemas

Identity and commerce connect to the same PostgreSQL database, named fablefit. They have separate schema ownership and must never read or write one another's tables.

    PostgreSQL database: fablefit
    ├── identity       owned only by identity
    └── commerce       owned only by commerce

Each service has its own database variables and a Hibernate default schema. This creates a clean service boundary now, while keeping local setup simple. You can later move a schema into an independent database without changing its domain ownership.

## Prerequisites

- Java 21 or newer
- PostgreSQL 16 or newer
- No global Gradle installation is required; use the included Gradle wrapper.

## Database setup

Create a local database and service account once. The following commands assume PostgreSQL command-line access:

    CREATE ROLE fablefit LOGIN PASSWORD 'change-me';
    CREATE DATABASE fablefit OWNER fablefit;
    \c fablefit
    CREATE SCHEMA IF NOT EXISTS identity AUTHORIZATION fablefit;
    CREATE SCHEMA IF NOT EXISTS commerce AUTHORIZATION fablefit;

If you use an existing PostgreSQL user, change the connection values in .env instead. The database user needs permission to create and alter tables in both schemas during local development.

## Environment files

Two environment files are provided:

| File | Purpose |
|---|---|
| .env.example | Safe template committed to Git. |
| .env | Local values, ignored by Git. Never commit real secrets. |

A local .env has already been created with placeholder credentials. Replace both database passwords before connecting if your local PostgreSQL user uses a different password.

### Variables

| Variable | Service | Purpose |
|---|---|---|
| SPRING_PROFILES_ACTIVE | all | Spring profile: local or prod. |
| APP_VERSION | all | Version shown in application metadata. |
| GATEWAY_PORT | gateway | Gateway HTTP port. |
| IDENTITY_SERVICE_URL | gateway | Internal URL to identity. |
| COMMERCE_SERVICE_URL | gateway | Internal URL to commerce. |
| IDENTITY_DB_URL, IDENTITY_DB_USERNAME, IDENTITY_DB_PASSWORD | identity | Database connection for identity. |
| IDENTITY_DB_SCHEMA | identity | Schema identity owns. |
| IDENTITY_DDL_AUTO | identity | Hibernate schema behavior. Use update locally. |
| COMMERCE_DB_URL, COMMERCE_DB_USERNAME, COMMERCE_DB_PASSWORD | commerce | Database connection for commerce. |
| COMMERCE_DB_SCHEMA | commerce | Schema commerce owns. |
| COMMERCE_DDL_AUTO | commerce | Hibernate schema behavior. Use update locally. |
| GATEWAY_JWT_ISSUER_URI, IDENTITY_JWT_ISSUER_URI, COMMERCE_JWT_ISSUER_URI | all | Planned identity JWT issuer URL. |

The .env file is not automatically loaded by Gradle. Before starting an app, load it in that terminal:

    set -a
    source .env
    set +a

In a Windows PowerShell terminal, define the same variables in the session or configure them in the IDE run configuration.

## Run locally

Use three terminals from the repository root.

### 1. Start identity

    set -a && source .env && set +a
    ./gradlew :services:apps:identity:bootRun

Identity starts on http://localhost:8081.

### 2. Start commerce

    set -a && source .env && set +a
    ./gradlew :services:apps:commerce:bootRun

Commerce starts on http://localhost:8082.

### 3. Start gateway

    set -a && source .env && set +a
    ./gradlew :services:apps:gateway:bootRun

Gateway starts on http://localhost:8080.

Gateway routes:

| Public path | Destination |
|---|---|
| /api/identity/** | identity service; the /api/identity prefix is removed |
| /api/commerce/** | commerce service; the /api/commerce prefix is removed |

For example, when identity later exposes GET /auth/me, clients will call GET /api/identity/auth/me through the gateway.

## Build and test

Build and test the complete workspace:

    ./gradlew test
    ./gradlew bootJar

The generated executables are:

    services/apps/gateway/build/libs/gateway.jar
    services/apps/identity/build/libs/identity.jar
    services/apps/commerce/build/libs/commerce.jar

## Health checks and API documentation

Spring Boot Actuator and Swagger/OpenAPI are included in every application.

| Service | Health endpoint | Swagger UI |
|---|---|---|
| gateway | http://localhost:8080/actuator/health | http://localhost:8080/swagger-ui.html |
| identity | http://localhost:8081/actuator/health | http://localhost:8081/swagger-ui.html |
| commerce | http://localhost:8082/actuator/health | http://localhost:8082/swagger-ui.html |

Swagger will show useful operations as controllers are added. The identity service will become the actual JWT issuer; until then, the issuer variables document the intended security boundary.

## Spring configuration

Every app follows the Nodified three-file profile pattern:

    application.yml        shared configuration and local profile default
    application-local.yml  local defaults
    application-prod.yml   production settings supplied through environment variables

Use SPRING_PROFILES_ACTIVE=prod only after supplying all required database and issuer variables in the deployment environment.

For local development, Hibernate uses update to evolve tables. Before production, replace it with versioned migrations and set the DDL strategy to validate.

## Repository layout

    fablefit/
    ├── gradle/
    │   └── libs.versions.toml
    ├── services/
    │   └── apps/
    │       ├── gateway/
    │       ├── identity/
    │       └── commerce/
    ├── .env.example
    ├── build.gradle
    ├── settings.gradle
    └── gradlew

## Development rules

1. Keep identity tables, repositories, and business logic in the identity app and schema.
2. Keep catalog, inventory, cart, and order functionality in the commerce app and schema.
3. Do not query another service's schema directly.
4. Use HTTP APIs for genuine cross-service operations.
5. Never commit passwords, JWT signing keys, payment keys, or credentials embedded in URLs.

