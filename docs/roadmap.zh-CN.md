# 路线图

[English](roadmap.md) · [简体中文](roadmap.zh-CN.md)

> **摘要：** 本路线图用于说明演进顺序与质量门禁，不代表实现授权、发布承诺或日期承诺。

## 🧱 Current — 2.0.x Release Foundation

目标：

- 可信的数据库无关测试和可销毁 MySQL 测试；
- 隔离测试配置；
- 本地 CI workflow 设计；
- 准确的英文/中文公开文档；
- 真实说明迁移、安全、兼容性、测试与发布预检；
- 在不广泛重设 API 的前提下修复正确性问题。

当前证据：

- 35 个数据库无关测试在本地通过，其中包含无 JOIN WHERE 运行时值的真实 Wrapper 绑定状态。
- 当前 2026-09-16 全新 MySQL 9.4.0 参数绑定 preflight 通过 47/47，并把 container/network/volume 清理至 0/0/0。
- 2026-07-20 两个独立 MySQL 运行与一轮固定 seed 随机顺序运行作为历史证据保留。
- 普通主 JAR、sources、Javadoc、独立许可证、隔离 install 与外部使用方 smoke 已在本地验证。
- 精确合并后 main SHA `093318aa` 已通过 GitHub Actions `push/main` run `35074543269`，覆盖 core 35 个数据库无关测试、Starter 21/21、MySQL 47/47、package、报告上传与 cleanup。
- 签名/仓库设置、Maven Central 发布、tag 与 release 仍待完成。

## 🚀 In Progress — 2.1.x 接入体验

当前开发线已在本地及精确 head 的 pull-request CI 中实现并验证：

- 包含兼容 `smartorm` core 与合并式 `smartorm-spring-boot-starter` 的父 reactor；
- 通过 `AutoConfiguration.imports` 发现，无需扫描 SmartORM component；
- 延迟精确注册一个内部 `SmartNativeMapper`，应用继续拥有业务 Mapper 发现与 MyBatis/数据库/事务基础设施；
- 21 个数据库无关自动配置测试，以及包含离线复跑和默认业务 Mapper 扫描的隔离外部 Spring Boot 使用方测试；
- 拓扑变更后完整的 core 回归与可销毁 MySQL 检查。
- Starter foundation 已通过 PR #1 合并，随后 PR #2 合并无 JOIN 参数绑定修复；
- 精确合并后 main SHA `093318aa` 已由 `push/main` run `35074543269` 验证；
- 已完成只读 Lingxi 审计，并选择一个可回滚的 `user-service` Mapper 方法作为拟议 pilot 范围；首次 pilot 暴露出的参数绑定缺口现已在 `main` 修复，但真实项目行为等价验证仍需单独门禁。

仍需通过单独获批任务完成：

- 获得明确授权后执行 Lingxi pilot 源码迁移，并证明行为等价；
- 聚焦的配置属性/元数据；
- 已注册 Mapper 的声明校验；
- 脱敏诊断/Doctor 与可操作 FailureAnalyzer。

`2.1.0-SNAPSHOT` Starter 是已本地验证的开发 artifact，不是稳定版或 Maven Central 发布。当前没有可用的配置属性、Validator、Doctor 或 FailureAnalyzer。

## 🧩 Planned — 2.2.x API 易用性

Starter 契约稳定后的候选项：

- 命名参数；
- `@SmartCount` 与 `@SmartExists`；
- 分页边界与标识符校验；
- 脱敏 SQL Preview；
- 稳定测试支持；
- 可选 JOIN 边界准备。

每项都需要兼容性设计、测试、双语文档和明确实现任务，均不属于 2.0.x。

## 🏗️ Planned — 3.0 结构边界

可能的 major version 工作：

- 物理拆分 core/Spring/Starter/JOIN/demo；
- 让基础公开 `SmartMapper` 不再依赖 MPJ；
- 独立 `SmartJoinMapper` 与可选 JOIN 模块；
- 从 core 移除 demo、Web、MySQL 与 MPJ 实现依赖；
- 迁移适配/指南和经过测试的模块矩阵；
- 仅在模块图稳定后增加 BOM。

这类工作会影响源码/二进制兼容性，不能随意提前到 minor release。

## ⏸️ Deferred

- 在存在经过测试的方言契约前，推迟 PostgreSQL/MariaDB 实现。
- IntelliJ plugin。
- Annotation processor。
- 非 Spring runtime。
- 存储过程抽象。
- 宽泛动态查询 DSL。

替代 MyBatis/XML/Wrapper 或重新实现 MyBatis-Plus 不是产品方向。

## 🚦 顺序门禁

预期顺序：

1. 可信测试与公开文档；
2. 可评审 release foundation 与使用方测试；
3. 完成最小 Starter foundation、精确 head 远程验证与隔离接入规划；
4. 获得明确授权后执行 Lingxi 单方法 pilot 迁移并证明等价；
5. API 易用性改进；
6. major version 模块拆分。

只有用户最新批准的任务可以授权实施。
