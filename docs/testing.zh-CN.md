# 测试

[English](testing.md) · [简体中文](testing.zh-CN.md)

> **摘要：** SmartORM 使用分层证据。编译、数据库无关、MySQL、package 与远程 CI 结果证明不同边界，不能互相替代或混报。

## 🧭 测试分层

```text
Unit and compile checks
  -> Database-independent regression tests
  -> MySQL integration tests
  -> CI verification
```

### ✅ 单元与编译检查

`test-compile` 验证主源码和测试源码能否使用当前 Java/Maven 依赖缓存编译。它不能证明数据库行为。

```powershell
mvn -o test-compile
```

### 🧪 数据库无关回归测试

当前 28 个测试无需 Spring Boot、MySQL、网络服务或测试顺序，直接覆盖 Native SQL 渲染/绑定与空 `WHERE` 写操作安全：

```powershell
mvn -o -pl smartorm '-Dtest=SmartMutationWhereSafetyTest,SmartNativeSqlRendererTest' test
```

覆盖范围包括正常/乱序/稀疏/重复占位符、显式/推断 JOIN 谓词、别名、分页渲染、越界参数、空/禁用写谓词、显式全表 opt-in 和输入不可变性。

### 🐬 MySQL 集成测试

当前 9 个 Spring Boot 测试类执行 46 个测试，覆盖查询、结果映射、插入、更新、删除、分页、JOIN、`SmartMapper` 辅助方法和生命周期 Hook。每个类都使用 `test` profile 和事务回滚。

本轮 2026-08-12 preflight 使用用户手动启动后的 Docker Desktop Linux daemon，并创建全新的 `smartorm-release-preflight` Compose 项目。46 个测试全部通过，0 failure、0 error、0 skip；强制 cleanup 后项目 container、network 与 volume 均为 0。2026-07-20 双轮与随机顺序探针仍作为独立历史证据保留。

集成环境由 `docker-compose.test.yml` 定义：

- 固定镜像 tag `mysql:9.4.0`；
- Compose 项目自有、可删除 volume；
- 仅绑定 loopback；
- 测试前健康检查；
- `utf8mb4` 与明确时区/SQL mode；
- 权威 schema 以只读方式挂载到新容器。

### ☁️ CI 验证

当前 `.github/workflows/test.yml` 选择 JDK 17，启用 Maven 依赖缓存，编译测试，运行 28 个数据库无关 core 测试与 8 个 Starter 上下文测试，启动 Compose MySQL，运行准确的 46 个数据库测试，不重复测试地执行 package，上传 Surefire 报告，并定义 `if: always()` cleanup 步骤。

Foundation SHA `c6e8c8b` 的精确 `push/main` 运行已通过只读方式核验为 run `31574822720`。它通过了 28 个数据库无关测试、46 个 MySQL 测试、package、Surefire 上传与 always-cleanup 步骤。该运行早于 Starter feature commits；当前 feature checkpoint 需在获得明确 push 授权后单独进行远程运行。Runner OS/工具版本、Action major tag、MySQL image tag 与 Docker Compose 并未作为整体固定到不可变 digest，因此这不构成位级可重复声明。README badge 表示实时 workflow 状态，不能替代精确 SHA 证据。

### 🧩 Starter 测试

聚焦的自动配置套件不依赖数据库：

```powershell
mvn -o -pl smartorm-spring-boot-starter -am '-Dtest=SmartOrmAutoConfigurationTest' '-Dsurefire.failIfNoSpecifiedTests=false' test
```

当前套件 8/8 通过，覆盖 imports 发现、精确 8 Bean 运行图、应用自有 `@MapperScan`、缺类/缺 Mapper backoff、用户 Bean backoff、基础设施非所有权，以及 bootstrap 时无 Mapper 交互。

一个 Boot 3.5.5 外部使用方在忽略的 `target/` 下生成，并且只解析本地安装的 `smartorm-spring-boot-starter:2.1.0-SNAPSHOT`。其单个 `@SpringBootTest` 在从项目已配置仓库获取所需 Surefire provider 后通过，随后离线复跑也通过。它使用应用自有 `@MapperScan` 与合成 `SqlSessionFactory`，没有连接数据库，也不是仓库维护的 sample project。

## ✅ 环境要求

- JDK 17。
- 本地观察基线使用 Maven 3.9.x；仓库没有 Maven Wrapper。
- 集成测试需要支持 Compose 且 Linux container daemon 正常运行的 Docker。
- 主机端口 `13316`，或通过 `SMARTORM_TEST_DB_PORT` 指定其他 loopback 端口。

## 🔐 测试专用环境变量

只使用当前进程的值，禁止复用开发或生产数据库凭据：

```powershell
$env:SMARTORM_TEST_ROOT_PASSWORD = 'REDACTED'
$env:SMARTORM_TEST_DB_USERNAME = 'REDACTED'
$env:SMARTORM_TEST_DB_PASSWORD = 'REDACTED'
$env:SMARTORM_TEST_DB_PORT = '13316'
```

Spring datasource 读取测试 username/password 以及可选 host/port；root password 只由 Compose 用于初始化和健康检查可销毁服务。两条路径都不会回退到主 datasource 凭据。

## 🐬 运行一次全新 MySQL 套件

使用唯一项目名，并始终删除对应 volume：

```powershell
docker compose -p smartorm-it-run1 -f docker-compose.test.yml config --quiet
docker compose -p smartorm-it-run1 -f docker-compose.test.yml up -d --wait
mvn -o -pl smartorm '-Dtest=SmartMapperHookTest,SmartMapperCoreTest,MapperUpdateTest,MapperSelectTest,MapperSelectResultTypeTest,MapperPageTest,MapperJoinTest,MapperInsertTest,MapperDeleteTest' test
docker compose -p smartorm-it-run1 -f docker-compose.test.yml down -v --remove-orphans
```

完成 `down -v` 后，检查选定 Compose 项目不再残留 container、volume 或 network。

## 🔁 可重复性检查

使用另一个项目名（例如 `smartorm-it-run2`）重复完整流程。不同项目前缀会创建不同 volume，避免复用第一轮状态。

保留的 2026-07-20 本地验证证据：

| 运行 | 测试数 | 结果 | 清理 |
|---|---:|---|---|
| 本轮 release preflight，2026-08-12 | 46 | 46 passed，0 failures/errors/skips | 项目 container/volume/network 均为 0 |
| `smartorm-it-run1` | 46 | 46 passed，0 failures/errors/skips | 项目 container/volume/network 均为 0 |
| `smartorm-it-run2` | 46 | 46 passed，0 failures/errors/skips | 项目 container/volume/network 均为 0 |
| 随机顺序，seed `20260720` | 46 | 46 passed，0 failures/errors/skips | 项目 container/volume/network 均为 0 |

第一行属于本轮 preflight 证据，后三行属于 2026-07-20 保留的历史证据。它们共同只验证实际测试的 MySQL 9.4.0 组合，不能证明其他数据库或框架版本。

## 📦 Artifact Preflight

测试编译、28 个 core 回归与 8 个 Starter 测试另行通过后，当前 reactor 以明确跳过测试的方式完成离线 package：

```powershell
mvn -o -DskipTests package
```

已生成并检查：

- 普通 core 库主 JAR，而不是 Spring Boot executable JAR；
- core sources 与 Javadoc JAR；
- 包含恰好一条 `AutoConfiguration.imports` 记录的普通 Starter JAR；
- 隔离 Maven 仓库布局中的 POM；
- 已排除 demo class、`application.yaml` 与 demo SQL 的 artifact 内容，同时仓库仍保留 demo 源码；
- 仓库 `LICENSE` 与打包后的许可证元数据。

早期 core package 验证使用隔离 Maven 缓存，外部使用方通过 2 个测试与离线复跑。当前 Starter reactor 也安装到另一个忽略的 `maven.repo.local`，其外部使用方通过 1 个测试与离线复跑。这些检查只验证本地类发布 artifact 路径；Maven Central 尚未配置、上传或实际消费。

该结果不能证明位级可重复、签名、远程仓库接收或 Starter feature checkpoint 的远程 CI 成功。

## 🛡️ 安全规则

- 禁止对已有/用户数据库运行 `init-smartorm-demo.sql`；其中包含破坏性 DDL。
- 禁止把主机 MySQL 数据目录挂载到测试服务。
- 始终使用唯一 Compose 项目，并执行 `down -v --remove-orphans`。
- 禁止在日志或本地任务记录中保存测试密码或 datasource URL。
- 只有断言确实在健康且已初始化的容器上执行后，才能报告集成测试通过。
