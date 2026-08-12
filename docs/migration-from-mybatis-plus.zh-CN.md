# 从 MyBatis-Plus 迁移

[English](migration-from-mybatis-plus.md) · [简体中文](migration-from-mybatis-plus.zh-CN.md)

> **摘要：** SmartORM 是增强而不是替代。迁移应当渐进、由确定性测试证明，并为每个方法保留清晰回滚路径。

现有 `BaseMapper`、Wrapper、XML、Provider、MyBatis-Plus 注解和 MyBatis-Plus-Join 实现继续有效。

## 🧭 可用性总览

| Level | 目标 | 当前 2.0.x 状态 |
|---|---|---|
| 0 | 添加当前源码/本地模块或本地安装 artifact，但不迁移方法 | 本地 artifact 使用方 smoke 已验证；公共仓库接入尚未验证 |
| 1 | 接入一个 Mapper | 已实现 |
| 2 | 接入一个方法 | 已实现 |
| 3 | 选择性替换重复固定形态 SQL | 已实现，但必须有测试 |
| 4 | 启用使用方诊断 | 规划给未来 Starter；当前不可用 |
| 5 | 按应用模块扩大迁移 | 流程指导；需要项目自己的回归与回滚控制 |

## 0️⃣ Level 0 — 保持现有行为

在分支或本地评估模块中开始，暂时不修改 Mapper 继承和方法。

- 保持 `BaseMapper`、XML、Wrapper、Provider 和现有 MPJ 代码不变。
- 添加本地模块前后都运行原应用及测试套件。
- 记录当前依赖面：仓库仍是单模块；发布 artifacts 排除 demo class/configuration/SQL，MPJ 保持传递，JSqlParser/Web/MySQL 按文档为 optional。

当前限制：隔离本地仓库 install 和外部使用方 2 个 smoke tests 已通过，包括一次离线复跑，但仍没有经过验证的 Maven Central 制品或 Starter。该证据只证明本地 dependency-only 路径，不代表公共安装可用。

回滚：移除本地模块/依赖及对应扫描配置。

## 1️⃣ Level 1 — 接入一个 Mapper

选择一个低风险 Mapper：

```java
public interface UserMapper extends SmartMapper<User> {
}
```

继承的 MyBatis-Plus 方法继续可用。现有 XML、Wrapper、Provider 和无注解自定义方法可以保留在同一个 Mapper 中。

当前 2.0.x 注意事项：`SmartMapper` 同时继承 `MPJBaseMapper`，因此 MPJ 在当前公开类型层次中不是可选依赖。

回滚：恢复为 `BaseMapper<User>`，仅删除该 Mapper 使用的 SmartORM 方法/Hook。

## 2️⃣ Level 2 — 迁移一个方法

选择已有测试的确定性固定形态方法：

```java
@SmartSelect(where = "status = #{0}", orderBy = "id")
List<User> findByStatus(Integer status);
```

推荐步骤：

1. 对比阶段保留原 XML/Wrapper/Provider 方法。
2. 为两条路径创建相同 fixture。
3. 对比准确结果行、顺序、null 行为、异常和数据库副作用。
4. 逐步切换调用方。
5. 在约定的回滚窗口内保留旧路径。

回滚：把调用方切回旧方法并删除注解方法。其他 Mapper 方法不受影响。

## 3️⃣ Level 3 — 替换重复模板 SQL

适合的场景：

- 简单固定查询；
- 确定性分页；
- 带明确谓词的简单更新和删除；
- 固定 JOIN 投影到 `Map` 或 DTO；
- 能够准确验证行为的重复 Mapper 样板代码。

不要为了提高 SmartORM 使用比例而迁移。当替换会降低清晰度或增加风险时，应保留复杂或成熟代码。

回滚：保留或建立原实现检查点，并按单个方法回退。

## 4️⃣ Level 4 — 启用诊断

该 Level 是未来迁移阶段，不是当前功能。规划中的 Starter 可能提供已注册 Mapper 校验、模块检查、可操作错误和脱敏诊断。

在它实现之前：

- 依赖聚焦的单元/集成测试；
- 避免在 SQL 和参数日志中输出敏感值；
- 手动诊断组件与 Mapper 扫描；
- 不要把 Doctor、FailureAnalyzer 或 AutoConfiguration 描述为已可用。

当前 2.0.x 没有可以启用或关闭的该类诊断功能。

## 5️⃣ Level 5 — 按应用模块扩大迁移

只有 Level 1–3 在代表性周期内稳定后才扩大范围。

- 一次只迁移一个有边界的应用模块。
- 指定回滚负责人。
- 观测查询结果、数据库副作用、错误率和支持成本。
- 行为不明确或旧扩展路径更合适时，停止当前迁移波次。
- 如果其他模块仍使用 SmartORM，保留依赖，只回滚受影响模块。

## 🚫 不推荐迁移的场景

不要仅为了统一形式而迁移：

- 复杂运行时动态 SQL；
- 存储过程；
- 数据库专用 SQL；
- 没有维护痛点的成熟 XML/Provider 代码；
- 依赖未支持数据库或未验证框架版本的查询；
- 缺少确定性回归和回滚覆盖的代码；
- 任意客户端控制的字段、运算符、排序或 SQL 片段。

## 🔗 JOIN 迁移说明

当前 2.0.x JOIN 声明使用 Native SQL 路径，且 `SmartMapper` 直接继承 MPJ。必须验证行倍增、别名、显式/推断 `ON`、占位符顺序、DTO 映射和分页总数。

独立 JOIN 模块与 `SmartJoinMapper` 属于 3.0 路线图，不是当前坐标或 API。

## 🛡️ 写操作迁移安全

当没有生成有效 `WHERE` 谓词时，`@SmartUpdate` 和 `@SmartDelete` 默认拒绝执行。如果旧代码有意执行全表操作，迁移时必须进行方法级评审并显式声明 `allowFullTable = true`。

不要用 opt-in 掩盖缺失或错误的谓词。详见[安全](safety.zh-CN.md)。

## ✅ 迁移前

- [ ] 项目匹配精确验证组合，或已经建立自己的兼容矩阵。
- [ ] 现有测试覆盖结果行、排序、null、异常、总数和数据库副作用。
- [ ] SQL 结构固定且由开发者控制。
- [ ] 已评审占位符与方法参数映射。
- [ ] 已指定回滚实现和负责人。
- [ ] 写操作有有效谓词，或全表意图已评审。

## 🧪 迁移后

- [ ] 旧实现与 SmartORM 在确定性 fixture 上行为一致。
- [ ] 占位符数量与顺序正确。
- [ ] 分页总数/排序和 JOIN 行倍增一致。
- [ ] 未记录敏感 SQL 参数。
- [ ] 已在测试环境演练回滚。
