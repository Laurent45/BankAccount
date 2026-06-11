# Bank Account

A bank account kata built with **hexagonal architecture** (ports & adapters, Alistair Cockburn's original two-zone shape): a single framework-free hexagon containing the domain model and the use cases, with Spring Boot, PostgreSQL and a REST API kept at the edges.

## Architecture

```
 ┌─ infrastructure ─────────────────────────────────────────────────┐
 │                                                                  │
 │  HTTP ──► adapter.in.web          adapter.out.persistence ──► PostgreSQL
 │               │                              │                   │
 └───────────────┼──────────────────────────────┼───────────────────┘
                 │ calls                        │ implements
 ┌─ domain (the hexagon) ───────────────────────┼───────────────────┐
 │               ▼                              ▼                   │
 │   port.in (use cases + commands)        port.out (repositories)  │
 │    ├ account/   ├ operation/  ├ statement/   ▲                   │
 │               │                              │                   │
 │               ▼                              │                   │
 │   package-private services ──────────────────┘                   │
 │               │                                                  │
 │               ▼                                                  │
 │   entities & value objects (account, operation, statement, …)    │
 └──────────────────────────────────────────────────────────────────┘
```

Two Maven modules with a strict dependency direction — `infrastructure → domain`:

| Module | Role | Dependencies |
|---|---|---|
| `domain` | The hexagon: entities (`BankAccount`, `SavingsAccount`), value objects (`Amount`, `Balance`), business rules, use cases and commands (inbound ports), repository interfaces (outbound ports) | none |
| `infrastructure` | The adapters: Spring Boot app, REST adapter (`adapter.in.web`), JPA persistence adapter (`adapter.out.persistence`), wiring | domain |

The domain module has **zero framework dependencies** and is reachable **only through its ports** — and the compiler enforces it. The service implementing each use case is package-private, co-located with its interface in an intent-grouped subpackage (`port.in.account`, `port.in.operation`, `port.in.statement`), and exposed solely through a static factory on the port (`DepositUseCase.create(accountRepository)`). Spring only exists in `infrastructure`, where `UseCaseConfiguration` calls those factories and plugs the adapters into the ports.

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
| POST | `/api/accounts/bank` | Create a bank account |
| POST | `/api/accounts/savings` | Create a savings account `{"depositCeiling": 1000}` |
| GET | `/api/accounts/{id}` | Account information (type-specific fields) |
| POST | `/api/accounts/{id}/deposits` | Deposit `{"amount": 150.50}` |
| POST | `/api/accounts/{id}/withdrawals` | Withdraw `{"amount": 40}` |
| PUT | `/api/accounts/{id}/overdraft-authorization` | Set overdraft limit `{"limit": 200}` (0 removes it) |
| GET | `/api/accounts/{id}/statement` | Statement of the last rolling month |

The API is versioned through the `API-Version` request header (Spring Framework 7's native API versioning). The current version is `1`; requests without the header default to it, and unsupported versions are rejected with `400`.

Errors follow RFC 9457 Problem Details: `404` unknown account, `422` business rule violation (insufficient funds, deposit ceiling, overdraft on savings), `400` invalid input.

A ready-to-use [Bruno](https://www.usebruno.com/) collection lives in [`bruno/`](bruno) — open the folder in Bruno, pick the `local` environment, and run the requests in order (created account ids are chained automatically).
