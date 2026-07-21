# 兼容性

[English](compatibility.md) | [简体中文](compatibility.zh-CN.md)

## 证据策略

“已验证”表示准确仓库组合完成了明确的本地检查，不代表支持一个版本范围。只有 `pom.xml` 声明而没有执行测试，不足以构成验证证据。

## 已验证组合

| 组件 | 精确版本/环境 | 证据 |
|---|---|---|
| Java | release 17；本地 JDK 17.0.12 | 主源码/测试编译和聚焦测试通过 |
| Maven | 本地 3.9.11；无 Wrapper | `test-compile` 与聚焦测试生命周期通过 |
| Spring Boot | 3.5.5 | Application context 与 46 个数据库测试通过两轮 |
| MyBatis-Plus | 3.5.14 | 当前套件验证 Wrapper CRUD/page 行为 |
| MyBatis-Plus-Join | 1.5.5 | 当前合并 artifact 的 JOIN 测试通过 |
| MySQL Connector/J | 9.4.0 | 两轮可销毁集成测试实际使用 |
| MySQL Server | `mysql:9.4.0` | 两个独立全新 volume 各通过 46 个测试 |

额外固定 seed 的随机类/方法顺序运行也通过全部 46 个数据库测试。

## 未验证

- Java 8–16、Java 18+ 或任意 Java 版本范围。
- Spring Boot 2.x 或除 3.5.5 外的 Boot 版本。
- 其他 MyBatis-Plus 或 MyBatis-Plus-Join 版本。
- PostgreSQL、MariaDB、Oracle、SQL Server 或其他 MySQL 版本。
- Gradle 使用方构建。
- Maven Central 已发布制品接入。
- 使用 Starter 的外部 Spring Boot 应用。
- 远程 GitHub Actions 运行。

禁止仅根据“能够编译”推断支持。

## 当前 artifact 限制

- 项目是单体 artifact，同时包含库、Spring 集成、demo、MPJ、MySQL 与 Web 关注点。
- `SmartMapper` 的公开父接口包含 `MPJBaseMapper`。
- 当前运行需要显式组件扫描和 Mapper 扫描。
- 不存在 Maven Wrapper、Starter、AutoConfiguration、配置元数据或使用方 sample。
- 本地离线 package 因缓存缺少 `maven-jar-plugin:3.4.2` 而阻塞。

## 公开行为兼容性

当前应视为兼容性敏感的行为包括：

- 注解名称、属性、默认值和数字占位符语法；
- `SmartMapper` 父接口、便捷方法和生命周期 Hook；
- Entity/Map/DTO/单结果/分页结果推断；
- JOIN 结构、别名、推断/显式 `ON` 与 Native 绑定顺序；
- fail-closed 空 `WHERE` 行为和方法级 `allowFullTable` opt-in。

未来模块拆分不得静默移动或删除已经暴露的类型。破坏 Mapper 继承或 artifact 坐标需要 major version 迁移路径。

## 扩展矩阵规则

向已验证表增加版本或数据库之前：

1. 构建干净的使用方项目或类发布制品；
2. 运行数据库无关回归；
3. 启动 Application Context；
4. 运行对应可销毁数据库套件；
5. 在 CI 和本文记录准确版本及限制。
