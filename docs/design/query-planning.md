# 🧠 SmartORM 统一查询抽象与查询规划说明

> 面向 `SmartQuery`、`SmartSelect`、`SmartPage` 的设计说明文档

这个文档主要记录两个问题：

1. 为什么当前版本同时保留 `SmartSelect` 与 `SmartPage`
2. 为什么未来仍然希望继续推进统一查询抽象

---

## ✨ 当前现状

当前版本中，查询相关主注解包括：

- `SmartSelect`
- `SmartPage`

它们在结构上非常接近，都会涉及：

- fields
- where
- orderBy
- desc
- join

区别主要在于：

- `SmartSelect` 更关注普通查询与结果类型
- `SmartPage` 更关注分页语义与分页返回结构

也正因为这两者高度相似，项目中才引入了：

- `QuerySemantic`
- 公共 SQL 渲染基类
- 共用的 Wrapper 构建思路

这些都是为“统一查询抽象”提前做的铺垫。

---

## ❓ 为什么没有在 2.0.0 直接合并

最直接的原因是：

> Java 注解本身并不适合做你期待中的“属性继承式复用”。

虽然 Spring 生态中有很多“组合注解”的成功案例，例如：

- `@RestController`
- `@GetMapping`

但它们底层依赖的是更完整的元注解解析机制，而不是简单的注解继承。

对于 SmartORM 当前阶段来说，如果直接强行把 `SmartSelect` 与 `SmartPage` 合成一个注解，会带来几个问题：

- 返回类型与分页语义的边界不够清晰
- 现有执行链需要同时处理更多分支
- 注解语义会先于执行模型复杂化
- 维护成本会在 2.0.0 阶段过早上升

所以当前版本选择：

- 保留两个主注解
- 抽公共语义，不强行合并入口

这是一个更稳妥的阶段性方案。

---

## 📦 SmartQuery 的当前定位

当前项目中已经保留了 `SmartQuery` 注解，但它现在的定位是：

> 统一查询抽象的预留入口

也就是说，当前它不是主执行链入口，而是为了后续版本做的接口预留。

这样做的价值是：

- 保留设计方向
- 避免当前版本过早扩大变更面
- 为 2.1.x / 2.2.x 留出结构延展空间

---

## 🔍 为什么需要统一查询抽象

统一查询抽象的目标不是“为了统一而统一”，而是想解决这些问题：

### 1. 减少重复声明

当前 `SmartSelect` 与 `SmartPage` 中有大量相同属性：

- `fields`
- `where`
- `orderBy`
- `desc`
- `join`

长期维护时，这类重复会导致：

- 注解定义重复
- Resolver 重复
- 文档重复
- 用户理解成本增加

### 2. 更统一的查询语义表达

如果未来用户只需要理解一套“查询声明式模型”，那么：

- 普通查询
- 分页查询
- DTO 查询
- Map 查询
- join 查询

都可以放到更统一的认知框架里。

### 3. 为组合注解与元注解机制打基础

统一查询抽象之后，才更容易继续推进：

- 查询类组合注解
- 继承式查询约束
- 统一查询模板

---

## 📘 SelectResultType 为什么当前只保留这几个

当前 `SelectResultType` 保留的是：

- `AUTO`
- `ENTITY_LIST`
- `MAP_LIST`
- `DTO_LIST`

这是一个有意收缩后的结果。

### 当前不急于加入更多类型的原因

#### 单对象
当前已经可以通过返回类型与默认路径处理，不一定要把“单对象”作为显式枚举值。

#### boolean / exists
这类语义更适合后续走独立注解，例如：

- `@SmartExists`

#### 分页
分页不是单纯“返回一种结果类型”，它还涉及：

- 分页参数
- count 查询
- page 查询
- 分页结果模型

所以分页更适合作为独立语义，而不是简单塞进 `SelectResultType`。

---

## 🗺️ 后续可能的统一方向

未来比较合理的一种形式可能是：

```java
@SmartQuery(
        fields = {"id", "user_name"},
        where = "status = #{0}",
        orderBy = "create_time",
        desc = true
)
List<User> findActiveUsers(Integer status);
```

以及：

```java
@SmartQuery(
        fields = {"id", "user_name"},
        where = "status = #{0}",
        orderBy = "create_time",
        desc = true,
        page = 1,
        pageSize = 10
)
PageResult<User> findActiveUsersPage(Integer status);
```

但这类统一只有在以下条件更成熟后才适合推进：

- 返回类型推断规则更稳定
- 分页执行路径边界更明确
- 元注解与组合注解机制设计更成熟

---

## 📎 结论

当前版本不直接合并 `SmartSelect` 与 `SmartPage`，不是因为这个方向不对，  
而是因为：

> 2.0.0 更适合先把执行模型和分层结构稳定下来。

所以当前策略是：

- 保留两个主注解
- 抽公共查询语义
- 为统一查询抽象预留接口
- 把更进一步的统一能力放到后续版本迭代中

这也是当前 `SmartQuery` 存在但尚未正式启用的原因。
