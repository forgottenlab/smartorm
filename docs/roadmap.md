# Roadmap

[English](roadmap.md) · [简体中文](roadmap.zh-CN.md)

> **Summary:** This roadmap communicates sequence and quality gates. It is not implementation authorization, a release promise, or a date commitment.

## 🧱 Current — 2.0.x Release Foundation

Goals:

- trusted database-independent and disposable MySQL tests;
- isolated test configuration;
- local CI workflow design;
- accurate English/Chinese public documentation;
- migration, safety, compatibility, testing, and release-preflight truth;
- correctness fixes without broad API redesign.

Current evidence:

- 35 database-independent tests pass locally, including real Wrapper binding state for non-JOIN WHERE values.
- The current 2026-09-16 fresh MySQL 9.4.0 parameter-binding preflight passes 47/47 and cleans container/network/volume state to 0/0/0.
- Two independent 2026-07-20 MySQL runs and one fixed-seed randomized-order run remain historical evidence.
- An ordinary main JAR, sources, Javadoc, standalone license, isolated install, and external consumer smoke are locally verified.
- Exact merged-main SHA `093318aa` passed GitHub Actions `push/main` run `35074543269` with core 35 database-independent tests, Starter 21/21, MySQL 47/47, package, report upload, and cleanup.
- Signing/repository setup, Maven Central publication, tag, and release remain open.

## 🚀 In Progress — 2.1.x Adoption Experience

Implemented and verified locally and by exact-head pull-request CI in the current development line:

- a parent reactor with compatible `smartorm` core and a combined `smartorm-spring-boot-starter`;
- `AutoConfiguration.imports` discovery without SmartORM component scanning;
- late precise registration of one internal `SmartNativeMapper`, while application business Mapper discovery and MyBatis/database/transaction infrastructure remain application-owned;
- 21 database-free auto-configuration tests and an isolated default-scanning Spring Boot consumer test, including an offline repeat;
- full core regression and disposable MySQL checks after the topology change.
- Starter foundation merged through PR #1, followed by PR #2 for the non-JOIN parameter-binding repair;
- exact merged-main SHA `093318aa` verified by `push/main` run `35074543269`;
- a read-only Lingxi audit that selected one reversible `user-service` Mapper method as the proposed pilot scope; the initial pilot exposed the parameter-binding gap, which is now fixed on `main`, while real-project equivalence testing remains separately gated.

Still gated by separately approved work:

- the explicitly authorized Lingxi pilot source migration and behavioral-equivalence proof;
- focused configuration properties/metadata;
- registered-Mapper declaration validation;
- redacted diagnostics/Doctor and actionable failure analysis.

The `2.1.0-SNAPSHOT` Starter is a locally verified development artifact, not a stable or Maven Central release. No configuration property, Validator, Doctor, or FailureAnalyzer is available today.

## 🧩 Planned — 2.2.x API Usability

Candidates after the Starter contract is stable:

- named parameters;
- `@SmartCount` and `@SmartExists`;
- pagination bounds and identifier validation;
- redacted SQL preview;
- stabilized test support;
- optional JOIN-boundary preparation.

Each item requires a compatibility design, tests, bilingual docs, and an explicit implementation task. None is part of 2.0.x.

## 🏗️ Planned — 3.0 Structural Boundary

Potential major-version work:

- physical core/Spring/Starter/JOIN/demo separation;
- `SmartMapper` without MPJ as a base public dependency;
- a separate `SmartJoinMapper` and optional JOIN module;
- removal of demo, Web, MySQL, and MPJ implementation dependencies from core;
- migration adapters/guides and a tested module matrix;
- BOM only after the module graph is stable.

This work is source/binary significant and cannot be pulled into a minor release casually.

## ⏸️ Deferred

- PostgreSQL/MariaDB implementations until a tested dialect contract exists.
- IntelliJ plugin.
- annotation processor.
- non-Spring runtime.
- stored-procedure abstraction.
- broad dynamic query DSL.

Replacing MyBatis/XML/Wrapper or reimplementing MyBatis-Plus is not a product direction.

## 🚦 Sequence Gate

The intended order is:

1. trusted tests and public documentation;
2. reviewable release foundation and consumer tests;
3. minimal Starter foundation, exact-head remote validation, and isolated adoption planning (complete);
4. explicitly authorized one-method Lingxi pilot migration and equivalence proof;
5. API usability improvements;
6. major-version module separation.

Only the latest user-approved task authorizes work.
