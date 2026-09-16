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
| 35 database-independent regressions | ☁️ Remotely Verified | Exact main SHA `093318aa`, `push/main` run `35074543269`: 35/35 |
| Disposable MySQL 9.4.0 environment | ✅ Complete | Pinned image, loopback binding, isolated profile, project-owned cleanup |
| Historical clean double-run | 🧪 Locally Verified | 2026-07-20: two independent 46/46 runs |
| Historical random-order probe | 🧪 Locally Verified | Seed `20260720`: 46/46 |
| Current fresh MySQL parameter-binding preflight | ☁️ Remotely Verified | Local preflight 47/47 with real `BoundSql`; exact main run `35074543269` also passed MySQL 47/47 and removed dedicated Compose resources |
| GitHub Actions workflow definition | ✅ Complete | JDK 17, core 35, Starter 21, MySQL 47, package, report upload, always-cleanup |
| `c6e8c8b` main workflow result | ☁️ Remotely Verified | Exact `push/main` run `31574822720` passed its release-gate steps before the Starter commits |
| Starter feature workflow result | ☁️ Remotely Verified | Historical Draft PR #1 `pull_request` run `32653878401` completed successfully for exact feature SHA `18554e6` |
| Parameter-binding merge checkpoint workflow | ☁️ Remotely Verified | Checkpoint SHA `093318aa`; `push/main` run `35074543269` passed core 35, Starter 21, MySQL 47, package, reports, and cleanup |
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
| 2.1.x development Starter artifact and consumer | 🧪 Locally Verified | Ordinary Starter JAR; 21/21 context tests; default-scanning external consumer 1/1 plus offline repeat |
| Javadoc warning policy | ⏳ Pending | Generation passes; documentation-quality warnings require an owner threshold |
| Source author-email attribution decision | ⏳ Pending | Existing attribution is not a secret, but publication policy needs owner acceptance |
| Bit-for-bit reproducibility expectation | ⏳ Pending | ZIP timestamps are not normalized; no reproducibility claim |

## 🚦 Git and Publication Gates

| Gate | Status | Evidence / next condition |
|---|---|---|
| 2.0.x foundation checkpoints | ☁️ Remotely Verified | Main SHA `c6e8c8b` is pushed and its exact-SHA workflow passed |
| Starter foundation merge | ☁️ Remotely Verified | PR #1 merged the Starter foundation into `main`; later main verification includes the Starter tests |
| Parameter-binding fix merge | ☁️ Remotely Verified | PR #2 merged commits `6d7cd49` and `523e9e7` into main SHA `093318aa` |
| Maven Central availability | ⏳ Pending | No Central Portal publication, signing, or repository acceptance |
| Spring Boot Starter | ☁️ Remotely Verified | Minimal `2.1.0-SNAPSHOT` foundation is on `main`; exact main run `35074543269` passed Starter 21/21 |
| Main post-merge CI | ☁️ Remotely Verified | `push/main` run `35074543269` completed successfully for exact main SHA `093318aa` |
| Tag / GitHub Release | ⏳ Pending | No tag, GitHub Release, or stable `2.1.0` publication has been created |

## 📋 Current Decision

`SMARTORM_MAIN_POST_MERGE_CI_VERIFIED`.

The 2.1.x Starter foundation is merged into `main`, and PR #2 merged the non-JOIN `@SmartSelect.where` parameter-binding fix. Exact main SHA `093318aa46143f052cdf54b3183b0fda80eba4da` passed `push/main` run `35074543269` with core 35 database-independent tests, Starter 21/21, MySQL 47/47, package, report upload, and dedicated Compose cleanup. Maven Central publication, signing/repository setup, tag, stable `2.1.0`, and GitHub Release remain pending; no bit-for-bit reproducibility claim is made.
