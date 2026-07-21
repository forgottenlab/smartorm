# SmartORM

[English](README.md) | [简体中文](README.zh-CN.md)

> SmartORM 是一个面向 Spring Boot 与 MyBatis-Plus 的可渐进接入、注解驱动增强库。

SmartORM 用于减少固定形态 CRUD、分页、结果映射和 JOIN 查询中的重复 Mapper 代码，同时保留 MyBatis-Plus 的原有编程模型。接入可以逐步完成：现有 `BaseMapper`、Wrapper、XML、Provider 和 MyBatis-Plus-Join 代码可以继续使用，只让选定的 Mapper 和方法接入 SmartORM。

## 项目状态

SmartORM 2.0.x 当前处于源码优先的 release foundation 阶段，并不是已经发布的 Spring Boot Starter。

- 仓库使用 Java 17 和 Spring Boot 3.5.5 构建。
- 数据库无关回归测试与可销毁 MySQL 9.4 集成测试已经在本地建立。
- 仓库存在 GitHub Actions 工作流，但尚未声称远程 CI 已通过。
- Maven Central 发布、面向使用者的自动装配和多模块结构仍属于未来工作。

当前仓库适合评估、本地源码模块接入和参与贡献。不要仅根据 `pom.xml` 中的坐标判断公共制品已经可用。

## SmartORM 是什么

SmartORM 面向已经使用 Spring Boot 与 MyBatis-Plus 的团队，提供聚焦的数据访问增强。声明式 Mapper 注解会经过一条结构化运行链路：

```text
Mapper method
  -> Aspect
  -> Handler
  -> Resolver
  -> Builder or SQL Renderer
  -> Executor
```

当你希望减少固定形态的重复 Mapper 代码，同时继续保留 MyBatis 和 MyBatis-Plus 已有扩展路径时，SmartORM 更适合使用。

## SmartORM 不是什么

SmartORM 不是：

- ORM 替代品；
- MyBatis 或 MyBatis-Plus 替代品；
- `BaseMapper`、Wrapper、XML 或 Provider SQL 替代品；
- MyBatis-Plus-Join 替代品；
- 通用动态 SQL 语言；
- 当前已经发布的 Spring Boot Starter。

复杂动态 SQL、存储过程、数据库专用语句，以及已经稳定且没有维护痛点的 XML/Provider 代码，应继续保留原有实现，除非迁移具备明确价值。

## 已实现能力

- 注解驱动的查询、插入、更新、删除和分页操作。
- Entity、`Map`、DTO/VO、单结果和 `PageResult` 路径。
- 使用推断或显式 `ON` 谓词的固定 JOIN 声明。
- 保持标量参数顺序的 Native SQL 渲染与绑定。
- `SmartMapper` 便捷方法和生命周期 Hook。
- 默认拒绝最终没有有效 `WHERE` 谓词的 `@SmartUpdate` 与 `@SmartDelete` 操作。

`@SmartQuery` 当前只是未启用的预留注解，不是受支持的执行入口。

## 快速开始

### 1. 使用当前源码模块

公共 Maven Central 制品尚未得到验证。评估时请把本仓库作为 Maven 项目打开，或把当前源码作为本地模块引入。当前接入需要与 demo 配置等价的组件扫描和 Mapper 扫描；[快速开始指南](docs/getting-started.zh-CN.md)给出了准确配置。

### 2. 定义一个按需接入的 Mapper 方法

```java
public interface UserMapper extends SmartMapper<User> {

    @SmartSelect(
            fields = {"id", "user_name", "status"},
            where = "status = #{0}",
            orderBy = "id"
    )
    List<User> findByStatus(Integer status);
}
```

没有 Smart 注解的 Mapper 方法仍然使用普通 MyBatis-Plus、XML、Wrapper 或 Provider 实现。

### 3. 验证声明

增加一个能够自行创建 fixture 的确定性集成测试，并验证结果行、排序、null 行为和数据库副作用。数字占位符从 `#{0}` 开始，引用 Java 方法参数下标。

当前扫描配置、实体示例、测试环境和常见错误见[快速开始](docs/getting-started.zh-CN.md)。

## 当前安装现实

当前经过验证的使用方式是源码/本地模块方式。仓库声明的坐标为：

```text
io.github.forgottenlab:smartorm:2.0.0
```

这些坐标用于描述项目，不代表 Maven Central 发布已经完成。未来 Starter 只存在于路线图中，当前构建文件不应添加尚不存在的 Starter。

## 从 MyBatis-Plus 迁移

迁移是渐进且可回滚的：

1. 保留现有依赖和 Mapper 行为；
2. 只接入一个 Mapper；
3. 只迁移一个测试充分的方法；
4. 只替换重复的固定形态 SQL；
5. 完成对比和回滚检查后，再按应用模块扩大范围。

现有 `BaseMapper`、Wrapper、XML、Provider 和 MyBatis-Plus-Join 代码继续有效。详见[从 MyBatis-Plus 迁移](docs/migration-from-mybatis-plus.zh-CN.md)。

## 安全

当最终生成的 Wrapper 不包含有效 `WHERE` 谓词时，`@SmartUpdate` 与 `@SmartDelete` 会拒绝执行。有意执行全表操作时，必须在方法级显式声明：

```java
@SmartDelete(allowFullTable = true)
int deleteAllRows();
```

该 opt-in 只绕过空 `WHERE` 安全检查，不会放宽其他解析、SQL、事务、授权或数据库保护。使用写注解前请阅读完整[安全指南](docs/safety.zh-CN.md)。

## 测试证据

当前精确仓库基线的本地证据：

- 28 个数据库无关测试：通过。
- 46 个 MySQL 集成测试使用可销毁 `mysql:9.4.0` 环境，从两个独立新建 volume 各通过一次。
- 额外固定 seed 的随机类/方法顺序运行：46 个测试通过。
- 每次运行后，任务容器、volume 和 network 均已删除。

`.github/workflows/test.yml` 与这套基线一致，但尚未经过远程 GitHub Actions 运行确认。详见[测试](docs/testing.zh-CN.md)。

## 文档

- [快速开始](docs/getting-started.zh-CN.md)
- [从 MyBatis-Plus 迁移](docs/migration-from-mybatis-plus.zh-CN.md)
- [安全](docs/safety.zh-CN.md)
- [测试](docs/testing.zh-CN.md)
- [架构](docs/architecture.zh-CN.md)
- [兼容性](docs/compatibility.zh-CN.md)
- [路线图](docs/roadmap.zh-CN.md)
- [发布检查清单](docs/release-checklist.zh-CN.md)
- [更新日志](CHANGELOG.md)

每份公开文档顶部均提供语言切换。

## 兼容性摘要

当前验证点是 Java 17、Spring Boot 3.5.5、MyBatis-Plus 3.5.14、MyBatis-Plus-Join 1.5.5 和 MySQL 9.4.0。这只是一个已测试组合，不是宽泛支持范围。PostgreSQL、MariaDB、其他 Java/Spring 版本、Gradle 使用者和已发布制品接入仍未验证。

准确证据与限制见[兼容性矩阵](docs/compatibility.zh-CN.md)。

## 路线图边界

Starter 自动装配规划在 2.1.x，API 易用性规划在 2.2.x，物理模块拆分规划在 3.0。这些是方向，不是已经交付的功能、日期承诺或实现授权。详见[路线图](docs/roadmap.zh-CN.md)。

## 许可证与发布准备度

POM 声明 Apache License 2.0。公开发布前仍需补齐仓库级独立许可证文件和剩余制品/发布门禁，进度记录在[发布检查清单](docs/release-checklist.md)。
