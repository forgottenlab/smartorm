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

- 28 database-independent tests pass locally.
- The current 2026-08-12 fresh MySQL 9.4.0 release preflight passes 46/46 and cleans container/network/volume state to 0/0/0.
- Two independent 2026-07-20 MySQL runs and one fixed-seed randomized-order run remain historical evidence.
- An ordinary main JAR, sources, Javadoc, standalone license, isolated install, and external consumer smoke are locally verified.
- The exact GitHub Actions `push/main` run for Foundation SHA `c13426d` passed the 28-test, MySQL 46-test, package, report, and cleanup gates.
- The new local checkpoint HEAD still requires push and exact-SHA remote CI verification; signing/repository setup, Maven Central publication, tag, and release remain open.

## 🚀 Planned — 2.1.x Adoption Experience

Direction, subject to a separately approved task and consumer tests:

- Spring Boot Starter;
- AutoConfiguration integrated with MyBatis-Plus rather than competing with it;
- focused configuration metadata;
- registered-Mapper declaration validation;
- redacted diagnostics/Doctor and actionable failure analysis.

No Starter coordinate, property, AutoConfiguration class, or diagnostic API is available today.

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
3. Starter on the compatible artifact;
4. API usability improvements;
5. major-version module separation.

Only the latest user-approved task authorizes work.
