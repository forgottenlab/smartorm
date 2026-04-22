# Changelog

本文件用于统一记录 SmartORM / MyBatis-Plus-Helper 的版本变更历史。

## [2.0.0] - 2026-04-22

### Added
- 新增基于 `AOP + Handler` 的注解执行模型，使 Mapper 方法可像原生方法一样直接声明并调用。
- 新增 `SmartPage` 分页能力。
- 新增 `SmartJoin` 表连接注解及 `JoinType` 枚举。
- 新增 `SelectResultType`，支持查询结果类型的显式声明与自动推断。
- 新增 `SmartNativeExecutor` 与原生 SQL 渲染链路，用于支持 JOIN 与复杂查询场景。
- 新增 `QuerySemantic` 抽象，用于统一 `SelectMeta` 与 `PageMeta` 的查询语义。
- 新增 `RenderedSql` 与 `RenderedPageSql` 作为 SQL 渲染结果模型。
- 新增 `SmartBeanMapper`，支持将查询结果映射到 DTO / VO / Java Bean。
- 新增 `JdbcMetaProvider` 与 `JoinInferenceEngine`，用于支撑 JOIN 条件推断与数据库元数据读取。
- 新增生命周期 Hook 机制，支持 `beforeSmartOperation`、`afterSmartOperation` 与 `onSmartException`。

### Changed
- 项目包名统一迁移为 `io.github.forgottenlab.smartorm`。
- 项目结构统一整理为 `annotations / aspect / handler / resolver / builder / executor / mapper / model / exception / sql / support / util / demo`。
- SQL 模块统一整理为 `sql.provider`、`sql.renderer`、`sql.model`。
- `support` 与 `util` 的职责边界重新划分，相关辅助类迁移到更明确的子包下。
- 示例代码统一迁移至 `demo` 包下，并拆分为 `config`、`dto`、`entity`、`mapper` 等子包。
- 全项目统一源码注释头模板，`@VersionHistory` 改为统一引用 `CHANGELOG.md`。
- 清理各层开发期注释、TODO 与调试输出，使源码更适合作为发布版本。
- 新增 `docs` 文档目录，补充架构说明、查询规划说明以及数据库与测试使用说明。

### Fixed
- 修复旧包名 `com.smartorm` 残留导致的编译错误。
- 修复多个类迁移后 import 路径不一致的问题。
- 修复 `SmartAnnotationAspect` 中注解识别包路径与当前实际包名不一致的问题。
- 修复 demo 启动与配置层的 Mapper 扫描职责不一致问题。
- 修复 `SmartBaseResolver.resolveJoins(...)` 对显式 `on` 条件的标记逻辑，使 JOIN 渲染阶段能够正确区分自动推断与用户显式指定的 ON 条件。
- 修复 `SmartInsertUtil` 与 `SmartExpressionUtil` 注释中的错误占位符描述。
- 修复 `SmartReflectionUtil` 中重复作者声明与弃用标记不一致问题。

### Refactored
- 将查询链路进一步明确为：`Aspect -> Handler -> Resolver -> Builder / SQL Renderer -> Executor`。
- 将 `Select` 与 `Page` 的公共 SQL 渲染逻辑抽取到 `SmartBaseSqlRenderer`。
- 将 `Wrapper` 构建过程中的公共逻辑抽取到 `SmartWrapperCommonBuilder`。
- 将查询语义公共抽象收口到 `QuerySemantic`。

### Notes
- `SmartQuery` 当前作为统一查询抽象的预留注解存在，暂未作为主执行入口启用。
- 当前 JOIN 中显式 `on` 条件的处理方式已可运行，但后续版本将继续优化其 SQL 结构表达方式与安全策略。
- 2.0.0 的重点是执行模型升级、包结构规范化，以及 JOIN / Native SQL 能力的初步整合。
- 当前工程以源码引入与本地运行验证为主；关于依赖隔离与 Starter 形态的进一步拆分，将在后续版本继续推进。

---

## [1.x] - Historical Notes

### Notes
- 1.x 阶段主要以 `SmartMapper` 内部 default 方法驱动执行为主。
- 2.0.0 开始，项目正式转向更清晰的分层执行模型与注解驱动架构。
