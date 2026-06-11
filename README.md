# Bank Account

A bank account kata built with **hexagonal architecture** (ports & adapters): a framework-free domain and application core, with Spring Boot, PostgreSQL and a REST API kept at the edges.

## Architecture

```
                        ┌─────────────────────────────────────────────┐
                        │               infrastructure                │
                        │                                             │
 HTTP ──► adapter.in.web ──► application.port.in ◄── implements ──┐   │
                        │         (use cases)                     │   │
                        │             │                  ┌────────┴─┐ │
                        │             ▼                  │ services │ │
                        │      domain (entities,         └────────┬─┘ │
                        │       value objects)                    │   │
                        │             ▲                           │   │
                        │   domain.port.out ◄── calls ────────────┘   │
                        │             ▲                               │
                        │   adapter.out.persistence ──► PostgreSQL    │
                        └─────────────────────────────────────────────┘
```

Three Maven modules with a strict dependency direction — `infrastructure → application → domain`:

| Module | Role | Dependencies |
|---|---|---|
| `domain` | Entities (`BankAccount`, `SavingsAccount`), value objects (`Amount`, `Balance`), business rules, outbound ports | none |
| `application` | Use cases (inbound ports), services orchestrating the domain, commands | domain |
| `infrastructure` | Spring Boot app: REST adapter (`adapter.in.web`), JPA persistence adapter (`adapter.out.persistence`), wiring | application |

The domain and application modules have **zero framework dependencies**. Spring only exists in `infrastructure`, where `UseCaseConfiguration` instantiates the services and plugs the adapters into the ports.

## Business rules

- **Bank account**: deposits and withdrawals; withdrawals limited to balance + overdraft authorization (none by default, updatable through the API).
- **Savings account**: deposits capped by a deposit ceiling; no overdraft.
- **Statement**: current balance plus the operations of the last rolling month, most recent first.
- Every deposit/withdrawal stores an operation **atomically** with the balance update.

## Tech stack

Java 25 · Spring Boot 4 (Web MVC, Data JPA) · PostgreSQL 18 · Maven multi-module · JUnit + AssertJ · Testcontainers

## Getting started

Prerequisites: JDK 25, Maven, Docker.

```bash
# 1. Start PostgreSQL
docker compose up -d

# 2. Run the application (http://localhost:8080)
mvn spring-boot:run -pl infrastructure

# 3. Run all tests (integration tests start their own PostgreSQL via Testcontainers)
mvn test
```

The schema is owned by `infrastructure/src/main/resources/database/schema.sql` (Hibernate runs in `validate` mode only).

## API

| Method | Path | Description |
|---|---|---|
| POST | `/accounts/bank` | Create a bank account |
| POST | `/accounts/savings` | Create a savings account `{"depositCeiling": 1000}` |
| GET | `/accounts/{id}` | Account information (type-specific fields) |
| POST | `/accounts/{id}/deposits` | Deposit `{"amount": 150.50}` |
| POST | `/accounts/{id}/withdrawals` | Withdraw `{"amount": 40}` |
| PUT | `/accounts/{id}/overdraft-authorization` | Set overdraft limit `{"limit": 200}` (0 removes it) |
| GET | `/accounts/{id}/statement` | Statement of the last rolling month |

Errors follow RFC 9457 Problem Details: `404` unknown account, `422` business rule violation (insufficient funds, deposit ceiling, overdraft on savings), `400` invalid input.

A ready-to-use [Bruno](https://www.usebruno.com/) collection lives in [`bruno/`](bruno) — open the folder in Bruno, pick the `local` environment, and run the requests in order (created account ids are chained automatically).
