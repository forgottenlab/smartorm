# 发布检查清单

[English](release-checklist.md) | [简体中文](release-checklist.zh-CN.md)

本清单是 preflight 记录，不代表发布权限。所有发布动作都需要单独明确授权。

## 测试基础

- [x] 已建立 28 个数据库无关测试。
- [x] 已建立项目自有并可清理的 MySQL 9.4.0 可销毁环境。
- [x] 2026-07-20 两个独立全新数据库环境各通过 46/46。
- [x] 固定 seed 的随机顺序数据库运行通过 46/46。
- [ ] GitHub Actions workflow 已在 release commit 上成功运行。
- [ ] 已从准确 release commit 重新运行 release-candidate 检查。

## 文档

- [x] 英文默认 README 与结构一致的中文 README 已存在。
- [x] 快速开始、迁移、安全、测试、架构、兼容性和路线图均有双语版本。
- [x] 已明确区分当前行为和规划行为。
- [x] 避免了未验证兼容性和未发布 Starter/artifact 声明。
- [ ] 仓库存在与 POM 声明一致的独立许可证文件。
- [ ] 已评审公开项目需要的贡献/安全策略。

## 兼容性与迁移

- [x] 已记录精确验证的 Java/Spring Boot/MyBatis-Plus/MPJ/MySQL 组合。
- [x] 已列出未验证数据库、版本、构建工具和使用方路径。
- [x] 已建立渐进迁移与回滚指导。
- [x] 已说明全表写 opt-in 与保护范围。
- [ ] 干净外部使用方项目已经验证公共 artifact 与接入路径。

## Artifact 验证

- [ ] `mvn package` 在干净环境成功。
- [ ] Source 与 Javadoc artifact 已生成并检查。
- [ ] Artifact 内容按预期排除 demo-only/敏感/生成文件。
- [ ] 已评审 POM metadata、SCM、license、developers、coordinates 和 version。
- [ ] 已定义可重复构建/checksum 期望。
- [ ] 在不发布的前提下完成签名与仓库要求验证。

当前阻塞：本地离线 package 无法从缓存解析 `maven-jar-plugin:3.4.2`。

## Git 预检

- [ ] 工作树干净。
- [ ] Release diff 不包含无关用户文件、`.ai/history`、日志、构建输出或敏感信息。
- [ ] 已评审 branch、HEAD、remote 和预期 tag。
- [ ] 所需本地 commit 已隔离并评审。
- [ ] 不需要 force-push 或改写远程历史。

当前工作树有意保持非干净状态并包含受保护用户修改，因此尚未 release-ready。

## Changelog 与 Release Notes

- [x] `CHANGELOG.md` 存在 `[Unreleased]`。
- [ ] 已评审最终版本/日期和完整用户可见变更。
- [ ] Release notes 说明安装现实、安全基线、兼容性、限制和迁移。
- [ ] Release notes 链接在 release tag 上有效。
- [ ] 已包含已知问题和回滚指导。

## 授权门禁

- [ ] 用户对准确评审状态明确授权 commit/tag/push/release。
- [ ] 发布凭据与远程仓库访问在文档/日志之外处理。
- [ ] 已指定发布后验证与回滚负责人。

创建本清单期间没有执行 release、tag、push 或 publication。
