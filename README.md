# SmartORM

[English](README.md) | [简体中文](README.zh-CN.md)

> SmartORM is an opt-in, annotation-driven enhancement library for MyBatis-Plus applications built with Spring Boot.

SmartORM reduces repetitive Mapper code for fixed-shape CRUD, pagination, result mapping, and JOIN queries while preserving the MyBatis-Plus programming model. Adoption is incremental: existing `BaseMapper`, Wrapper, XML, Provider, and MyBatis-Plus-Join code can remain in place while selected Mappers and methods opt in.

## Project status

SmartORM 2.0.x is currently a source-first release foundation, not a published Spring Boot Starter.

- The repository builds with Java 17 and Spring Boot 3.5.5.
- Database-independent regressions and disposable MySQL 9.4 integration tests are established locally.
- A GitHub Actions workflow exists, but no remote CI run is claimed yet.
- Maven Central publication, consumer-grade auto-configuration, and a multi-module layout are future work.

Use the current repository for evaluation, local source-module integration, and contribution. Do not treat the coordinates in `pom.xml` as proof that a public artifact is available.

## What SmartORM is

SmartORM is a focused enhancement layer for teams that already use Spring Boot and MyBatis-Plus. It adds declarative Mapper annotations and routes them through a structured runtime pipeline:

```text
Mapper method
  -> Aspect
  -> Handler
  -> Resolver
  -> Builder or SQL Renderer
  -> Executor
```

It is a good fit when you want to reduce repeated, fixed-shape Mapper code without replacing the escape hatches already provided by MyBatis and MyBatis-Plus.

## What SmartORM is not

SmartORM is not:

- a replacement ORM;
- a replacement for MyBatis or MyBatis-Plus;
- a replacement for `BaseMapper`, Wrapper, XML, or Provider SQL;
- a replacement for MyBatis-Plus-Join;
- a general-purpose dynamic SQL language;
- a published Spring Boot Starter today.

Keep complex dynamic SQL, stored procedures, database-specific statements, and already-stable XML/Provider code on their existing paths unless migration has a clear maintenance benefit.

## Implemented features

- Annotation-driven select, insert, update, delete, and page operations.
- Entity, `Map`, DTO/VO, single-result, and `PageResult` paths.
- Fixed JOIN declarations with inferred or explicit `ON` predicates.
- Native SQL rendering with ordered scalar parameter binding.
- `SmartMapper` convenience methods and lifecycle hooks.
- Safe-by-default rejection of `@SmartUpdate` and `@SmartDelete` operations with no effective `WHERE` predicate.

`@SmartQuery` exists as an inactive placeholder and is not a supported execution entry point.

## Quick start

### 1. Use the current source module

A public Maven Central artifact has not been verified. For evaluation, open this repository as a Maven project or include the current source as a local module. The current integration requires component and Mapper scanning equivalent to the demo configuration; the [Getting Started guide](docs/getting-started.md) shows the exact setup.

### 2. Define one opt-in Mapper method

```java
public interface UserMapper extends SmartMapper<User> {

    @SmartSelect(
            fields = {"id", "user_name", "status"},
            where = "status = #{0}",
            orderBy = "id"
    )
    List<User> findByStatus(Integer status);
}
```

Unannotated Mapper methods continue to use their normal MyBatis-Plus, XML, Wrapper, or Provider implementation.

### 3. Verify the declaration

Add a deterministic integration test that creates its own fixture and checks rows, ordering, null behavior, and database effects. Numeric placeholders refer to the Java method argument index, starting at `#{0}`.

See [Getting Started](docs/getting-started.md) for the current scan configuration, entity example, test setup, and common errors.

## Installation reality

Current verified usage is source/local-module based. The repository declares:

```text
io.github.forgottenlab:smartorm:2.0.0
```

These coordinates describe the project but are not a claim that Maven Central publication has completed. A future Starter is listed on the roadmap and must not be added to current build files.

## Migration from MyBatis-Plus

Migration is incremental and reversible:

1. keep the existing dependency and Mapper behavior;
2. opt in one Mapper;
3. migrate one well-tested method;
4. replace only repetitive fixed-shape SQL;
5. scale by application module only after comparison and rollback checks.

Existing `BaseMapper`, Wrapper, XML, Provider, and MyBatis-Plus-Join code remains valid. See [Migration from MyBatis-Plus](docs/migration-from-mybatis-plus.md).

## Safety

`@SmartUpdate` and `@SmartDelete` reject execution when the final generated Wrapper has no effective `WHERE` predicate. A deliberate full-table operation requires a method-level declaration:

```java
@SmartDelete(allowFullTable = true)
int deleteAllRows();
```

This opt-in bypasses only the empty-`WHERE` guard. It does not relax any other parsing, SQL, transaction, authorization, or database protection. Review the full [Safety guide](docs/safety.md) before using write annotations.

## Testing evidence

Current local evidence for the exact repository baseline:

- 28 database-independent tests: passed.
- 46 MySQL integration tests against disposable `mysql:9.4.0`: passed twice from independently created volumes.
- A separate fixed-seed randomized class/method-order run: 46 passed.
- Task-owned containers, volumes, and networks were removed after each run.

The workflow in `.github/workflows/test.yml` mirrors this baseline, but it has not been confirmed by a remote GitHub Actions run. See [Testing](docs/testing.md).

## Documentation

- [Getting Started](docs/getting-started.md)
- [Migration from MyBatis-Plus](docs/migration-from-mybatis-plus.md)
- [Safety](docs/safety.md)
- [Testing](docs/testing.md)
- [Architecture](docs/architecture.md)
- [Compatibility](docs/compatibility.md)
- [Roadmap](docs/roadmap.md)
- [Release checklist](docs/release-checklist.md)
- [Changelog](CHANGELOG.md)

Chinese versions are available from the language switch at the top of each public document.

## Compatibility summary

The verified point is Java 17, Spring Boot 3.5.5, MyBatis-Plus 3.5.14, MyBatis-Plus-Join 1.5.5, and MySQL 9.4.0. This is one tested point, not a broad supported range. PostgreSQL, MariaDB, other Java/Spring versions, Gradle consumers, and published-artifact consumption remain unverified.

See the [Compatibility matrix](docs/compatibility.md) for exact evidence and limitations.

## Roadmap boundary

Starter auto-configuration is planned for 2.1.x, API usability work for 2.2.x, and physical module separation for 3.0. These are directions, not delivered features, dates, or implementation authorization. See the [Roadmap](docs/roadmap.md).

## License and release readiness

The POM declares Apache License 2.0. A standalone repository license file and the remaining artifact/release gates must be completed before a public release; track them in the [release checklist](docs/release-checklist.md).
