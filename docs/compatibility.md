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
| Spring Boot | 3.5.5 | Core application context/database suites passed; Starter `ApplicationContextRunner` tests passed 21/21 without a database |
| MyBatis-Plus | 3.5.14 | Wrapper CRUD/page behavior passed current suites |
| MyBatis-Plus-Join | 1.5.5 | Current combined-artifact JOIN tests passed |
| MySQL Connector/J | 9.4.0 | Used in the current and retained disposable integration runs |
| MySQL Server | `mysql:9.4.0` tag | Current fresh-volume run passed 47/47 with real binding assertions and cleaned 0/0/0 residue; earlier runs are retained evidence |
| Core artifact set | main/sources/Javadoc/POM | Reactor package and artifact inspection passed after separate test validation |
| Starter artifact | `smartorm-spring-boot-starter:2.1.0-SNAPSHOT` | Ordinary JAR, imports metadata, dependencies, and contents inspected locally |
| Core external consumer | isolated local Maven repository | Two tests passed online and passed again offline |
| Starter external consumer | Boot 3.5.5, isolated local Maven repository | Real `@EnableAutoConfiguration` plus default application Mapper discovery passed 1/1 online and 1/1 offline without a database connection or internal-package reference |

On 2026-09-16, a new `smartorm-parameter-binding` environment passed 47/47 and cleanup left zero project containers, networks, and volumes. The parameter-binding fix was then merged by PR #2 into exact main SHA `093318aa46143f052cdf54b3183b0fda80eba4da`. GitHub Actions `push/main` run `35074543269` completed successfully with 35 database-independent tests, Starter 21/21, MySQL 47/47, package, Surefire upload, and dedicated Compose resource removal. Earlier runs `31574822720` and `32653878401` remain historical evidence for prior foundation and Starter checkpoints.

## 🚫 Not Verified

- Java 8–16, Java 18+, or a Java version range.
- Spring Boot 2.x or any Boot version other than 3.5.5.
- Other MyBatis-Plus or MyBatis-Plus-Join versions.
- PostgreSQL, MariaDB, Oracle, SQL Server, or other MySQL versions.
- Gradle consumer builds.
- Maven Central signing, upload, repository acceptance, or namespace verification.
- Maven Central consumption of either the core or Starter artifact.
- Adoption in an independent real application or Lingxi module, including real database execution through the Starter.
- Bit-for-bit reproducible artifacts or immutable runner/container inputs.

Do not infer support from compilation alone.

## 📦 Current Artifact Limitations

- The repository is a parent reactor with compatible `smartorm` and combined `smartorm-spring-boot-starter` children; the core child still combines library, Spring integration, demo, MPJ, MySQL, and Web source concerns.
- Core release artifacts exclude demo classes, `application.yaml`, and demo SQL; the demo remains available in the core source tree.
- `SmartMapper` exposes `MPJBaseMapper` in its public superinterfaces.
- MyBatis-Plus-Join remains transitive. JSqlParser is optional; Spring Web and MySQL Connector/J are runtime-optional.
- Direct core use requires explicit component and internal Mapper scanning. Starter use requires neither: the Starter precisely registers its one internal Mapper while applications scan only their own business Mapper packages.
- The Starter does not own application Mapper scanning, `DataSource`, `SqlSessionFactory`, `SqlSessionTemplate`, transaction management, or pagination, and it does not access the database during its own bootstrap. Existing explicit/legacy Native Mapper registration is reused; missing or ambiguous session candidates back off conservatively.
- No Maven Wrapper, configuration metadata, Validator, Doctor, FailureAnalyzer, or maintained in-repository consumer sample exists.
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
