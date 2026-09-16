<div align="center">

# SmartORM

**面向 Spring Boot 与 MyBatis-Plus 的可渐进接入、注解驱动增强库。**

[English](README.md) · [简体中文](README.zh-CN.md)

[![Test](https://github.com/forgottenlab/smartorm/actions/workflows/test.yml/badge.svg?branch=main)](https://github.com/forgottenlab/smartorm/actions/workflows/test.yml)
![Java 17](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot 3.5.5](https://img.shields.io/badge/Spring%20Boot-3.5.5-6DB33F?logo=springboot&logoColor=white)
![MyBatis--Plus 3.5.14](https://img.shields.io/badge/MyBatis--Plus-3.5.14-1F6FEB)
[![Apache License 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

</div>

## ✨ 为什么选择 SmartORM？

SmartORM 是一个面向 Spring Boot 与 MyBatis-Plus 的可渐进接入、注解驱动增强库。

它用于减少固定形态的重复 Mapper 代码，同时保留 MyBatis-Plus 的编程模型与原有扩展路径。接入过程可以渐进完成：先选择一个 Mapper、一个方法，保留原实现并行对照，需要时也能回滚，而不必重构整个应用。

> [!NOTE]
> SmartORM 2.0.x 仍是已验证的发布基础。当前 `main` 上的 2.1.x 开发线已增加可本地安装的 Spring Boot Starter，但两个 artifact 都尚未发布到 Maven Central。

## 🚀 当前能力

| 能力 | 状态 | 当前行为 |
|---|---|---|
| 注解驱动 CRUD | ✅ 可用 | `@SmartSelect`、`@SmartInsert`、`@SmartUpdate`、`@SmartDelete` 按 Mapper 方法选择性启用 |
| 分页 | ✅ 可用 | `@SmartPage` 与 `PageResult`；应用需注册 MyBatis-Plus 分页拦截器 |
| DTO / `Map` 映射 | ✅ 可用 | 查询结果推断支持 Entity、`Map`、DTO/VO 与单结果路径 |
| Native SQL 路径 | ✅ 可用 | 渲染后的 SQL 占位符顺序与紧凑绑定参数列表保持一致 |
| 固定 JOIN 声明 | ✅ 可用 | `@SmartJoin` 支持显式或约定推断 `ON`；当前实现基于 MPJ |
| 写操作安全 | ✅ 可用 | 默认阻止没有有效 `WHERE` 的 Smart 更新与删除 |
| 生命周期 Hook | ✅ 可用 | `beforeSmartOperation`、`afterSmartOperation`、`onSmartException` |
| Spring Boot Starter | 🧪 开发中 | `smartorm-spring-boot-starter:2.1.0-SNAPSHOT` 已在本地验证精确注册一个内部 Mapper，同时保留应用对 MyBatis 基础设施和业务 Mapper 扫描的所有权 |

`@SmartQuery` 当前只是未启用的预留注解，不是受支持的执行入口。

## 🎯 适用场景

| 适合使用 | 建议考虑其他方案 |
|---|---|
| 已使用 MyBatis-Plus 的 Spring Boot 应用<br>固定 CRUD 与确定性分页<br>DTO / `Map` 投影和固定 JOIN<br>重复的 Mapper 模板 SQL<br>希望渐进、可回滚接入的团队 | 非 Spring 或响应式应用<br>高度动态或大量数据库专用 SQL<br>存储过程<br>没有维护痛点的成熟复杂 XML/Provider SQL<br>要求当前版本已经提供多数据库方言层的项目 |

## 🚫 它不会替代什么

> [!IMPORTANT]
> SmartORM 不会替代 MyBatis、MyBatis-Plus、`BaseMapper`、Wrapper、XML、Provider SQL 或 MyBatis-Plus-Join。虽然名称中包含 ORM，但它不是完整 ORM 的替代品；原有路径更清晰或能力更强时，应继续保留。

## ⚡ 30 秒示例

SmartORM 继续使用 MyBatis-Plus 实体元数据，只在 Mapper 方法上增加选择性启用的注解：

```java
@TableName("user")
class User {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_name")
    private String userName;

    private Integer status;
    // getters and setters
}

public interface UserMapper extends SmartMapper<User> {

    @SmartSelect(
            fields = {"id", "user_name", "status"},
            where = "status = #{0}",
            orderBy = "id"
    )
    List<User> findByStatus(Integer status);
}

List<User> activeUsers = userMapper.findByStatus(1);
```

`#{0}` 引用 Java 方法的第一个参数。没有 Smart 注解的方法继续使用普通 MyBatis-Plus、Wrapper、XML 或 Provider 行为。当前 Starter 与 Mapper 扫描要求见[快速开始](docs/getting-started.zh-CN.md)。

## 📦 安装方式

Maven Central 尚未发布。克隆并验证仓库后，可把当前 reactor 安装到本地 Maven 仓库：

```powershell
mvn -DskipTests install
```

本地 Spring Boot 使用方随后可以声明开发版 Starter 坐标：

```xml
<dependency>
    <groupId>io.github.forgottenlab</groupId>
    <artifactId>smartorm-spring-boot-starter</artifactId>
    <version>2.1.0-SNAPSHOT</version>
</dependency>
```

Starter 通过 Spring Boot `AutoConfiguration.imports` 发现，应用无需再对 SmartORM 运行组件执行 component scan。Starter 会精确注册内部 `SmartNativeMapper`；应用只扫描自己的 Mapper package，或继续使用 MyBatis Boot 默认发现。Starter 复用无歧义的应用 `SqlSessionTemplate` 或 `SqlSessionFactory`，不会创建 `DataSource`、session 基础设施、事务管理器或全局 Mapper scanner。已有显式/旧式 Native Mapper 注册会被复用以便迁移；session 缺失或歧义时保守 back off。

依赖 `-DskipTests` 前应先单独完成测试门禁；上面的命令只负责安装已经验证的本地构建。如果应用明确继续使用手动集成路径，也可依赖 `io.github.forgottenlab:smartorm:2.1.0-SNAPSHOT`。Core artifact 仍是普通库 JAR，并已排除 demo class、`application.yaml` 与 demo SQL。由于 `SmartMapper` 暴露 MPJ，MyBatis-Plus-Join 仍保持传递依赖；JSqlParser、Spring Web 与 MySQL Connector/J 的边界见[兼容性](docs/compatibility.zh-CN.md)。

## 🛡️ 默认安全

> [!WARNING]
> SmartORM 默认阻止全表更新和删除。Native/JOIN 路径的运行时标量值按顺序绑定；无 JOIN `@SmartSelect` 以及共享的 `@SmartPage` / `@SmartDelete` WHERE 路径会把运行时值保留在 MyBatis-Plus Wrapper 参数状态中。SQL 结构仍来自开发者声明的元数据。有意执行全表操作时，必须在方法级显式设置 `allowFullTable = true`；不存在全局关闭该保护的开关。

该 opt-in 只绕过“缺少有效 `WHERE`”拒绝逻辑，不会补充授权、事务、回滚、输入校验或数据库保护。SmartORM 不宣称能够普遍杜绝 SQL 注入；结构片段必须保持静态，并按[安全指南](docs/safety.zh-CN.md)评审真实边界，包括当前仍采用独立表达式路径的 `@SmartUpdate`。

## 🧪 已验证测试

| 层级 | 范围 | 证据 |
|---|---|---|
| 数据库无关回归 | 无 JOIN Wrapper 绑定、Native SQL 渲染/绑定与写操作安全 | 当前参数绑定 preflight：35/35 通过 |
| Starter 上下文回归 | 内部 Mapper 注册、默认/显式应用扫描、兼容、session 选择与 backoff | 本轮 hardening：无数据库 21/21 通过 |
| 外部 Starter 使用方 | 隔离本地 Maven 仓库、真实 `@EnableAutoConfiguration`、默认业务 Mapper 发现、无内部 package 引用 | 联网 1/1 通过，随后离线 1/1 通过 |
| 历史 MySQL 双轮 | 两个独立创建的 `mysql:9.4.0` volume | 2026-07-20：两轮各 46/46，随后清理 |
| 历史随机顺序探针 | 固定 seed `20260720` | 2026-07-20：46/46，随后清理 |
| 当前参数绑定 MySQL preflight | 全新 `smartorm-parameter-binding` Compose 项目 | 2026-09-16：47/47；真实 `BoundSql` 映射已验证；残留 0/0/0 |
| 历史 MySQL release preflight | 全新 `smartorm-release-preflight` Compose 项目 | 2026-08-12：46/46；0 failures/errors/skips；残留 0/0/0 |
| Starter hardening MySQL 回归 | 全新 `smartorm-starter-mapper-hardening` Compose 项目 | 2026-08-13：46/46；cleanup 残留 0/0/0 |
| 参数绑定修复合并 checkpoint CI | JDK 17、core 35、Starter 21、MySQL 47、package、报告与 cleanup | Checkpoint SHA `093318aa`：精确 `push/main` run `35074543269` 通过；Surefire artifact `10437496726`；Compose container、volume、network 均已移除 |

本地 preflight 还包含编译、package 与 artifact 检查。参数绑定修复合并 checkpoint workflow 已在 SHA `093318aa` 上远程验证 35/21/47 测试分层；历史运行仅作为此前 checkpoint 的证据保留。详见[测试](docs/testing.zh-CN.md)。

## 🧱 架构概览

```text
Annotated Mapper method
  -> Aspect
  -> Handler
  -> Resolver / Meta
  -> Wrapper Builder or SQL Renderer
  -> Executor
  -> MyBatis-Plus / MyBatis
```

SmartORM 现在使用包含 `smartorm` 与合并式 `smartorm-spring-boot-starter` 子模块的父 reactor。Core 模块保留现有公开坐标与源码 package；demo 源码仍位于 core，但会从类发布 artifacts 中排除。可选 JOIN 模块的拆分仍是未来 major version 工作。详见[架构](docs/architecture.zh-CN.md)。

## 📚 文档导航

| 主题 | English | 简体中文 |
|---|---|---|
| 快速开始 | [Guide](docs/getting-started.md) | [指南](docs/getting-started.zh-CN.md) |
| 迁移 | [Guide](docs/migration-from-mybatis-plus.md) | [指南](docs/migration-from-mybatis-plus.zh-CN.md) |
| 安全 | [Guide](docs/safety.md) | [指南](docs/safety.zh-CN.md) |
| 测试 | [Evidence](docs/testing.md) | [证据](docs/testing.zh-CN.md) |
| 架构 | [Current boundaries](docs/architecture.md) | [当前边界](docs/architecture.zh-CN.md) |
| 兼容性 | [Matrix](docs/compatibility.md) | [矩阵](docs/compatibility.zh-CN.md) |
| 路线图 | [Planned direction](docs/roadmap.md) | [规划方向](docs/roadmap.zh-CN.md) |
| 发布检查清单 | [Preflight](docs/release-checklist.md) | [发布预检](docs/release-checklist.zh-CN.md) |

另见[更新日志](CHANGELOG.md)。

## 🗺️ 路线图

当前 2.1.x 开发线已包含最小、经过使用方测试的 Starter foundation。配置元数据、校验、诊断、API 易用性与更深的物理拆分仍是需要单独门禁的路线图事项，不是已交付 API 或发布承诺。详见[路线图](docs/roadmap.zh-CN.md)。

## 🤝 贡献与反馈

欢迎聚焦的 Issue 与 Pull Request。请提供兼容性组合、最小复现和最小相关测试，并明确公开 API、安全、迁移与双语文档影响；正式的贡献与安全策略仍是发布后续事项。

## 📜 许可证

SmartORM 使用 [Apache License 2.0](LICENSE)。当前开发线验证不代表已经完成 release、tag、Maven Central 发布或 GitHub Release。
