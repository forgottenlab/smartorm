# Compatibility

[English](compatibility.md) · [简体中文](compatibility.zh-CN.md)

> **Summary:** Compatibility claims are limited to exact versions backed by executed checks. This is a verified point, not a promised version range.

## 🧾 Evidence Policy

“Verified” means the exact repository combination completed the stated local checks. It does not imply a supported version range. A declaration in `pom.xml` without an executed test is not enough.

## ✅ Verified Point

| Component | Exact version / environment | Evidence |
|---|---|---|
| Java | release 17; local JDK 17.0.12 | Main/test compilation and focused tests passed |
| Maven | local 3.9.11; no Wrapper | `test-compile`, focused tests, and package lifecycle passed |
| Spring Boot | 3.5.5 | Application context and 46 database tests passed in the current 2026-08-12 preflight and twice on 2026-07-20 |
| MyBatis-Plus | 3.5.14 | Wrapper CRUD/page behavior passed current suites |
| MyBatis-Plus-Join | 1.5.5 | Current combined-artifact JOIN tests passed |
| MySQL Connector/J | 9.4.0 | Used in the current and retained disposable integration runs |
| MySQL Server | `mysql:9.4.0` tag | Current fresh-volume run passed 46/46 and cleaned 0/0/0 residue; two independent 2026-07-20 runs are retained evidence |
| Local artifact set | main/sources/Javadoc/POM | Online `-DskipTests` clean package and artifact inspection passed after separate test validation |
| External consumer smoke | isolated local Maven repository | Two tests passed online and passed again offline |

An additional fixed-seed randomized class/method-order run passed all 46 database tests on 2026-07-20. In the current 2026-08-12 preflight, the manually launched Docker Desktop Linux daemon was healthy; a new `smartorm-release-preflight` environment passed 46/46 and cleanup left zero project containers, networks, and volumes. GitHub Actions run `30647688231` then verified the exact Foundation SHA `c13426d`: remote 28/28, MySQL 46/46, package, report upload, and cleanup all passed.

## 🚫 Not Verified

- Java 8–16, Java 18+, or a Java version range.
- Spring Boot 2.x or any Boot version other than 3.5.5.
- Other MyBatis-Plus or MyBatis-Plus-Join versions.
- PostgreSQL, MariaDB, Oracle, SQL Server, or other MySQL versions.
- Gradle consumer builds.
- Published Maven Central consumption.
- Maven Central signing, upload, repository acceptance, or namespace verification.
- A consumer Spring Boot application using a Starter.
- Bit-for-bit reproducible artifacts or immutable runner/container inputs.

Do not infer support from compilation alone.

## 📦 Current Artifact Limitations

- The repository remains a single Maven module containing library, Spring integration, demo, MPJ, MySQL, and Web source concerns.
- Release artifacts exclude demo classes, `application.yaml`, and demo SQL; the demo remains available in the repository source tree.
- `SmartMapper` exposes `MPJBaseMapper` in its public superinterfaces.
- MyBatis-Plus-Join remains transitive. JSqlParser is optional; Spring Web and MySQL Connector/J are runtime-optional.
- The current runtime requires explicit component and Mapper scanning.
- No Maven Wrapper, Starter, AutoConfiguration, configuration metadata, or maintained in-repository consumer sample exists.
- The local published-like artifact path has passed package/install/consumer checks, but no public repository path has been validated.

## 🔒 Public Behavior Compatibility

Current behavior that should be treated as compatibility-sensitive includes:

- annotation names, properties, defaults, and numeric placeholder syntax;
- `SmartMapper` superinterfaces, convenience methods, and lifecycle hooks;
- entity/Map/DTO/single/page result inference;
- JOIN structure, aliases, inferred/explicit `ON`, and native binding order;
- fail-closed empty-`WHERE` behavior and method-level `allowFullTable` opt-in.

A future module split must not silently move or remove exposed types. Breaking Mapper inheritance or artifact coordinates requires a major-version migration path.

## 🧪 Matrix Expansion Rule

Before adding a version or database to the verified table:

1. build a clean consumer or published-like artifact;
2. run database-independent regressions;
3. start the application context;
4. run the relevant disposable database suite;
5. record exact versions and limitations in CI and this document.
