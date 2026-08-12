# 架构

[English](architecture.md) · [简体中文](architecture.zh-CN.md)

> **摘要：** SmartORM 在兼容的 `smartorm` artifact 中保留已验证的 2.0.x 分层运行链路。2.1.x 开发线增加 Maven reactor 与最小合并式 Spring Boot Starter，但没有提前执行规划中的 3.0 语义拆分。

## 🧭 当前范围

根项目现在是 `io.github.forgottenlab:smartorm-parent:2.1.0-SNAPSHOT` 聚合器，包含两个子模块：

- `io.github.forgottenlab:smartorm:2.1.0-SNAPSHOT`：兼容的合并式库，Java package 与公开 API 未改变；
- `io.github.forgottenlab:smartorm-spring-boot-starter:2.1.0-SNAPSHOT`：合并式 Starter 与自动配置 artifact。

库代码、Spring 集成、MPJ、MySQL 支持、测试和 demo 源码仍共存于 `smartorm` 子模块；这不是未来 core/Spring/JOIN 语义拆分。Core 构建继续生成普通主 JAR、sources 与 Javadoc JAR。Demo class、`application.yaml` 和 demo SQL 仍保留在源码中，但会从类发布 artifacts 排除。

## 🔄 运行链路

```text
Annotated Mapper method
  -> SmartAnnotationAspect
  -> SmartHandlerRegistry / SmartXXXHandler
  -> SmartXXXResolver / Meta
  -> SmartWrapperBuilder or SmartXXXSqlRenderer
  -> SmartMapperExecutor or SmartNativeExecutor
  -> MyBatis-Plus / MyBatis
```

`SmartMapper` 的生命周期 Hook 在 Aspect 路径前后调用：`beforeSmartOperation`、`afterSmartOperation` 和 `onSmartException`。

## 📦 主要 Package

| Package | 当前职责 |
|---|---|
| `annotations` | 公开 Smart 注解与枚举 |
| `aspect` | AOP 拦截与生命周期 Hook 编排 |
| `handler` | 注解分发与执行协调 |
| `resolver`, `resolver.meta` | 注解/方法元数据解析 |
| `builder` | MyBatis-Plus Wrapper 构建与写操作安全检查 |
| `sql.renderer`, `sql.model`, `sql.provider` | Native SQL 渲染和 MyBatis Provider 路径 |
| `executor` | Wrapper 与 Native 执行适配 |
| `mapper` | `SmartMapper` 与内部 `SmartNativeMapper` |
| `support`, `util` | JOIN 推断、映射、元数据、表达式和辅助能力 |
| `demo` | 内置应用、实体、Mapper 与 MyBatis 配置 |
| Starter `autoconfigure` | `SmartOrmAutoConfiguration` 与 package-private 运行时 Bean registrar |

## 🔎 无 JOIN 查询路径

没有 JOIN 元数据的 `@SmartSelect` 和 `@SmartPage` 会构建 MyBatis-Plus `QueryWrapper`，并通过 `SmartMapperExecutor` 执行。返回推断支持实体列表、Map 列表、DTO 列表和单对象 AUTO 行为。分页使用 MyBatis-Plus `Page`，需要注册分页拦截器。

## 🔗 JOIN 与 Native 查询路径

带 `@SmartJoin` 的查询会渲染 SQL 和有序紧凑参数列表，再通过 `SmartNativeMapper` 与 `SmartNativeExecutor` 执行。

- 显式 `ON` 保持为开发者编写的结构；其中占位符属于绑定值。
- 缺少 `ON` 时使用命名约定推断，除非已经初始化 `JdbcMetaProvider`。
- 当前仓库没有连接 `JdbcMetaProvider`，因此命名约定是已验证默认路径。
- Native/JOIN 结果支持 Map 与 DTO；Native `ENTITY_LIST` 会被拒绝。

## ✍️ 写操作路径

Insert 会把 fields/values 解析为实体并委托 MyBatis-Plus insert。Update 与 Delete 构建 Wrapper，通过 `SmartMapperExecutor` 执行。

在 Update/Delete 执行前，`SmartMutationSafetyGuard` 会检查最终谓词 segment。没有有效谓词时默认拒绝，除非方法显式声明 `allowFullTable = true`。

## 🔧 当前接入要求

直接使用 `smartorm` core artifact 时保留原有手动路径：Spring component scan 包含 `io.github.forgottenlab.smartorm`，Mapper 扫描同时包含应用 Mapper 和 `io.github.forgottenlab.smartorm.mapper`。

2.1.x 开发版 Starter 使用以下激活链路：

```text
AutoConfiguration.imports
  -> SmartOrmAutoConfiguration
  -> BeanFactoryPostProcessor registrar
  -> 复用或精确注册 canonical smartNativeMapper
  -> SmartNativeExecutor + 五个 handler + registry + aspect
```

Starter 不扫描 SmartORM component 或应用 Mapper package。应用只扫描自己的 Mapper，或继续使用 MyBatis Boot 默认发现。MyBatis registry post-processor 完成后，registrar 会复用一个兼容 `SmartNativeMapper`，否则精确注册一个 canonical `MapperFactoryBean`，因此不会抑制应用 scanner 的决策。没有声明硬编码的自动配置 `before`/`after` 顺序。

激活刻意采用保守策略。Starter 优先使用唯一或唯一 `@Primary` 的应用 `SqlSessionTemplate`，其次使用唯一或唯一 `@Primary` 的 `SqlSessionFactory`；session 缺失/歧义、内部 Mapper 注册不兼容、已存在 `SmartAnnotationAspect` 或运行时 Bean 名称冲突时，完整 Bean 图会 back off。Starter 只注册已知内部 Mapper，不注册 scanner，也不创建 `DataSource`、session 基础设施、事务管理器、分页拦截器或 `JdbcMetaProvider`；bootstrap 不连接数据库。

配置属性/元数据、声明校验、FailureAnalyzer、启动 Validator 与 Doctor 尚未实现。

## 🧩 当前依赖与 API 边界

- `SmartMapper<T>` 公开继承 `BaseMapper<T>` 与 `MPJBaseMapper<T>`。
- 由于 `SmartMapper` 暴露 `MPJBaseMapper`，MyBatis-Plus-Join 仍保持传递依赖。
- JSqlParser 为 optional；Spring Web 与 MySQL Connector/J 为 runtime + optional，不再是使用方必须继承的传递依赖。
- Demo 代码与运行集成仍和 core 语义位于 `smartorm` 子模块，但 demo class/configuration/SQL 已从发布 artifacts 排除。
- 本地安装的 core artifact 保留既有外部使用方 2 个 smoke tests 证据。开发版 Starter 另外通过 1 个外部 Spring Boot 使用方 smoke 及离线复跑；Maven Central 消费与真实项目接入仍未验证。
- `SmartQuery` 是公开但未启用的注解。
- Wrapper 与 Native 路径没有完全相同的标量绑定实现。

这些属于兼容性约束，不是可以忽略的实现细节。

## 🗺️ 未来方向——仅规划

路线图现在区分已在本地实现的 Starter foundation 与剩余产品工作：

- 2.1.x：远程验证 Starter foundation，为单个真实项目模块制定接入计划，再独立设计配置、校验与诊断。
- 2.2.x：完成兼容性设计后的聚焦 API 易用性改进。
- 3.0：物理拆分 core/Spring/JOIN/demo，并建立 `SmartMapper`/`SmartJoinMapper` 边界。

后续项仍是计划，不是已交付 API、坐标、日期或实现授权。详见[路线图](roadmap.zh-CN.md)。
