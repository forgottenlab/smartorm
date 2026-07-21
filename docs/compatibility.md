# Compatibility

[English](compatibility.md) | [简体中文](compatibility.zh-CN.md)

## Evidence policy

“Verified” means the exact repository combination completed the stated local checks. It does not imply a supported version range. A declaration in `pom.xml` without an executed test is not enough.

## Verified point

| Component | Exact version / environment | Evidence |
|---|---|---|
| Java | release 17; local JDK 17.0.12 | Main/test compilation and focused tests passed |
| Maven | local 3.9.11; no Wrapper | `test-compile` and focused test lifecycle passed |
| Spring Boot | 3.5.5 | Application context and 46 database tests passed twice |
| MyBatis-Plus | 3.5.14 | Wrapper CRUD/page behavior passed current suites |
| MyBatis-Plus-Join | 1.5.5 | Current combined-artifact JOIN tests passed |
| MySQL Connector/J | 9.4.0 | Used in both disposable integration runs |
| MySQL Server | `mysql:9.4.0` | 46 tests passed twice on independent fresh volumes |

An additional fixed-seed randomized class/method-order run also passed all 46 database tests.

## Not verified

- Java 8–16, Java 18+, or a Java version range.
- Spring Boot 2.x or any Boot version other than 3.5.5.
- Other MyBatis-Plus or MyBatis-Plus-Join versions.
- PostgreSQL, MariaDB, Oracle, SQL Server, or other MySQL versions.
- Gradle consumer builds.
- Published Maven Central consumption.
- A consumer Spring Boot application using a Starter.
- Remote GitHub Actions execution.

Do not infer support from compilation alone.

## Current artifact limitations

- The project is a single artifact that includes library, Spring integration, demo, MPJ, MySQL, and Web concerns.
- `SmartMapper` exposes `MPJBaseMapper` in its public superinterfaces.
- The current runtime requires explicit component and Mapper scanning.
- No Maven Wrapper, Starter, AutoConfiguration, configuration metadata, or consumer sample exists.
- Offline packaging is blocked locally because `maven-jar-plugin:3.4.2` is absent from the cache.

## Public behavior compatibility

Current behavior that should be treated as compatibility-sensitive includes:

- annotation names, properties, defaults, and numeric placeholder syntax;
- `SmartMapper` superinterfaces, convenience methods, and lifecycle hooks;
- entity/Map/DTO/single/page result inference;
- JOIN structure, aliases, inferred/explicit `ON`, and native binding order;
- fail-closed empty-`WHERE` behavior and method-level `allowFullTable` opt-in.

A future module split must not silently move or remove exposed types. Breaking Mapper inheritance or artifact coordinates requires a major-version migration path.

## Matrix expansion rule

Before adding a version or database to the verified table:

1. build a clean consumer or published-like artifact;
2. run database-independent regressions;
3. start the application context;
4. run the relevant disposable database suite;
5. record exact versions and limitations in CI and this document.
