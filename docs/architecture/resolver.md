# 🧭 SmartORM Resolver 解析层说明

> 注解到 Meta 的静态语义解析层

Resolver 层负责把 Mapper 方法上的 Smart 注解解析为结构化的 Meta 对象，供后续 Builder、SQL Renderer 和 Executor 使用。

如果说 Aspect 负责“把调用送进来”，  
那么 Resolver 负责的就是：

> 把一个注解方法，解释成一份可执行的结构化语义。

---

## ✨ 模块职责

Resolver 层当前主要负责：

- 从 `Method + 注解` 中提取结构化信息
- 解析当前 Mapper 对应的实体类型
- 解析方法返回类型
- 构建对应的 `XXXMeta`
- 统一处理 `join`、结果类型、分页语义等解析逻辑

Resolver 不负责：

- AOP 拦截
- Wrapper 构建
- SQL 构建
- 数据库执行

---

## 🧭 在整体架构中的位置

```text
Aspect
   ↓
Handler
   ↓
Resolver（Annotation -> Meta）
   ↓
Builder / SQL Renderer
   ↓
Executor
```

Resolver 是整个执行链中的第一层“业务语义解释器”。

---

## 🧩 当前核心组件

### 1. `SmartBaseResolver`

解析基类，负责填充公共元数据，例如：

- `method`
- `mapperClass`
- `returnType`
- `entityClass`

同时也负责解析 join 相关的基础结构。

### 2. `SmartXXXResolver`

当前每一种主注解都有对应解析器，例如：

- `SmartSelectResolver`
- `SmartInsertResolver`
- `SmartUpdateResolver`
- `SmartDeleteResolver`
- `SmartPageResolver`

每个 Resolver 只负责本注解对应的解析规则。

### 3. `SmartReturnTypeResolver`

这是查询链中特别重要的一层，用于推断：

- `ENTITY_LIST`
- `MAP_LIST`
- `DTO_LIST`
- 单对象默认返回

它决定了后续执行路径如何理解当前查询结果。

### 4. `resolver.meta`

Meta 层用于作为 Resolver 的输出结果，当前包括：

- `BaseMeta`
- `SelectMeta`
- `InsertMeta`
- `UpdateMeta`
- `DeleteMeta`
- `PageMeta`
- `JoinMeta`
- `ForeignKeyMeta`
- `QuerySemantic`

---

## 🔄 统一解析流程（以 SmartSelect 为例）

🟦 1. Aspect 定位到方法与注解  
🟦 2. Handler 调用 `SmartSelectResolver.resolve(...)`  
🟦 3. Resolver 填充公共元数据  
🟦 4. Resolver 提取字段、条件、排序、limit、join 等信息  
🟦 5. 解析返回类型与结果模式  
🟦 6. 生成 `SelectMeta`  
🟦 7. 交给后续 Builder 或 SQL Renderer

---

## 📦 QuerySemantic 的作用

当前版本中，`SelectMeta` 与 `PageMeta` 都实现了 `QuerySemantic`。

这样做的好处是：

- `SmartBaseSqlRenderer` 可以复用公共查询渲染逻辑
- `Select` 与 `Page` 的查询语义更统一
- 后续统一查询抽象时，迁移成本更低

这也是 2.0.0 里一个比较关键的结构抽象。

---

## 📘 返回类型解析策略

当前版本下，`SmartReturnTypeResolver` 的策略大致是：

🟦 1. 如果注解显式指定 `resultType`，优先使用显式指定  
🟦 2. 如果返回 `List<Map<String, Object>>`，推断为 `MAP_LIST`  
🟦 3. 如果返回 `List<DTO>`，推断为 `DTO_LIST`  
🟦 4. 如果存在 join 且未显式指定，当前默认更偏向 `MAP_LIST`  
🟦 5. 其他情况回退到默认单对象或实体列表路径

---

## 🔗 Join 解析策略

Resolver 层会先把注解中的 `@SmartJoin` 转换为 `JoinMeta`，包括：

- joinTable
- alias
- joinType
- on
- userDefinedOn

真正的 ON 推断与标准化由后续 JOIN 推断引擎继续处理，但 Resolver 负责先把结构准备好。

---

## 📐 设计原则

Resolver 层遵循：

- 一个主注解对应一个 Resolver
- Resolver 只解析，不执行
- Meta 只承载数据，不负责行为
- 尽量把查询语义统一抽象，而不是让每条链都重复定义

---

## ⚠️ 当前限制

当前版本 Resolver 层仍有一些边界：

- `SmartQuery` 尚未正式接入主执行链
- 更复杂的返回类型推断仍然有继续扩展空间
- 对多数据库差异仍未引入方言级抽象
- 聚合类、统计类解析规则尚未系统化

---

## 🗺️ 后续演进方向

未来 Resolver 层可能继续向这些方向发展：

- 正式接入统一查询抽象
- 支持组合注解与元注解递归解析
- 进一步收紧返回类型与结果模式的推断规则
- 为统计、聚合、批量操作提供更独立的 Meta 体系

---

## 📎 总结

Resolver 层解决的是一个非常关键的问题：

> 如何把“看起来很轻量的注解声明”，解释成一份结构清晰、边界明确、可被后续执行链直接消费的元数据模型。
