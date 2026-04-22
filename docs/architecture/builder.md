# 🧱 SmartORM Builder 构建层说明

> Meta 到 Wrapper / Entity / 执行输入的转换层

Builder 层的职责是：  
将 `Resolver` 产出的结构化元数据，转换为后续执行器可消费的对象。

当前版本主要包括两种构建方向：

- `Wrapper` 构建
- 插入实体构建

---

## ✨ 模块职责

Builder 层当前主要负责：

- 根据 `SelectMeta / PageMeta / DeleteMeta` 构建 `QueryWrapper`
- 根据 `UpdateMeta` 构建 `UpdateWrapper`
- 根据 `InsertMeta` 构建插入实体
- 将字段、条件、排序、限制等构建逻辑集中起来

Builder 不负责：

- 注解解析
- AOP 拦截
- SQL 执行
- BaseMapper 调用

---

## 🧭 在整体架构中的位置

```text
Resolver
   ↓
Builder（Meta -> Wrapper / Entity）
   ↓
Executor
```

当查询不需要走 Native SQL 路径时，Builder 就是执行前最后一层“语义落地”。

---

## 🧩 当前核心组件

### 1. `SmartWrapperBuilder`

面向具体场景的构建入口：

- `buildSelectWrapper(...)`
- `buildPageWrapper(...)`
- `buildUpdateWrapper(...)`
- `buildDeleteWrapper(...)`

它负责根据 Meta 类型拼装适合的 Wrapper。

### 2. `SmartWrapperCommonBuilder`

负责抽取重复构建逻辑，例如：

- 字段选择
- where 条件
- 排序
- limit

这样可以避免普通查询与分页查询的公共逻辑重复散落。

### 3. `SmartInsertUtil`

用于根据 `InsertMeta` 动态构建实体对象，作为插入执行路径的输入。

---

## 🔄 Select / Page / Update / Delete 的构建差异

### Select
构建 `QueryWrapper`，重点在：

- fields
- where
- orderBy
- limit

### Page
构建 `QueryWrapper`，重点在：

- fields
- where
- orderBy

分页本身由后续分页执行器或 SQL 渲染层处理。

### Update
构建 `UpdateWrapper`，重点在：

- fields + values
- where
- SQL 表达式和值表达式的区分

### Delete
构建 `QueryWrapper`，重点在：

- where

---

## 🔌 与 SQL Renderer 的边界

Builder 与 SQL Renderer 都在做“构建”，但它们面向的输出不同：

### Builder 输出
- `QueryWrapper`
- `UpdateWrapper`
- 实体对象

### SQL Renderer 输出
- `RenderedSql`
- `RenderedPageSql`

因此可以把两者理解为：

- Builder：面向 MyBatis-Plus 执行路径
- SQL Renderer：面向 Native SQL 执行路径

---

## 📐 设计原则

Builder 层遵循这些原则：

- 只构建，不执行
- Meta 驱动构建
- 重复逻辑尽量抽到公共构建器
- Wrapper 路径与 Native SQL 路径边界清晰
- 构建结果要尽可能接近后续执行器的直接输入

---

## ⚠️ 当前限制

当前 Builder 层仍有一些限制：

- 对更复杂查询结构的表达仍然偏轻量
- `GROUP BY / HAVING` 等能力尚未纳入统一构建模型
- 更复杂更新表达式仍需继续收紧规则

---

## 🗺️ 后续演进方向

后续 Builder 层主要可能向这些方向继续演进：

- 引入更明确的查询片段抽象
- 逐步支持 `GROUP BY`、聚合、统计类构建
- 与统一查询抽象（例如 `SmartQuery`）进一步对齐
- 为多数据库方言适配预留更清晰的构建边界

---

## 📎 总结

Builder 层解决的核心问题是：

> 如何把已经解析好的查询语义，稳定地落地成可执行输入。

它不是执行层，但它决定了执行层拿到的输入是否统一、是否清晰、是否容易扩展。
