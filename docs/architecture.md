# Architecture

[English](architecture.md) · [简体中文](architecture.zh-CN.md)

> **Summary:** SmartORM 2.0.x uses a single-module, layered runtime. This document separates that implemented shape from future architecture; planned modules and Starter types are not available today.

## 🧭 Current Scope

SmartORM remains one Maven module whose source tree contains library code, Spring integration, the demo application/configuration, MPJ integration, MySQL support/resources, and tests. The 2.0.x release build produces an ordinary library main JAR plus sources and Javadoc JARs. Demo classes, `application.yaml`, and demo SQL are excluded from release artifacts while remaining available in the repository source tree. There is no AutoConfiguration or physical child module.

## 🔄 Runtime Pipeline

```text
Annotated Mapper method
  -> SmartAnnotationAspect
  -> SmartHandlerRegistry / SmartXXXHandler
  -> SmartXXXResolver / Meta
  -> SmartWrapperBuilder or SmartXXXSqlRenderer
  -> SmartMapperExecutor or SmartNativeExecutor
  -> MyBatis-Plus / MyBatis
```

Lifecycle hooks on `SmartMapper` are called around the Aspect path: `beforeSmartOperation`, `afterSmartOperation`, and `onSmartException`.

## 📦 Main Packages

| Package | Current responsibility |
|---|---|
| `annotations` | Public Smart annotations and enums |
| `aspect` | AOP interception and lifecycle hook coordination |
| `handler` | Annotation dispatch and execution coordination |
| `resolver`, `resolver.meta` | Annotation/method metadata parsing |
| `builder` | MyBatis-Plus Wrapper construction and mutation safety guard |
| `sql.renderer`, `sql.model`, `sql.provider` | Native SQL rendering and MyBatis provider path |
| `executor` | Wrapper and native execution adapters |
| `mapper` | `SmartMapper` and internal `SmartNativeMapper` |
| `support`, `util` | JOIN inference, mapping, metadata, expressions, and helpers |
| `demo` | Embedded application, entities, Mappers, and MyBatis configuration |

## 🔎 Non-JOIN Query Path

`@SmartSelect` and `@SmartPage` without JOIN metadata build MyBatis-Plus `QueryWrapper` instances and execute through `SmartMapperExecutor`. Return inference supports entity lists, Map lists, DTO lists, and single-object AUTO behavior. Pagination uses MyBatis-Plus `Page` and requires the pagination interceptor.

## 🔗 JOIN and Native Query Path

A query with `@SmartJoin` renders SQL and an ordered compact parameter list, then executes through `SmartNativeMapper` and `SmartNativeExecutor`.

- Explicit `ON` remains developer-authored structure; placeholders inside it are bound values.
- Missing `ON` falls back to naming-convention inference unless a `JdbcMetaProvider` has been initialized.
- The current repository does not wire `JdbcMetaProvider`, so convention inference is the proven default.
- Native/JOIN results support Map and DTO paths; native `ENTITY_LIST` is rejected.

## ✍️ Mutation Path

Insert resolves fields/values into an entity and delegates to MyBatis-Plus insert. Update and delete build Wrappers and execute through `SmartMapperExecutor`.

Before update/delete execution, `SmartMutationSafetyGuard` inspects final predicate segments. A missing effective predicate is rejected unless the method explicitly declares `allowFullTable = true`.

## 🔧 Current Integration Requirements

Spring component scanning must include `io.github.forgottenlab.smartorm`, and Mapper scanning must include both application Mappers and `io.github.forgottenlab.smartorm.mapper`. The demo achieves this with `scanBasePackages` and `@MapperScan`.

There is no consumer-oriented Starter, configuration metadata, failure analyzer, startup validator, or Doctor in 2.0.x.

## 🧩 Current Dependency and API Boundaries

- `SmartMapper<T>` publicly extends both `BaseMapper<T>` and `MPJBaseMapper<T>`.
- MyBatis-Plus-Join remains a transitive dependency because `SmartMapper` exposes `MPJBaseMapper`.
- JSqlParser is optional; Spring Web and MySQL Connector/J are runtime-optional rather than mandatory transitive consumer dependencies.
- Demo code and runtime integration still live beside core semantics in the source module, although demo classes/configuration/SQL are excluded from release artifacts.
- The locally installed artifact has passed a two-test external consumer smoke, including an offline rerun; Maven Central consumption remains unverified.
- `SmartQuery` is public but inactive.
- Wrapper and native paths do not share identical scalar-binding implementation.

These are compatibility constraints, not hidden implementation details.

## 🗺️ Future Direction — Planned Only

The roadmap proposes:

- 2.1.x: consumer-tested Spring Boot Starter, AutoConfiguration, declaration validation, and diagnostics.
- 2.2.x: focused API usability improvements after compatibility design.
- 3.0: physical core/Spring/JOIN/demo separation and a `SmartMapper`/`SmartJoinMapper` boundary.

None of these modules, coordinates, properties, or APIs is implemented by this document. See [Roadmap](roadmap.md).
