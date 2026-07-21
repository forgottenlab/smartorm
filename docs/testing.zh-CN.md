# 测试

[English](testing.md) | [简体中文](testing.zh-CN.md)

SmartORM 使用分层证据。编译结果、数据库无关 Renderer 结果、MySQL 集成结果和 CI 结果证明的是不同内容，不能互相替代或混报。

## 测试分层

```text
Unit and compile checks
  -> Database-independent regression tests
  -> MySQL integration tests
  -> CI verification
```

### 单元与编译检查

`test-compile` 验证主源码和测试源码能否使用当前 Java/Maven 依赖缓存编译。它不能证明数据库行为。

```powershell
mvn -o test-compile
```

### 数据库无关回归测试

当前 28 个测试无需 Spring Boot、MySQL、网络服务或测试顺序，直接覆盖 Native SQL 渲染/绑定与空 `WHERE` 写操作安全：

```powershell
mvn -o '-Dtest=SmartMutationWhereSafetyTest,SmartNativeSqlRendererTest' test
```

覆盖范围包括正常/乱序/稀疏/重复占位符、显式/推断 JOIN 谓词、别名、分页渲染、越界参数、空/禁用写谓词、显式全表 opt-in 和输入不可变性。

### MySQL 集成测试

当前 9 个 Spring Boot 测试类执行 46 个测试，覆盖查询、结果映射、插入、更新、删除、分页、JOIN、`SmartMapper` 辅助方法和生命周期 Hook。每个类都使用 `test` profile 和事务回滚。

集成环境由 `docker-compose.test.yml` 定义：

- 精确镜像 `mysql:9.4.0`；
- Compose 项目自有、可删除 volume；
- 仅绑定 loopback；
- 测试前健康检查；
- `utf8mb4` 与明确时区/SQL mode；
- 权威 schema 以只读方式挂载到新容器。

### CI 验证

`.github/workflows/test.yml` 固定 JDK 17，启用 Maven 缓存，编译测试，运行 28 个数据库无关测试，启动 Compose MySQL，运行准确的 46 个数据库测试，不重复测试地执行 package，上传 Surefire 报告，并始终删除数据库状态。

工作流存在于当前工作区，但没有 push，也没有观察到 GitHub 上的实际运行。在真实运行成功前，不应展示通过状态的 CI badge。

## 环境要求

- JDK 17。
- 本地观察基线使用 Maven 3.9.x；仓库没有 Maven Wrapper。
- 集成测试需要支持 Compose 且 Linux container daemon 正常运行的 Docker。
- 主机端口 `13316`，或通过 `SMARTORM_TEST_DB_PORT` 指定其他 loopback 端口。

## 测试专用环境变量

只使用当前进程的值，禁止复用开发或生产数据库凭据：

```powershell
$env:SMARTORM_TEST_ROOT_PASSWORD = 'REDACTED'
$env:SMARTORM_TEST_DB_USERNAME = 'REDACTED'
$env:SMARTORM_TEST_DB_PASSWORD = 'REDACTED'
$env:SMARTORM_TEST_DB_PORT = '13316'
```

Spring test profile 只读取这些测试变量，不会回退到主 datasource 凭据。

## 运行一次全新 MySQL 套件

使用唯一项目名，并始终删除对应 volume：

```powershell
docker compose -p smartorm-it-run1 -f docker-compose.test.yml config --quiet
docker compose -p smartorm-it-run1 -f docker-compose.test.yml up -d --wait
mvn -o '-Dtest=SmartMapperHookTest,SmartMapperCoreTest,MapperUpdateTest,MapperSelectTest,MapperSelectResultTypeTest,MapperPageTest,MapperJoinTest,MapperInsertTest,MapperDeleteTest' test
docker compose -p smartorm-it-run1 -f docker-compose.test.yml down -v --remove-orphans
```

完成 `down -v` 后，检查选定 Compose 项目不再残留 container、volume 或 network。

## 可重复性检查

使用另一个项目名（例如 `smartorm-it-run2`）重复完整流程。不同项目前缀会创建不同 volume，避免复用第一轮状态。

2026-07-20 的本地验证证据：

| 运行 | 测试数 | 结果 | 清理 |
|---|---:|---|---|
| `smartorm-it-run1` | 46 | 46 passed，0 failures/errors/skips | 项目 container/volume/network 均为 0 |
| `smartorm-it-run2` | 46 | 46 passed，0 failures/errors/skips | 项目 container/volume/network 均为 0 |
| 随机顺序，seed `20260720` | 46 | 46 passed，0 failures/errors/skips | 项目 container/volume/network 均为 0 |

该证据只验证当前 MySQL 9.4.0 精确组合，不能证明其他数据库或框架版本。

## Package 状态

最近一次本地离线命令：

```powershell
mvn -o -DskipTests package
```

因本地 Maven 缓存缺少 `org.apache.maven.plugins:maven-jar-plugin:3.4.2` 而阻塞。未修改依赖/插件版本或仓库。CI 设计会在普通仓库访问条件下执行 package，但在 CI 实际运行前仍属于未验证路径。

## 安全规则

- 禁止对已有/用户数据库运行 `init-smartorm-demo.sql`；其中包含破坏性 DDL。
- 禁止把主机 MySQL 数据目录挂载到测试服务。
- 始终使用唯一 Compose 项目，并执行 `down -v --remove-orphans`。
- 禁止在日志或 `.ai` history 中保存测试密码或 datasource URL。
- 只有断言确实在健康且已初始化的容器上执行后，才能报告集成测试通过。
