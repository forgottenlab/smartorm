# Safety

[English](safety.md) | [简体中文](safety.zh-CN.md)

SmartORM favors fail-closed behavior for destructive annotated writes. This document describes implemented 2.0.x behavior only; it is not a claim that SmartORM replaces application authorization, transaction design, database permissions, backups, or database-native safeguards.

## Default write policy

`@SmartUpdate` and `@SmartDelete` reject execution when the completed MyBatis-Plus Wrapper contains no effective normal predicate segment.

Rejected examples include:

```java
@SmartDelete
int deleteAllRowsAccidentally();
```

```java
@SmartUpdate(
        fields = {"status"},
        values = {"#{0}"},
        where = ""
)
int updateAllRowsAccidentally(Integer status);
```

Blank/whitespace expressions, a `WHERE`-only shell, and predicates that are disabled at runtime remain fail-closed.

## Explicit full-table opt-in

A deliberate full-table operation requires the method-level flag:

```java
@SmartUpdate(
        fields = {"status"},
        values = {"#{0}"},
        allowFullTable = true
)
int resetEveryStatus(Integer status);
```

```java
@SmartDelete(allowFullTable = true)
int deleteEveryRow();
```

The default is `false` for both annotations.

## What `allowFullTable = true` does

It bypasses only SmartORM's missing-effective-`WHERE` refusal for that one Mapper method.

It does not:

- add authorization or user confirmation;
- start or require a transaction;
- bypass database permissions, constraints, triggers, or interceptors;
- validate business intent;
- change field/value parsing or placeholder semantics;
- make an operation reversible;
- affect `BaseMapper`, XML, Provider, or non-SmartORM SQL paths.

Treat every opt-in as security-sensitive code that requires review, a transaction/rollback plan, deterministic tests, and a documented owner.

## Predicate and SET boundaries

The guard runs after Wrapper construction and before execution. SET assignments do not count as predicates. Only the final structured normal-predicate segments satisfy the guard.

This prevents code from treating an update value such as `status = 0` as proof that a restrictive `WHERE` exists.

## SQL structure and runtime values

Annotation properties such as `fields`, `where`, `orderBy`, JOIN table/alias, and explicit `ON` are developer-authored SQL structure. They must never be generated directly from arbitrary client input.

Runtime method arguments use numeric placeholders such as `#{0}`. Current paths differ:

- Native/JOIN renderers convert placeholders into ordered MyBatis parameter bindings.
- Non-JOIN `@SmartSelect` and the shared `@SmartPage`/`@SmartDelete` WHERE builder convert numeric method-argument placeholders into compact MyBatis-Plus `Wrapper.apply` bindings. Runtime values remain in Wrapper parameter state, including String and null values; repeated indexes reuse the same logical binding.
- `@SmartUpdate` still has a separate WHERE implementation, and expression-style `setSql` values still use `SmartExpressionUtil.fillExpression`. Ordinary non-expression SET values continue to use Wrapper binding.

Therefore, do not claim that every current path has identical prepared-statement binding semantics. Keep annotation structure static, validate input at the application boundary, and add regression tests for quotes, nulls, ordering, and database effects.

## JOIN `ON` boundary

Explicit `ON` is treated as developer-authored structure, not as one scalar parameter. `#{n}` fragments inside `ON` are bound values on the native path.

Current validation rejects semicolons and selected DML keywords, but it is not a general SQL parser or sanitizer. Never accept arbitrary external `ON` expressions. Prefer fixed column references and verify aliases and placeholder order in tests.

## Scope of protection

The empty-predicate guard covers only the SmartORM `@SmartUpdate` and `@SmartDelete` execution paths. Existing MyBatis-Plus methods, Wrapper calls, XML, Provider SQL, scripts, and direct JDBC remain governed by their own controls.

Database interceptors may provide defense in depth, but they do not replace the method-level SmartORM safety contract.

## Review checklist for full-table operations

- [ ] The absence of `WHERE` is intentional, not caused by a placeholder or condition bug.
- [ ] The method name clearly communicates full-table impact.
- [ ] Callers are authorized and cannot reach the method through arbitrary external input.
- [ ] Transaction, backup, rollback, and maintenance-window requirements are defined.
- [ ] A database integration test checks the affected row count and final state.
- [ ] Existing database-side protection remains enabled.
- [ ] The opt-in and rationale are visible in code review.

## Regression evidence

`SmartQuerySupportBindingTest` contains seven database-independent tests covering integer, String, multiple, repeated, sparse/out-of-order, null, input immutability, and the non-JOIN Handler boundary with real MyBatis-Plus Wrapper state. `SmartMutationWhereSafetyTest` retains 18 tests for the mutation guard. The database suite separately verifies execution and final MyBatis `BoundSql` parameter mappings against disposable MySQL 9.4.0.

See [Testing](testing.md) for exact evidence and commands.
