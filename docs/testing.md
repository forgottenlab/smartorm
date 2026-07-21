# Testing

[English](testing.md) | [简体中文](testing.zh-CN.md)

SmartORM uses layered evidence. A compile result, a database-independent renderer result, a MySQL integration result, and a CI result prove different things and must not be reported interchangeably.

## Test layers

```text
Unit and compile checks
  -> Database-independent regression tests
  -> MySQL integration tests
  -> CI verification
```

### Unit and compile checks

`test-compile` verifies that main and test sources compile with the current Java/Maven dependency cache. It does not prove database behavior.

```powershell
mvn -o test-compile
```

### Database-independent regression tests

The current 28-test set directly exercises native SQL rendering/binding and empty-`WHERE` mutation safety without Spring Boot, MySQL, network services, or test order dependencies:

```powershell
mvn -o '-Dtest=SmartMutationWhereSafetyTest,SmartNativeSqlRendererTest' test
```

Coverage includes sequential/out-of-order/sparse/repeated placeholders, explicit/inferred JOIN predicates, aliases, pagination rendering, invalid indices, empty/disabled mutation predicates, explicit full-table opt-in, and input immutability.

### MySQL integration tests

Nine Spring Boot test classes currently execute 46 tests for select, result mapping, insert, update, delete, pagination, JOIN, `SmartMapper` helpers, and lifecycle hooks. Every class uses the `test` profile and transaction rollback.

The integration environment is defined by `docker-compose.test.yml`:

- exact image `mysql:9.4.0`;
- project-owned removable volume;
- loopback-only host binding;
- health check before tests;
- `utf8mb4` and explicit timezone/SQL mode;
- authoritative schema mounted read-only into a new container.

### CI verification

`.github/workflows/test.yml` fixes JDK 17, enables Maven caching, compiles tests, runs the 28 database-independent tests, starts the Compose MySQL service, runs the exact 46 database tests, packages without rerunning tests, uploads Surefire reports, and always removes the database state.

The workflow exists in the working tree but has not been pushed or observed running on GitHub. Do not display a passing CI badge until a real run succeeds.

## Requirements

- JDK 17.
- Maven 3.9.x for the locally observed baseline; no Maven Wrapper exists.
- Docker with Compose support and a running Linux container daemon for integration tests.
- Host port `13316`, or another loopback port through `SMARTORM_TEST_DB_PORT`.

## Test-only environment variables

Use process-local values. Never reuse development or production database credentials:

```powershell
$env:SMARTORM_TEST_ROOT_PASSWORD = 'REDACTED'
$env:SMARTORM_TEST_DB_USERNAME = 'REDACTED'
$env:SMARTORM_TEST_DB_PASSWORD = 'REDACTED'
$env:SMARTORM_TEST_DB_PORT = '13316'
```

The Spring profile reads only these test variables. It does not fall back to the main datasource credentials.

## Run one clean MySQL suite

Use a unique project name and always remove its volume:

```powershell
docker compose -p smartorm-it-run1 -f docker-compose.test.yml config --quiet
docker compose -p smartorm-it-run1 -f docker-compose.test.yml up -d --wait
mvn -o '-Dtest=SmartMapperHookTest,SmartMapperCoreTest,MapperUpdateTest,MapperSelectTest,MapperSelectResultTypeTest,MapperPageTest,MapperJoinTest,MapperInsertTest,MapperDeleteTest' test
docker compose -p smartorm-it-run1 -f docker-compose.test.yml down -v --remove-orphans
```

After `down -v`, verify that no container, volume, or network remains for the selected Compose project.

## Reproducibility check

Repeat the complete sequence with a different project name such as `smartorm-it-run2`. A different project prefix creates a different volume and avoids first-run state reuse.

The verified local evidence from 2026-07-20 is:

| Run | Tests | Result | Cleanup |
|---|---:|---|---|
| `smartorm-it-run1` | 46 | 46 passed, 0 failures/errors/skips | 0 project containers/volumes/networks |
| `smartorm-it-run2` | 46 | 46 passed, 0 failures/errors/skips | 0 project containers/volumes/networks |
| randomized order, seed `20260720` | 46 | 46 passed, 0 failures/errors/skips | 0 project containers/volumes/networks |

This verifies the exact current MySQL 9.4.0 point; it is not evidence for other database or framework versions.

## Packaging status

The last local offline command:

```powershell
mvn -o -DskipTests package
```

was blocked because `org.apache.maven.plugins:maven-jar-plugin:3.4.2` was absent from the local Maven cache. No dependency/plugin version or repository was changed. CI is designed to run packaging with normal repository access, but that path remains unverified until CI executes.

## Safety rules

- Never run `init-smartorm-demo.sql` against an existing/user database; it contains destructive DDL.
- Never mount host MySQL data directories into the test service.
- Always use a unique Compose project and `down -v --remove-orphans`.
- Do not persist test passwords or datasource URLs in logs or `.ai` history.
- Do not report integration tests as passed unless their assertions executed against a healthy initialized container.
