<div align="center">

# SmartORM

**Opt-in, annotation-driven enhancements for Spring Boot and MyBatis-Plus.**

[English](README.md) · [简体中文](README.zh-CN.md)

[![Test](https://github.com/forgottenlab/smartorm/actions/workflows/test.yml/badge.svg?branch=main)](https://github.com/forgottenlab/smartorm/actions/workflows/test.yml)
![Java 17](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot 3.5.5](https://img.shields.io/badge/Spring%20Boot-3.5.5-6DB33F?logo=springboot&logoColor=white)
![MyBatis--Plus 3.5.14](https://img.shields.io/badge/MyBatis--Plus-3.5.14-1F6FEB)
[![Apache License 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

</div>

## ✨ Why SmartORM?

SmartORM is an opt-in, annotation-driven enhancement library for MyBatis-Plus applications built with Spring Boot.

It removes repetitive, fixed-shape Mapper code while preserving the MyBatis-Plus programming model and its escape hatches. Adoption is incremental: choose one Mapper and one method, keep existing paths beside it, and roll back without redesigning the application.

> [!NOTE]
> SmartORM 2.0.x remains the verified release foundation. The current 2.1.x development line adds a locally installable Spring Boot Starter, but neither artifact has been published to Maven Central.

## 🚀 What It Provides

| Capability | Status | Current behavior |
|---|---|---|
| Annotation-driven CRUD | ✅ Available | `@SmartSelect`, `@SmartInsert`, `@SmartUpdate`, and `@SmartDelete` opt in per Mapper method |
| Pagination | ✅ Available | `@SmartPage` and `PageResult`; the application registers the MyBatis-Plus pagination interceptor |
| DTO / `Map` mapping | ✅ Available | Query result inference supports entity, `Map`, DTO/VO, and single-result paths |
| Native SQL path | ✅ Available | Rendered SQL keeps placeholder order aligned with its compact bound-parameter list |
| Fixed JOIN declarations | ✅ Available | `@SmartJoin` supports explicit or convention-inferred `ON`; the current implementation is MPJ-based |
| Mutation safety | ✅ Available | Smart update/delete operations without an effective `WHERE` are blocked by default |
| Lifecycle hooks | ✅ Available | `beforeSmartOperation`, `afterSmartOperation`, and `onSmartException` |
| Spring Boot Starter | 🧪 Development | `smartorm-spring-boot-starter:2.1.0-SNAPSHOT` is locally verified; it reuses application-owned MyBatis infrastructure and Mapper scanning |

`@SmartQuery` is an inactive placeholder, not a supported execution entry point.

## 🎯 When to Use It

| Good fit | Consider alternatives |
|---|---|
| Spring Boot applications already using MyBatis-Plus<br>Fixed CRUD and deterministic pagination<br>DTO / `Map` projections and fixed JOINs<br>Repeated Mapper template SQL<br>Teams that want gradual, reversible adoption | Non-Spring or reactive applications<br>Highly dynamic or database-specific SQL<br>Stored procedures<br>Mature complex XML/Provider SQL with no maintenance problem<br>Projects requiring a currently verified multi-database dialect layer |

## 🚫 What It Does Not Replace

> [!IMPORTANT]
> SmartORM does not replace MyBatis, MyBatis-Plus, `BaseMapper`, Wrapper, XML, Provider SQL, or MyBatis-Plus-Join. Despite its name, it is not a full ORM replacement; keep existing paths whenever they are clearer or more capable.

## ⚡ 30-Second Example

SmartORM keeps MyBatis-Plus entity metadata and adds an opt-in annotation to the Mapper method:

```java
@TableName("user")
class User {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_name")
    private String userName;

    private Integer status;
    // getters and setters
}

public interface UserMapper extends SmartMapper<User> {

    @SmartSelect(
            fields = {"id", "user_name", "status"},
            where = "status = #{0}",
            orderBy = "id"
    )
    List<User> findByStatus(Integer status);
}

List<User> activeUsers = userMapper.findByStatus(1);
```

`#{0}` refers to the first Java method argument. Unannotated methods continue to use normal MyBatis-Plus, Wrapper, XML, or Provider behavior. See [Getting Started](docs/getting-started.md) for the current Starter and Mapper-scan requirements.

## 📦 Installation

Maven Central publication is not available yet. After cloning and validating the repository, install the current reactor into your local Maven repository:

```powershell
mvn -DskipTests install
```

Then a local Spring Boot consumer can use the development Starter coordinates:

```xml
<dependency>
    <groupId>io.github.forgottenlab</groupId>
    <artifactId>smartorm-spring-boot-starter</artifactId>
    <version>2.1.0-SNAPSHOT</version>
</dependency>
```

The Starter uses Spring Boot's `AutoConfiguration.imports` discovery and does not require application component scanning for SmartORM runtime components. The application must still register exactly one `SmartNativeMapper`, normally by including `io.github.forgottenlab.smartorm.mapper` in its existing MyBatis Mapper scan. It reuses the application's `DataSource`, `SqlSessionFactory`, `SqlSessionTemplate`, transaction manager, and Mapper registration; it creates none of them.

Run the test gates separately before relying on `-DskipTests`; the command above only installs an already-validated local build. Applications that intentionally keep the manual integration path can depend on `io.github.forgottenlab:smartorm:2.1.0-SNAPSHOT` instead. The core artifact remains an ordinary library JAR. Demo classes, `application.yaml`, and demo SQL are excluded. MyBatis-Plus-Join remains transitive because it is exposed by `SmartMapper`; JSqlParser, Spring Web, and MySQL Connector/J retain the dependency boundaries documented in [Compatibility](docs/compatibility.md).

## 🛡️ Safe by Default

> [!WARNING]
> Full-table SmartORM updates and deletes are blocked by default. Native/JOIN runtime scalar values use ordered parameter bindings, while SQL structure remains developer-authored metadata. An intentional full-table operation requires method-level `allowFullTable = true`; there is no global switch that disables this guard.

The opt-in bypasses only the missing-effective-`WHERE` refusal. It does not add authorization, transactions, rollback, input validation, or database protection. SmartORM does not claim universal SQL-injection prevention; keep structural fragments static and review the exact [Safety](docs/safety.md) boundary.

## 🧪 Verified Testing

| Layer | Scope | Evidence |
|---|---|---|
| Database-independent regressions | Native SQL rendering/binding and mutation safety | Current preflight: 28/28 passed |
| Starter context regressions | Auto-configuration, backoff, discovery, application-owned Mapper scan | Current development preflight: 8/8 passed without a database |
| External Starter consumer | Isolated local Maven repository, real `@EnableAutoConfiguration` and application-owned `@MapperScan` | 1/1 passed online, then 1/1 offline |
| Historical MySQL double-run | Two independently created `mysql:9.4.0` volumes | 2026-07-20: 46/46 twice, followed by cleanup |
| Historical random-order probe | Fixed seed `20260720` | 2026-07-20: 46/46, followed by cleanup |
| Current MySQL release preflight | Fresh `smartorm-release-preflight` Compose project | 2026-08-12: 46/46; 0 failures/errors/skips; container/network/volume residue 0/0/0 |
| GitHub Actions | JDK 17, 28 tests, MySQL suite, package, reports, cleanup | Foundation SHA `c6e8c8b`: exact `push/main` run `31574822720` passed every gate; Starter feature CI is pending |

Compilation, package, and artifact checks are also part of the local release preflight. Each layer proves a different boundary; historical runs are not presented as current evidence. See [Testing](docs/testing.md).

## 🧱 Architecture at a Glance

```text
Annotated Mapper method
  -> Aspect
  -> Handler
  -> Resolver / Meta
  -> Wrapper Builder or SQL Renderer
  -> Executor
  -> MyBatis-Plus / MyBatis
```

SmartORM now uses a parent reactor with `smartorm` and the combined `smartorm-spring-boot-starter` child modules. The core module retains the existing public coordinates and source packages; demo sources remain inside core but are excluded from release-shaped artifacts. The optional JOIN-module split remains future major-version work. See [Architecture](docs/architecture.md).

## 📚 Documentation

| Topic | English | 简体中文 |
|---|---|---|
| Getting Started | [Guide](docs/getting-started.md) | [指南](docs/getting-started.zh-CN.md) |
| Migration | [Guide](docs/migration-from-mybatis-plus.md) | [指南](docs/migration-from-mybatis-plus.zh-CN.md) |
| Safety | [Guide](docs/safety.md) | [指南](docs/safety.zh-CN.md) |
| Testing | [Evidence](docs/testing.md) | [证据](docs/testing.zh-CN.md) |
| Architecture | [Current boundaries](docs/architecture.md) | [当前边界](docs/architecture.zh-CN.md) |
| Compatibility | [Matrix](docs/compatibility.md) | [矩阵](docs/compatibility.zh-CN.md) |
| Roadmap | [Planned direction](docs/roadmap.md) | [规划方向](docs/roadmap.zh-CN.md) |
| Release Checklist | [Preflight](docs/release-checklist.md) | [发布预检](docs/release-checklist.zh-CN.md) |

See also the [Changelog](CHANGELOG.md).

## 🗺️ Roadmap

The 2.1.x development line now contains the minimal consumer-tested Starter foundation. Configuration metadata, validation, diagnostics, API-usability work, and any deeper physical split remain separately gated roadmap items rather than delivered APIs or release promises. See the [Roadmap](docs/roadmap.md).

## 🤝 Contributing and Feedback

Focused issues and pull requests are welcome. Please include the compatibility point, a minimal reproducer, and the smallest relevant test. Keep public API, safety, migration, and bilingual documentation implications explicit; contribution and security policies remain release follow-up work.

## 📜 License

SmartORM is licensed under the [Apache License 2.0](LICENSE). No release, tag, Maven Central publication, or GitHub Release is implied by the current local preflight.
