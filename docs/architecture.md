# Architecture

[English](architecture.md) | [简体中文](architecture.zh-CN.md)

This document separates the current 2.0.x implementation from future architecture. Planned modules and Starter types are not available today.

## Current scope

SmartORM is one Maven `jar` artifact containing library code, Spring integration, demo application/configuration, MPJ integration, MySQL driver/resources, and tests. The public entry point is source/local-module integration; there is no AutoConfiguration or physical child module.

## Runtime pipeline

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

## Main packages

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

## Non-JOIN query path

`@SmartSelect` and `@SmartPage` without JOIN metadata build MyBatis-Plus `QueryWrapper` instances and execute through `SmartMapperExecutor`. Return inference supports entity lists, Map lists, DTO lists, and single-object AUTO behavior. Pagination uses MyBatis-Plus `Page` and requires the pagination interceptor.

## JOIN and native query path

A query with `@SmartJoin` renders SQL and an ordered compact parameter list, then executes through `SmartNativeMapper` and `SmartNativeExecutor`.

- Explicit `ON` remains developer-authored structure; placeholders inside it are bound values.
- Missing `ON` falls back to naming-convention inference unless a `JdbcMetaProvider` has been initialized.
- The current repository does not wire `JdbcMetaProvider`, so convention inference is the proven default.
- Native/JOIN results support Map and DTO paths; native `ENTITY_LIST` is rejected.

## Mutation path

Insert resolves fields/values into an entity and delegates to MyBatis-Plus insert. Update and delete build Wrappers and execute through `SmartMapperExecutor`.

Before update/delete execution, `SmartMutationSafetyGuard` inspects final predicate segments. A missing effective predicate is rejected unless the method explicitly declares `allowFullTable = true`.

## Current integration requirements

Spring component scanning must include `io.github.forgottenlab.smartorm`, and Mapper scanning must include both application Mappers and `io.github.forgottenlab.smartorm.mapper`. The demo achieves this with `scanBasePackages` and `@MapperScan`.

There is no consumer-oriented Starter, configuration metadata, failure analyzer, startup validator, or Doctor in 2.0.x.

## Current dependency and API boundaries

- `SmartMapper<T>` publicly extends both `BaseMapper<T>` and `MPJBaseMapper<T>`.
- MySQL and Spring Web dependencies are currently part of the single artifact rather than isolated demo dependencies.
- Demo code and runtime integration live beside core semantics.
- `SmartQuery` is public but inactive.
- Wrapper and native paths do not share identical scalar-binding implementation.

These are compatibility constraints, not hidden implementation details.

## Future direction — planned only

The roadmap proposes:

- 2.1.x: consumer-tested Spring Boot Starter, AutoConfiguration, declaration validation, and diagnostics.
- 2.2.x: focused API usability improvements after compatibility design.
- 3.0: physical core/Spring/JOIN/demo separation and a `SmartMapper`/`SmartJoinMapper` boundary.

None of these modules, coordinates, properties, or APIs is implemented by this document. See [Roadmap](roadmap.md).
