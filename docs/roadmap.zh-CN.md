# 路线图

[English](roadmap.md) | [简体中文](roadmap.zh-CN.md)

本路线图用于说明方向与质量门禁，不代表实现授权、发布承诺或日期承诺。

## Current — 2.0.x release foundation

目标：

- 可信的数据库无关测试和可销毁 MySQL 测试；
- 隔离测试配置；
- 本地 CI workflow 设计；
- 准确的英文/中文公开文档；
- 真实说明迁移、安全、兼容性、测试与发布预检；
- 在不广泛重设 API 的前提下修复正确性问题。

当前证据：

- 28 个数据库无关测试在本地通过。
- 两个独立全新 MySQL 9.4.0 环境各通过 46/46 测试并清理状态。
- 一轮随机顺序 46 测试通过。
- CI workflow 已存在，但未远程运行。
- Package、独立许可证与发布门禁仍未闭环。

## Planned — 2.1.x 接入体验

方向如下，但必须由独立获批任务和使用方测试驱动：

- Spring Boot Starter；
- 与 MyBatis-Plus 集成而不是竞争的 AutoConfiguration；
- 聚焦的配置元数据；
- 已注册 Mapper 的声明校验；
- 脱敏诊断/Doctor 与可操作 FailureAnalyzer。

当前不存在可用的 Starter 坐标、属性、AutoConfiguration 类或诊断 API。

## Planned — 2.2.x API 易用性

Starter 契约稳定后的候选项：

- 命名参数；
- `@SmartCount` 与 `@SmartExists`；
- 分页边界与标识符校验；
- 脱敏 SQL Preview；
- 稳定测试支持；
- 可选 JOIN 边界准备。

每项都需要兼容性设计、测试、双语文档和明确实现任务，均不属于 2.0.x。

## Planned — 3.0 结构边界

可能的 major version 工作：

- 物理拆分 core/Spring/Starter/JOIN/demo；
- 让基础公开 `SmartMapper` 不再依赖 MPJ；
- 独立 `SmartJoinMapper` 与可选 JOIN 模块；
- 从 core 移除 demo、Web、MySQL 与 MPJ 实现依赖；
- 迁移适配/指南和经过测试的模块矩阵；
- 仅在模块图稳定后增加 BOM。

这类工作会影响源码/二进制兼容性，不能随意提前到 minor release。

## Deferred

- 在存在经过测试的方言契约前，推迟 PostgreSQL/MariaDB 实现。
- IntelliJ plugin。
- Annotation processor。
- 非 Spring runtime。
- 存储过程抽象。
- 宽泛动态查询 DSL。

替代 MyBatis/XML/Wrapper 或重新实现 MyBatis-Plus 不是产品方向。

## 顺序门禁

预期顺序：

1. 可信测试与公开文档；
2. 可评审 release foundation 与使用方测试；
3. 在兼容 artifact 上实现 Starter；
4. API 易用性改进；
5. major version 模块拆分。

只有用户最新批准的任务可以授权实施。
