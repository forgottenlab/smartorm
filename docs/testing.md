# Testing

[English](testing.md) · [简体中文](testing.zh-CN.md)

> **Summary:** SmartORM uses layered evidence. Compile, database-independent, MySQL, package, and remote CI results prove different boundaries and must not be reported interchangeably.

## 🧭 Test Layers

```text
Unit and compile checks
  -> Database-independent regression tests
  -> MySQL integration tests
  -> CI verification
```

### ✅ Unit and Compile Checks

`test-compile` verifies that main and test sources compile with the current Java/Maven dependency cache. It does not prove database behavior.

```powershell
mvn -o test-compile
```

### 🧪 Database-Independent Regression Tests

The current 28-test set directly exercises native SQL rendering/binding and empty-`WHERE` mutation safety without Spring Boot, MySQL, network services, or test order dependencies:

```powershell
mvn -o -pl smartorm '-Dtest=SmartMutationWhereSafetyTest,SmartNativeSqlRendererTest' test
```

Coverage includes sequential/out-of-order/sparse/repeated placeholders, explicit/inferred JOIN predicates, aliases, pagination rendering, invalid indices, empty/disabled mutation predicates, explicit full-table opt-in, and input immutability.

### 🐬 MySQL Integration Tests

Nine Spring Boot test classes currently execute 46 tests for select, result mapping, insert, update, delete, pagination, JOIN, `SmartMapper` helpers, and lifecycle hooks. Every class uses the `test` profile and transaction rollback.

The current 2026-08-12 preflight used the user-started Docker Desktop Linux daemon and a new `smartorm-release-preflight` Compose project. All 46 tests passed with no failure, error, or skip. Mandatory cleanup then left zero project containers, networks, and volumes. The dated 2026-07-20 double-run and random-order probe remain separate historical evidence.

The integration environment is defined by `docker-compose.test.yml`:

- fixed image tag `mysql:9.4.0`;
- project-owned removable volume;
- loopback-only host binding;
- health check before tests;
- `utf8mb4` and explicit timezone/SQL mode;
- authoritative schema mounted read-only into a new container.

### ☁️ CI Verification

`.github/workflows/test.yml` selects JDK 17, enables Maven dependency caching, compiles tests, runs the 28 database-independent tests, starts the Compose MySQL service, runs the exact 46 database tests, packages without rerunning tests, uploads Surefire reports, and defines an `if: always()` cleanup step.

The exact `push/main` run for Foundation SHA `c13426d` was inspected read-only as run `30647688231`. It passed the 28 database-independent tests, 46 MySQL tests, package, Surefire upload, and always-cleanup steps. This is remote evidence for that SHA only; the new local checkpoint HEAD requires a separate run after push. Runner OS/tool versions, action major tags, the MySQL image tag, and Docker Compose are not pinned to immutable digests as a group, so this is not a bit-for-bit reproducibility claim. The README badge shows live workflow status and does not replace exact-SHA evidence.

## ✅ Requirements

- JDK 17.
- Maven 3.9.x for the locally observed baseline; no Maven Wrapper exists.
- Docker with Compose support and a running Linux container daemon for integration tests.
- Host port `13316`, or another loopback port through `SMARTORM_TEST_DB_PORT`.

## 🔐 Test-Only Environment Variables

Use process-local values. Never reuse development or production database credentials:

```powershell
$env:SMARTORM_TEST_ROOT_PASSWORD = 'REDACTED'
$env:SMARTORM_TEST_DB_USERNAME = 'REDACTED'
$env:SMARTORM_TEST_DB_PASSWORD = 'REDACTED'
$env:SMARTORM_TEST_DB_PORT = '13316'
```

The Spring datasource reads the test username/password and optional host/port variables. The root password is used only by Compose to initialize and health-check the disposable service. Neither path falls back to the main datasource credentials.

## 🐬 Run One Clean MySQL Suite

Use a unique project name and always remove its volume:

```powershell
docker compose -p smartorm-it-run1 -f docker-compose.test.yml config --quiet
docker compose -p smartorm-it-run1 -f docker-compose.test.yml up -d --wait
mvn -o -pl smartorm '-Dtest=SmartMapperHookTest,SmartMapperCoreTest,MapperUpdateTest,MapperSelectTest,MapperSelectResultTypeTest,MapperPageTest,MapperJoinTest,MapperInsertTest,MapperDeleteTest' test
docker compose -p smartorm-it-run1 -f docker-compose.test.yml down -v --remove-orphans
```

After `down -v`, verify that no container, volume, or network remains for the selected Compose project.

## 🔁 Reproducibility Check

Repeat the complete sequence with a different project name such as `smartorm-it-run2`. A different project prefix creates a different volume and avoids first-run state reuse.

The retained local evidence from 2026-07-20 is:

| Run | Tests | Result | Cleanup |
|---|---:|---|---|
| current release preflight, 2026-08-12 | 46 | 46 passed, 0 failures/errors/skips | 0 project containers/volumes/networks |
| `smartorm-it-run1` | 46 | 46 passed, 0 failures/errors/skips | 0 project containers/volumes/networks |
| `smartorm-it-run2` | 46 | 46 passed, 0 failures/errors/skips | 0 project containers/volumes/networks |
| randomized order, seed `20260720` | 46 | 46 passed, 0 failures/errors/skips | 0 project containers/volumes/networks |

The first row is current-preflight evidence. The three 2026-07-20 rows are retained historical evidence. Together they verify only the tested MySQL 9.4.0 point, not other database/framework versions.

## 📦 Artifact Preflight

After test compilation and the focused 28-test suite passed separately, the current preflight completed an online clean package with tests intentionally skipped:

```powershell
mvn -DskipTests clean package
```

The following outputs were generated and inspected:

- an ordinary library main JAR, not a Spring Boot executable JAR;
- sources and Javadoc JARs;
- the POM stored in the isolated Maven repository layout;
- artifact contents excluding demo classes, `application.yaml`, and demo SQL while retaining the repository demo sources;
- the repository `LICENSE` and packaged license metadata.

The package validation used an isolated Maven cache, and the install used a newly created isolated `maven.repo.local`. A separate external consumer then passed two tests against the installed artifact, and the same consumer tests passed again offline. This validates the local published-like artifact path only; Maven Central has not been configured, uploaded to, or consumed from.

This result does not establish bit-for-bit reproducibility, signing, remote repository acceptance, or remote CI success.

## 🛡️ Safety Rules

- Never run `init-smartorm-demo.sql` against an existing/user database; it contains destructive DDL.
- Never mount host MySQL data directories into the test service.
- Always use a unique Compose project and `down -v --remove-orphans`.
- Do not persist test passwords or datasource URLs in logs or local task records.
- Do not report integration tests as passed unless their assertions executed against a healthy initialized container.
