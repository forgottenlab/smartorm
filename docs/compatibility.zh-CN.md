# 兼容性

[English](compatibility.md) · [简体中文](compatibility.zh-CN.md)

> **摘要：** 兼容性声明只覆盖经过实际验证的精确版本组合。这是验证点，不是承诺的版本范围。

## 🧾 证据策略

“已验证”表示准确仓库组合完成了明确的本地检查，不代表支持一个版本范围。只有 `pom.xml` 声明而没有执行测试，不足以构成验证证据。

## ✅ 已验证组合

| 组件 | 精确版本/环境 | 证据 |
|---|---|---|
| Java | release 17；本地 JDK 17.0.12 | 主源码/测试编译和聚焦测试通过 |
| Maven | 本地 3.9.11；无 Wrapper | `test-compile`、聚焦测试与 package 生命周期通过 |
| Spring Boot | 3.5.5 | Application context 与 46 个数据库测试在本轮 2026-08-12 preflight 通过，并保留 2026-07-20 两轮证据 |
| MyBatis-Plus | 3.5.14 | 当前套件验证 Wrapper CRUD/page 行为 |
| MyBatis-Plus-Join | 1.5.5 | 当前合并 artifact 的 JOIN 测试通过 |
| MySQL Connector/J | 9.4.0 | 本轮与保留的可销毁集成测试均实际使用 |
| MySQL Server | `mysql:9.4.0` tag | 本轮全新 volume 通过 46/46 并清理至 0/0/0 残留；另保留 2026-07-20 两轮独立证据 |
| 本地 artifact 集 | main/sources/Javadoc/POM | 独立测试验证后，联网 `-DskipTests` clean package 与 artifact 检查通过 |
| 外部使用方 smoke | 隔离本地 Maven 仓库 | 2 个测试联网通过并再次离线复跑通过 |

额外固定 seed 的随机类/方法顺序运行也在 2026-07-20 通过全部 46 个数据库测试。本轮 2026-08-12 preflight 中，用户手动启动后的 Docker Desktop Linux daemon 正常；全新 `smartorm-release-preflight` 环境通过 46/46，清理后项目 container、network 与 volume 均为 0。随后 GitHub Actions run `30647688231` 验证了准确 Foundation SHA `c13426d`：远程 28/28、MySQL 46/46、package、报告上传与 cleanup 全部通过。

## 🚫 未验证

- Java 8–16、Java 18+ 或任意 Java 版本范围。
- Spring Boot 2.x 或除 3.5.5 外的 Boot 版本。
- 其他 MyBatis-Plus 或 MyBatis-Plus-Join 版本。
- PostgreSQL、MariaDB、Oracle、SQL Server 或其他 MySQL 版本。
- Gradle 使用方构建。
- Maven Central 已发布制品接入。
- Maven Central 签名、上传、仓库接收或 namespace 校验。
- 使用 Starter 的外部 Spring Boot 应用。
- 位级可重复 artifact 或不可变 runner/container 输入。

禁止仅根据“能够编译”推断支持。

## 📦 当前 Artifact 限制

- 仓库仍是一个 Maven 单模块，源码同时包含库、Spring 集成、demo、MPJ、MySQL 与 Web 关注点。
- 发布 artifacts 排除 demo class、`application.yaml` 与 demo SQL；demo 仍保留在仓库源码树中。
- `SmartMapper` 的公开父接口包含 `MPJBaseMapper`。
- MyBatis-Plus-Join 保持传递依赖。JSqlParser 为 optional；Spring Web 与 MySQL Connector/J 为 runtime + optional。
- 当前运行需要显式组件扫描和 Mapper 扫描。
- 不存在 Maven Wrapper、Starter、AutoConfiguration、配置元数据或仓库内维护的使用方 sample。
- 本地类发布 artifact 路径已通过 package/install/consumer 检查，但公共仓库路径尚未验证。

## 🔒 公开行为兼容性

当前应视为兼容性敏感的行为包括：

- 注解名称、属性、默认值和数字占位符语法；
- `SmartMapper` 父接口、便捷方法和生命周期 Hook；
- Entity/Map/DTO/单结果/分页结果推断；
- JOIN 结构、别名、推断/显式 `ON` 与 Native 绑定顺序；
- fail-closed 空 `WHERE` 行为和方法级 `allowFullTable` opt-in。

未来模块拆分不得静默移动或删除已经暴露的类型。破坏 Mapper 继承或 artifact 坐标需要 major version 迁移路径。

## 🧪 扩展矩阵规则

向已验证表增加版本或数据库之前：

1. 构建干净的使用方项目或类发布制品；
2. 运行数据库无关回归；
3. 启动 Application Context；
4. 运行对应可销毁数据库套件；
5. 在 CI 和本文记录准确版本及限制。
