# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What This Repo Is

A [Giter8](http://www.foundweekends.org/giter8/) (`.g8`) template for generating new Scala/Wabase web application projects. The actual template lives under `src/main/g8/`; everything there is what gets generated for users.

To test the template locally:
```bash
sbt g8Test
```

To apply the template (generates a new project):
```bash
sbt new https://github.com/guntiso/wabase-template.g8.git
```

## Commands (for the generated project under `src/main/g8/`)

```bash
sbt ~reStart       # Run with watch mode (auto-recompile + restart)
sbt test it/test   # Run unit and integration tests
sbt assembly       # Build fat JAR
```

## Template Variables

Defined in `src/main/g8/default.properties`:
- `name` — project name
- `package_name` — Scala package

## Generated Project Architecture

**Framework stack:**
- **Wabase** — web application framework (HTTP routing, CRUD, file handling, auth)
- **Mojoz** — YAML DSL for tables/views/routes → generates Scala/DDL code at compile time via `sbt-mojoz` plugin
- **TresQL** — type-safe SQL query language used in Scala code
- **Pekko** — actor concurrency (HTTP server, deferred jobs, server-sent events)

**Key directories in `src/main/g8/`:**

| Path | Purpose |
|------|---------|
| `tables/*.yaml` | Database table metadata (Mojoz DSL) |
| `views/*.yaml` | View/query definitions (Mojoz DSL) |
| `routes/*.yaml` | HTTP route definitions with OpenAPI annotations |
| `jobs/*.yaml` | Cron job definitions |
| `db/db-schema.sql` | Generated PostgreSQL DDL (commit when regenerated) |
| `src/main/scala/` | Application Scala code |
| `src/it/scala/` | Integration test runner |
| `src/it/resources/business-tests/` | YAML-driven business scenario tests |
| `.env` | Local environment config (DB URL, port 8082, app home) |

**Data flow:** YAML definitions in `tables/`, `views/`, `routes/` → `sbt-mojoz` generates Scala classes and SQL DDL at compile time → `TresQL` executes queries → Wabase handles HTTP/auth/file I/O.

**Built-in features** (pre-wired in the template):
- REST CRUD for `Person` entity as the example
- Audit trail (`audit.yaml` table, request/response logging)
- Deferred async request queue (`deferred.yaml`)
- File upload/download with SHA-256 tracking (`file.yaml`)
- Swagger/OpenAPI auto-generated from route YAML
- Scheduled jobs via Pekko Quartz (`cron_job.yaml`)
- GraalVM JS engine for YAML-defined validation rules
- PDF rendering (Flying Saucer)
- SMTP email (Simple Java Mail)

**Database:** HSQLDB in-memory by default; PostgreSQL via `.env` overrides.

**Main class:** `org.wabase.WabaseServer` (configured in `build.sbt`).

## Scala/Build Details

- Scala 3.8.2 (also supports 2.13.18 via cross-build)
- Java 25 required
- sbt 1.12.8
- Integration tests in separate `it` sbt subproject (`src/it/`)
- Test framework: ScalaTest 3.2.19
