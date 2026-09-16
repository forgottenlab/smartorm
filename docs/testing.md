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

The current 35-test set directly exercises non-JOIN Wrapper binding, native SQL rendering/binding, and empty-`WHERE` mutation safety without Spring Boot, MySQL, network services, or test order dependencies:

```powershell
mvn -o -pl smartorm '-Dtest=SmartQuerySupportBindingTest,SmartMutationWhereSafetyTest,SmartNativeSqlRendererTest' test
```

Coverage includes bound integer/String/null values, multiple/out-of-order/sparse/repeated placeholders, the non-JOIN Handler boundary, explicit/inferred JOIN predicates, aliases, pagination rendering, invalid indices, empty/disabled mutation predicates, explicit full-table opt-in, and input immutability.

### 🐬 MySQL Integration Tests

Nine Spring Boot test classes currently execute 47 tests for select, result mapping, insert, update, delete, pagination, JOIN, `SmartMapper` helpers, and lifecycle hooks. Every class uses the `test` profile and transaction rollback.

The 2026-09-16 parameter-binding preflight created a new `smartorm-parameter-binding` Compose project on MySQL 9.4.0. All 47 tests passed with no failure, error, or skip; the new test inspected real MyBatis `BoundSql` for existing-ID, missing-ID, and String-valued queries. Mandatory cleanup then left zero project containers, networks, and volumes. The 2026-08-12 preflight and dated 2026-07-20 double-run/random-order probe remain separate historical evidence.

The integration environment is defined by `docker-compose.test.yml`:

- fixed image tag `mysql:9.4.0`;
- project-owned removable volume;
- loopback-only host binding;
- health check before tests;
- `utf8mb4` and explicit timezone/SQL mode;
- authoritative schema mounted read-only into a new container.

### ☁️ CI Verification

The current `.github/workflows/test.yml` selects JDK 17, enables Maven dependency caching, compiles tests, runs the 35 database-independent core tests and 21 Starter context tests, starts the Compose MySQL service, runs the exact 47 database tests, packages without rerunning tests, uploads Surefire reports, and defines an `if: always()` cleanup step.

The parameter-binding merge checkpoint `093318aa46143f052cdf54b3183b0fda80eba4da` was verified by exact `push/main` run `35074543269` (run 7, attempt 1). It completed successfully with the 35 database-independent tests, Starter 21/21, MySQL 47/47, package, Surefire report upload, and always-cleanup. The uploaded Surefire artifact is `10437496726` (49,998 bytes); cleanup logs show the dedicated MySQL container, volume, and network were each removed. The workflow does not perform a separate numeric post-cleanup `0/0/0` enumeration. Earlier runs `31574822720` and `32653878401` remain historical evidence for the foundation and Starter feature checkpoints. Runner OS/tool versions, action major tags, the MySQL image tag, and Docker Compose are not pinned to immutable digests as a group, so this is not a bit-for-bit reproducibility claim. The README badge shows live workflow status and does not replace exact-SHA evidence.

### 🧩 Starter Tests

The focused auto-configuration suite is database-independent:

```powershell
mvn -o -pl smartorm-spring-boot-starter -am '-Dtest=SmartOrmAutoConfigurationTest' '-Dsurefire.failIfNoSpecifiedTests=false' test
```

The current suite passes 21/21 and covers imports discovery, exact internal Mapper metadata, the eight-bean runtime graph, MyBatis Boot default discovery, application-only `@MapperScan`, explicit/legacy compatibility, unique/primary/ambiguous sessions, missing-class/infrastructure and user-bean backoff, infrastructure non-ownership, and zero database interaction during bootstrap.

An external Boot 3.5.5 consumer was generated under ignored `target/` state and resolved only the locally installed `smartorm-spring-boot-starter:2.1.0-SNAPSHOT`. Its single context test passed online and then offline. It used MyBatis Boot default discovery for its own Mapper, contained no SmartORM internal-package reference or manual Native Mapper definition, and verified zero database connections; it is not a maintained sample project.

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
| parameter-binding preflight, 2026-09-16 | 47 | 47 passed, 0 failures/errors/skips | 0 project containers/volumes/networks |
| release preflight, 2026-08-12 | 46 | 46 passed, 0 failures/errors/skips | 0 project containers/volumes/networks |
| `smartorm-it-run1` | 46 | 46 passed, 0 failures/errors/skips | 0 project containers/volumes/networks |
| `smartorm-it-run2` | 46 | 46 passed, 0 failures/errors/skips | 0 project containers/volumes/networks |
| randomized order, seed `20260720` | 46 | 46 passed, 0 failures/errors/skips | 0 project containers/volumes/networks |

The first row is current-preflight evidence. The 2026-08-12 row and three 2026-07-20 rows are retained historical evidence. Together they verify only the tested MySQL 9.4.0 point, not other database/framework versions.

## 📦 Artifact Preflight

After test compilation, the 35 database-independent core regressions, 47 MySQL tests, and 21 Starter tests passed, the current reactor completed an offline package with tests intentionally skipped:

With the dedicated MySQL environment healthy, local `mvn clean test` completed 82/82 core tests (35 database-independent plus 47 database-backed) and 21/21 Starter tests, with no failures, errors, or skips. That checkpoint workflow independently verified the same 35 database-independent, 47 MySQL, and 21 Starter counts at SHA `093318aa`.

```powershell
mvn -o -DskipTests package
```

The following outputs were generated and inspected:

- an ordinary core library main JAR, not a Spring Boot executable JAR;
- core sources and Javadoc JARs;
- an ordinary Starter JAR with exactly one `AutoConfiguration.imports` entry;
- the POM stored in the isolated Maven repository layout;
- artifact contents excluding demo classes, `application.yaml`, and demo SQL while retaining the repository demo sources;
- the repository `LICENSE` and packaged license metadata.

The earlier core package validation used an isolated Maven cache and its external consumer passed two tests plus an offline repeat. The current Starter reactor was also installed into a separate ignored `maven.repo.local`; its external consumer passed one test plus an offline repeat. These checks validate local published-like artifact paths only; Maven Central has not been configured, uploaded to, or consumed from.

This result does not establish bit-for-bit reproducibility, signing, Maven Central acceptance, or stable-release status. Remote exact-main CI is verified separately above.

## 🛡️ Safety Rules

- Never run `init-smartorm-demo.sql` against an existing/user database; it contains destructive DDL.
- Never mount host MySQL data directories into the test service.
- Always use a unique Compose project and `down -v --remove-orphans`.
- Do not persist test passwords or datasource URLs in logs or local task records.
- Do not report integration tests as passed unless their assertions executed against a healthy initialized container.
