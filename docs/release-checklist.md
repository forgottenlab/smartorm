# Release Checklist

[English](release-checklist.md) | [简体中文](release-checklist.zh-CN.md)

This checklist is a preflight record, not permission to publish. Every release action requires separate explicit authorization.

## Test foundation

- [x] Database-independent suite exists: 28 tests.
- [x] Disposable MySQL 9.4.0 environment exists with project-owned cleanup.
- [x] Two independent fresh database runs passed 46/46 on 2026-07-20.
- [x] Fixed-seed randomized-order database run passed 46/46.
- [ ] GitHub Actions workflow has completed successfully on the release commit.
- [ ] Release-candidate checks have been rerun from the exact release commit.

## Documentation

- [x] English default README and aligned Chinese README exist.
- [x] Getting Started, Migration, Safety, Testing, Architecture, Compatibility, and Roadmap exist in both languages.
- [x] Current and planned behavior are visibly separated.
- [x] Unsupported compatibility and unpublished Starter/artifact claims are avoided.
- [ ] Standalone repository license file exists and matches the POM declaration.
- [ ] Contribution/security policies required for the public project are reviewed.

## Compatibility and migration

- [x] Exact verified Java/Spring Boot/MyBatis-Plus/MPJ/MySQL point is documented.
- [x] Unverified databases, versions, build tools, and consumer paths are listed.
- [x] Incremental migration and rollback guidance exists.
- [x] Full-table mutation opt-in and protection scope are documented.
- [ ] A clean external consumer project verifies the public artifact and integration path.

## Artifact verification

- [ ] `mvn package` succeeds from a clean environment.
- [ ] Source and Javadoc artifacts are generated and inspected.
- [ ] Artifact contents exclude demo-only/sensitive/generated files as intended.
- [ ] POM metadata, SCM, license, developers, coordinates, and version are reviewed.
- [ ] Reproducibility/checksum expectations are defined.
- [ ] Signing and repository requirements are validated without publishing.

Current blocker: local offline packaging cannot resolve `maven-jar-plugin:3.4.2` from cache.

## Git preflight

- [ ] Working tree is clean.
- [ ] Release diff contains no unrelated user files, `.ai/history`, logs, build outputs, or secrets.
- [ ] Branch, HEAD, remote, and intended tag are reviewed.
- [ ] Required local commits are separated and reviewed.
- [ ] No force-push or remote-history rewrite is required.

The current working tree is intentionally not clean and includes protected user changes; it is not release-ready.

## Changelog and release notes

- [x] `CHANGELOG.md` has an `[Unreleased]` section.
- [ ] Final version/date and complete user-visible changes are reviewed.
- [ ] Release notes describe installation reality, safety baseline, compatibility, limitations, and migration.
- [ ] Links in release notes resolve against the release tag.
- [ ] Known issues and rollback guidance are included.

## Authorization gate

- [ ] User explicitly authorizes commit/tag/push/release actions for the exact reviewed state.
- [ ] Publication credentials and remote repository access are handled outside documentation/logs.
- [ ] Post-release verification and rollback owner are assigned.

No release, tag, push, or publication was executed while creating this checklist.
