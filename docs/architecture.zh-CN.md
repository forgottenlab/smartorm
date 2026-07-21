# 架构

[English](architecture.md) | [简体中文](architecture.zh-CN.md)

本文严格区分当前 2.0.x 实现和未来架构。规划中的模块与 Starter 类型当前均不可用。

## 当前范围

SmartORM 当前是一个 Maven `jar` artifact，其中同时包含库代码、Spring 集成、demo 应用/配置、MPJ 集成、MySQL 驱动/资源和测试。公开入口仍是源码/本地模块接入；不存在 AutoConfiguration 或物理子模块。

## 运行链路

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

## 主要 package

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

## 无 JOIN 查询路径

没有 JOIN 元数据的 `@SmartSelect` 和 `@SmartPage` 会构建 MyBatis-Plus `QueryWrapper`，并通过 `SmartMapperExecutor` 执行。返回推断支持实体列表、Map 列表、DTO 列表和单对象 AUTO 行为。分页使用 MyBatis-Plus `Page`，需要注册分页拦截器。

## JOIN 与 Native 查询路径

带 `@SmartJoin` 的查询会渲染 SQL 和有序紧凑参数列表，再通过 `SmartNativeMapper` 与 `SmartNativeExecutor` 执行。

- 显式 `ON` 保持为开发者编写的结构；其中占位符属于绑定值。
- 缺少 `ON` 时使用命名约定推断，除非已经初始化 `JdbcMetaProvider`。
- 当前仓库没有连接 `JdbcMetaProvider`，因此命名约定是已验证默认路径。
- Native/JOIN 结果支持 Map 与 DTO；Native `ENTITY_LIST` 会被拒绝。

## 写操作路径

Insert 会把 fields/values 解析为实体并委托 MyBatis-Plus insert。Update 与 Delete 构建 Wrapper，通过 `SmartMapperExecutor` 执行。

在 Update/Delete 执行前，`SmartMutationSafetyGuard` 会检查最终谓词 segment。没有有效谓词时默认拒绝，除非方法显式声明 `allowFullTable = true`。

## 当前接入要求

Spring 组件扫描必须包含 `io.github.forgottenlab.smartorm`，Mapper 扫描必须同时包含应用 Mapper 和 `io.github.forgottenlab.smartorm.mapper`。demo 通过 `scanBasePackages` 和 `@MapperScan` 实现这一点。

2.0.x 当前没有面向使用方的 Starter、配置元数据、FailureAnalyzer、启动校验器或 Doctor。

## 当前依赖与 API 边界

- `SmartMapper<T>` 公开继承 `BaseMapper<T>` 与 `MPJBaseMapper<T>`。
- MySQL 与 Spring Web 依赖当前属于单体 artifact，没有隔离为 demo 依赖。
- demo 代码和运行集成与核心语义位于同一 artifact。
- `SmartQuery` 是公开但未启用的注解。
- Wrapper 与 Native 路径没有完全相同的标量绑定实现。

这些属于兼容性约束，不是可以忽略的实现细节。

## 未来方向——仅规划

路线图提出：

- 2.1.x：经过使用方测试的 Spring Boot Starter、AutoConfiguration、声明校验和诊断。
- 2.2.x：完成兼容性设计后的聚焦 API 易用性改进。
- 3.0：物理拆分 core/Spring/JOIN/demo，并建立 `SmartMapper`/`SmartJoinMapper` 边界。

本文没有实现上述模块、坐标、属性或 API。详见[路线图](roadmap.zh-CN.md)。
