# 发布检查清单

[English](release-checklist.md) · [简体中文](release-checklist.zh-CN.md)

> **摘要：** 本清单是证据账本，不代表发布权限。本地验证、远程验证、待完成发布事项与未来产品规划必须严格分开。

## 🧭 状态说明

| 状态 | 含义 |
|---|---|
| ✅ Complete | 仓库交付物或文档已经存在 |
| 🧪 Locally Verified | 准确本地命令或隔离环境已经通过 |
| ☁️ Remotely Verified | 已观察到准确远程 workflow/仓库动作通过 |
| ⏳ Pending | 必要发布事项尚未完成 |
| ⚠️ Blocked | 明确环境或证据边界当前阻止验证 |
| 🗺️ Planned | 未来产品工作；尚未实现，也不是 2.0.x 当前发布门禁 |

## 🧪 测试基础

| 门禁 | 状态 | 证据 / 下一条件 |
|---|---|---|
| 28 个数据库无关回归 | 🧪 Locally Verified | 本轮 preflight：28/28 |
| 可销毁 MySQL 9.4.0 环境 | ✅ Complete | 固定镜像、loopback 绑定、隔离 profile、项目自有 cleanup |
| 历史全新环境双轮 | 🧪 Locally Verified | 2026-07-20：两个独立环境各 46/46 |
| 历史随机顺序探针 | 🧪 Locally Verified | Seed `20260720`：46/46 |
| 本轮全新 MySQL release preflight | 🧪 Locally Verified | 2026-08-12：46/46，0 failures/errors/skips，残留 0/0/0 |
| GitHub Actions workflow 定义 | ✅ Complete | JDK 17、core 28、Starter 8、MySQL 46、package、报告上传与 always-cleanup |
| `c6e8c8b` main workflow 结果 | ☁️ Remotely Verified | 精确 `push/main` run `31574822720` 在 Starter commits 之前通过其 release gate steps |
| Starter feature workflow 结果 | ⏳ Pending | 需要单独授权的 feature push 与精确 SHA 运行 |
| 未来准确 release commit 复跑 | ⏳ Pending | 需要已经评审的不可变 release candidate |

## 📚 文档

| 门禁 | 状态 | 证据 / 下一条件 |
|---|---|---|
| 英文首页 README 与结构对应的中文 README | ✅ Complete | Hero、真实 badges、能力/适用表、安全与证据 |
| 双语快速开始、迁移、安全、测试、架构、兼容性、路线图、发布清单 | ✅ Complete | 当前与规划行为保持清晰分离 |
| 独立 Apache License 2.0 文件 | ✅ Complete | 根目录 `LICENSE` 与 POM 声明一致 |
| 公开 Markdown 链接与隐私扫描 | 🧪 Locally Verified | 本轮 preflight 验证 |
| 贡献与安全策略 | ⏳ Pending | 在独立任务中评审 `CONTRIBUTING.md` 与 `SECURITY.md` |

## 📦 兼容性与 Artifact

| 门禁 | 状态 | 证据 / 下一条件 |
|---|---|---|
| 精确 Java/Boot/MP/MPJ/MySQL 组合 | ✅ Complete | 未支持版本与数据库保持明确 |
| 渐进迁移与回滚指导 | ✅ Complete | 原有 BaseMapper/Wrapper/XML/Provider/MPJ 路径继续有效 |
| 普通主 JAR 与 sources/Javadoc/POM | 🧪 Locally Verified | Package 与 archive 检查通过，未忽略 Javadoc 错误 |
| Artifact 排除 demo class/configuration/SQL | 🧪 Locally Verified | 仓库仍保留 demo 源码 |
| 隔离本地 install 与使用方 smoke | 🧪 Locally Verified | 2 个使用方测试通过，包括离线复跑 |
| 2.1.x 开发版 Starter artifact 与使用方 | 🧪 Locally Verified | 普通 Starter JAR；21/21 上下文测试；默认扫描外部使用方 1/1 及离线复跑 |
| Javadoc warning 策略 | ⏳ Pending | 生成通过；文档质量 warning 仍需所有者阈值 |
| 源码作者邮箱署名决策 | ⏳ Pending | 既有署名不是 secret，但发布策略需所有者确认 |
| 位级可重复构建期望 | ⏳ Pending | ZIP timestamp 未归一化，不声明可重复性 |

## 🚦 Git 与发布门禁

| 门禁 | 状态 | 证据 / 下一条件 |
|---|---|---|
| 2.0.x foundation checkpoints | ☁️ Remotely Verified | Main SHA `c6e8c8b` 已 push，且精确 SHA workflow 通过 |
| Starter build/feature/docs checkpoints | 🧪 Locally Verified | 精确路径本地 commits，每次提交前后均检查边界 |
| Starter feature 远程验证 | ⏳ Pending | Foundation 任务中刻意不 push feature branch |
| Maven Central 可用性 | ⏳ Pending | 尚无 Central Portal 发布、签名或仓库接收 |
| Spring Boot Starter | 🧪 Locally Verified | 最小 `2.1.0-SNAPSHOT` foundation 已存在；它不属于 2.0.x release |
| Feature push、tag、GitHub Release | ⏳ Pending | 需要单独明确授权 |

## 📋 当前结论

`STARTER_FOUNDATION_LOCALLY_VERIFIED_REMOTE_PENDING`。

2.0.x foundation main SHA `c6e8c8b` 已通过精确远程 workflow。2.1.x Starter foundation 已通过本地上下文、使用方、core、MySQL、package、artifact、依赖与 cleanup 门禁，但 feature checkpoint 尚未 push 或远程验证。签名/仓库设置、Maven Central 发布、tag 与 release 仍待完成；本文不声明位级可重复构建。
