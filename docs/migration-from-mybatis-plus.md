# Migration from MyBatis-Plus

[English](migration-from-mybatis-plus.md) · [简体中文](migration-from-mybatis-plus.zh-CN.md)

> **Summary:** SmartORM is an enhancement, not a replacement. Migrate incrementally, prove behavior with deterministic tests, and keep a per-method rollback path.

Existing `BaseMapper`, Wrapper, XML, Provider, MyBatis-Plus annotations, and MyBatis-Plus-Join implementations remain valid.

## 🧭 Availability Map

| Level | Goal | Current 2.0.x status |
|---|---|---|
| 0 | Add the current source/local module or locally installed artifact without method migration | Local artifact consumer smoke verified; public repository onboarding is not verified |
| 1 | Opt in one Mapper | Implemented |
| 2 | Opt in one method | Implemented |
| 3 | Replace repeated fixed-shape SQL selectively | Implemented with tests required |
| 4 | Enable consumer diagnostics | Planned for a future Starter; not available today |
| 5 | Scale by application module | Process guidance; requires your own regression and rollback controls |

## 0️⃣ Level 0 — Keep Existing Behavior

Start in a branch or local evaluation module. Do not change Mapper inheritance or methods yet.

- Keep `BaseMapper`, XML, Wrapper, Provider, and existing MPJ code unchanged.
- Run the existing application and test suite before and after adding the local module.
- Record the current dependency surface: the repository is still single-module; release artifacts exclude demo classes/configuration/SQL, MPJ remains transitive, and JSqlParser/Web/MySQL are optional as documented.

Current limitation: an isolated local-repository install and external two-test consumer smoke passed, including an offline rerun, but there is no verified Maven Central artifact or Starter. This proves a local dependency-only path, not public installation availability.

Rollback: remove the local module/dependency and scan configuration.

## 1️⃣ Level 1 — Opt In One Mapper

Change one low-risk Mapper:

```java
public interface UserMapper extends SmartMapper<User> {
}
```

Inherited MyBatis-Plus methods remain available. Existing XML, Wrapper, Provider, and unannotated custom methods can remain in the same Mapper.

Current 2.0.x note: `SmartMapper` also extends `MPJBaseMapper`; MPJ is therefore not optional in the current public type hierarchy.

Rollback: restore `BaseMapper<User>` and remove only SmartORM-specific methods/hooks from that Mapper.

## 2️⃣ Level 2 — Migrate One Method

Choose a deterministic, fixed-shape method with existing tests:

```java
@SmartSelect(where = "status = #{0}", orderBy = "id")
List<User> findByStatus(Integer status);
```

Recommended sequence:

1. Keep the legacy XML/Wrapper/Provider method during comparison.
2. Create the same fixture for both paths.
3. Compare exact rows, order, null behavior, exceptions, and database effects.
4. Switch callers gradually.
5. Keep the old path for the agreed rollback window.

Rollback: switch callers back and remove the annotated method. Other Mapper methods are unaffected.

## 3️⃣ Level 3 — Replace Repetitive Template SQL

Good candidates:

- simple fixed selects;
- deterministic pagination;
- simple predicate-bound updates and deletes;
- fixed JOIN projections to `Map` or DTO;
- repeated Mapper boilerplate with exact expected behavior.

Do not migrate merely to maximize SmartORM usage. Preserve complex or mature code when the replacement would reduce clarity or increase risk.

Rollback: retain or checkpoint the original implementation and revert one method at a time.

## 4️⃣ Level 4 — Enable Diagnostics

This level is a future migration stage, not a current feature. The planned Starter may provide registered-Mapper validation, module checks, actionable failures, and redacted diagnostics.

Until that exists:

- rely on focused unit/integration tests;
- keep SQL and argument logs free of sensitive values;
- diagnose component/Mapper scanning manually;
- do not document a Doctor, FailureAnalyzer, or AutoConfiguration as available.

There is nothing to enable or disable in 2.0.x today.

## 5️⃣ Level 5 — Scale by Application Module

Only expand after Levels 1–3 are stable for a representative period.

- Migrate one bounded application module at a time.
- Assign a rollback owner.
- Measure query results, database effects, error rate, and support burden.
- Stop the migration wave when behavior is ambiguous or the escape hatch is better.
- Keep the dependency if other modules still use SmartORM; rollback only the affected module.

## 🚫 When Not to Migrate

Do not migrate these merely for consistency:

- complex runtime-dynamic SQL;
- stored procedures;
- database-specific SQL;
- mature XML/Provider code with no maintenance problem;
- queries requiring unsupported databases or unverified framework versions;
- code without deterministic regression and rollback coverage;
- arbitrary client-controlled fields, operators, ordering, or SQL fragments.

## 🔗 JOIN Migration Notes

Current 2.0.x JOIN declarations use the native SQL path, and `SmartMapper` directly inherits MPJ. Validate row multiplicity, aliases, explicit/inferred `ON`, placeholder order, DTO mapping, and pagination totals.

The proposed separate JOIN module and `SmartJoinMapper` belong to the 3.0 roadmap. They are not current coordinates or APIs.

## 🛡️ Write Migration Safety

`@SmartUpdate` and `@SmartDelete` fail closed when no effective `WHERE` predicate is generated. If legacy code intentionally performs a full-table operation, migration requires an explicit method-level review and `allowFullTable = true` declaration.

Do not use the opt-in to silence a missing/incorrect predicate. See [Safety](safety.md).

## ✅ Before Migration

- [ ] The project matches the exact verified compatibility point or has its own matrix.
- [ ] Existing behavior is covered for rows, ordering, nulls, exceptions, totals, and database effects.
- [ ] SQL structure is fixed and developer-controlled.
- [ ] Placeholder-to-argument mapping is reviewed.
- [ ] A rollback implementation and owner exist.
- [ ] Write operations have an effective predicate or reviewed full-table intent.

## 🧪 After Migration

- [ ] Legacy and SmartORM behavior match on deterministic fixtures.
- [ ] Placeholder count and order are correct.
- [ ] Pagination totals/order and JOIN multiplicity match.
- [ ] No sensitive SQL parameters are logged.
- [ ] Rollback has been rehearsed in a test environment.
