# Release Checklist

[English](release-checklist.md) · [简体中文](release-checklist.zh-CN.md)

> **Summary:** This is an evidence ledger, not permission to publish. Local verification, remote verification, pending release work, and future product plans are deliberately separate.

## 🧭 Status Vocabulary

| Status | Meaning |
|---|---|
| ✅ Complete | The repository deliverable or documentation exists |
| 🧪 Locally Verified | The exact local command or isolated environment passed |
| ☁️ Remotely Verified | The exact remote workflow/repository action was observed passing |
| ⏳ Pending | Required release work has not been completed |
| ⚠️ Blocked | A named environment or evidence boundary currently prevents verification |
| 🗺️ Planned | Future product work; not implemented and not a release gate for 2.0.x |

## 🧪 Test Foundation

| Gate | Status | Evidence / next condition |
|---|---|---|
| 28 database-independent regressions | 🧪 Locally Verified | Current preflight: 28/28 |
| Disposable MySQL 9.4.0 environment | ✅ Complete | Pinned image, loopback binding, isolated profile, project-owned cleanup |
| Historical clean double-run | 🧪 Locally Verified | 2026-07-20: two independent 46/46 runs |
| Historical random-order probe | 🧪 Locally Verified | Seed `20260720`: 46/46 |
| Current fresh MySQL release preflight | 🧪 Locally Verified | 2026-08-12: 46/46, 0 failures/errors/skips, residue 0/0/0 |
| GitHub Actions workflow definition | ✅ Complete | JDK 17, 28/46 suites, package, report upload, always-cleanup |
| `c13426d` remote workflow result | ☁️ Remotely Verified | Exact `push/main` run `30647688231` passed all release-gate steps |
| Exact future release-commit rerun | ⏳ Pending | Requires a reviewed immutable release candidate |

## 📚 Documentation

| Gate | Status | Evidence / next condition |
|---|---|---|
| English landing README and aligned Chinese README | ✅ Complete | Hero, truthful badges, capability/adoption tables, safety and evidence |
| Bilingual Getting Started, Migration, Safety, Testing, Architecture, Compatibility, Roadmap, Release Checklist | ✅ Complete | Current/planned behavior remains visibly separated |
| Standalone Apache License 2.0 file | ✅ Complete | Root `LICENSE` matches the POM declaration |
| Public Markdown links and privacy scan | 🧪 Locally Verified | Current preflight validation |
| Contribution and security policies | ⏳ Pending | Review `CONTRIBUTING.md` and `SECURITY.md` in an independent task |

## 📦 Compatibility and Artifacts

| Gate | Status | Evidence / next condition |
|---|---|---|
| Exact Java/Boot/MP/MPJ/MySQL point documented | ✅ Complete | Unsupported versions and databases remain explicit |
| Incremental migration and rollback guidance | ✅ Complete | Existing BaseMapper/Wrapper/XML/Provider/MPJ paths remain valid |
| Ordinary main JAR plus sources/Javadoc/POM | 🧪 Locally Verified | Package and archive inspection pass without ignored Javadoc errors |
| Demo classes/configuration/SQL excluded from artifacts | 🧪 Locally Verified | Repository demo source remains available |
| Isolated local install and consumer smoke | 🧪 Locally Verified | Two consumer tests, including an offline repeat |
| Javadoc warning policy | ⏳ Pending | Generation passes; documentation-quality warnings require an owner threshold |
| Source author-email attribution decision | ⏳ Pending | Existing attribution is not a secret, but publication policy needs owner acceptance |
| Bit-for-bit reproducibility expectation | ⏳ Pending | ZIP timestamps are not normalized; no reproducibility claim |

## 🚦 Git and Publication Gates

| Gate | Status | Evidence / next condition |
|---|---|---|
| Build/docs changes split into reviewed local checkpoints | 🧪 Locally Verified | Two explicit-path local commits; boundaries checked before and after commit |
| Clean exact release commit | 🧪 Locally Verified | Local checkpoint HEAD is clean; push and exact-SHA remote validation remain pending |
| Maven Central availability | ⏳ Pending | No Central Portal publication, signing, or repository acceptance |
| Spring Boot Starter | 🗺️ Planned | Not implemented and must not enter 2.0.x finalization |
| Tag, push, GitHub Release | ⏳ Pending | Require separate explicit authorization |

## 📋 Current Decision

`READY_FOR_PUSH_AND_REMOTE_CI`.

The local Docker/MySQL, 28-test, package, artifact, and checkpoint-boundary gates are verified. Foundation SHA `c13426d` also passed its exact remote workflow, but the new local checkpoint HEAD has not been pushed or remotely verified. Signing/repository setup, Maven Central publication, tag, and release remain pending; no bit-for-bit reproducibility claim is made.
