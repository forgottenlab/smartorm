# 🚀 MyBatis-Plus-Helper（SmartORM）

> 一个基于 **MyBatis-Plus** 的注解驱动增强工具，聚焦于让常见 CRUD、分页查询、关联查询与轻量原生 SQL 执行更简洁、更清晰、更易扩展。

[查看更新日志](./CHANGELOG.md)

---

## ✨ 项目简介

**MyBatis-Plus-Helper（SmartORM）** 是构建在 **MyBatis-Plus** 之上的增强工具。  
它并不试图替代 MyBatis-Plus，而是希望在保留其原有使用习惯的前提下，为 Mapper 增加更接近声明式编程的写法，并进一步规范执行链路与扩展方式。

这个项目主要解决的是下面几类问题：

- 复杂但重复的 Mapper 方法声明写起来啰嗦
- 查询、分页、更新、删除等逻辑分散在 XML、Wrapper、默认方法中，不够统一
- 当能力逐渐变多时，扩展点不清晰，维护成本越来越高
- 关联查询与 DTO 映射场景下，原有写法不够轻量

项目当前已经围绕这些目标建立了完整的执行链：

`Aspect -> Handler -> Resolver -> Builder / SQL Renderer -> Executor`

---

## 🌟 这个项目的价值

### 1. 不改变 MyBatis-Plus 的核心习惯

你依然可以继续使用：

- `BaseMapper`
- `Wrapper`
- `Page`
- `@TableName` / `@TableField` / `@TableId`

SmartORM 做的是增强，而不是替代。

### 2. 用注解直接声明常见数据操作

对于很多高频场景，不再需要手写一段样板式逻辑，而是可以直接在 Mapper 接口方法上声明：

- 查询什么字段
- where 条件是什么
- 是否分页
- 是否排序
- 是否存在 join
- 结果应该映射成 Entity / Map / DTO

### 3. 执行链更清晰，后续更容易扩展

相比把解析、构建、执行都堆在同一个地方，当前版本已经把职责拆分成：

- `aspect`：入口调度
- `handler`：注解分发执行
- `resolver`：注解解析为 Meta
- `builder / sql.renderer`：构建 Wrapper 或 SQL
- `executor`：统一执行
- `support / util`：辅助支撑

这对后续扩展新注解、新数据库能力、新结果映射方式都更友好。

---

## 🧩 核心特性

当前版本支持：

- `@SmartSelect`
- `@SmartInsert`
- `@SmartUpdate`
- `@SmartDelete`
- `@SmartPage`
- `@SmartJoin`

同时还支持：

- `SelectResultType` 查询结果类型声明
- DTO / VO 映射
- JOIN 查询
- 原生 SQL 渲染与执行
- 生命周期 Hook
- `SmartMapper` 快捷增强方法

---

## ⚖️ 与普通写法的对比

### 场景一：查询单条用户

普通写法通常需要你自己写 Wrapper 或额外封装：

```java
default User findUserById(Long id) {
    QueryWrapper<User> wrapper = new QueryWrapper<User>().eq("id", id).last("LIMIT 1");
    return this.selectOne(wrapper);
}
```

在 SmartORM 中可以直接写成：

```java
@SmartSelect(
        where = "id = #{0}",
        limit = 1
)
User findUserById(Long id);
```

### 场景二：分页查询

普通写法往往要自己构建 `Page`、`Wrapper` 再手动取结果：

```java
Page<User> page = new Page<>(1, 10);
QueryWrapper<User> wrapper = new QueryWrapper<User>()
        .eq("status", 1)
        .orderByDesc("create_time");
IPage<User> result = userMapper.selectPage(page, wrapper);
```

在 SmartORM 中可以声明为：

```java
@SmartPage(
        fields = {"id", "user_name", "age", "status"},
        where = "status = #{0}",
        orderBy = "create_time",
        desc = true,
        page = 1,
        pageSize = 10
)
PageResult<User> findUsersByStatusPage(Integer status);
```

### 场景三：JOIN + DTO

普通情况下，这类写法通常会更快走向 XML、自定义 SQL 或多层手动映射。  
SmartORM 当前已经支持把这条链收口为：

- 注解声明 JOIN
- SQL Renderer 生成语句
- Native Executor 执行
- `SmartBeanMapper` 映射 DTO

---

## 📦 当前版本

- 当前版本：`2.0.0`
- 更新日志：[`CHANGELOG.md`](./CHANGELOG.md)
- GitHub 仓库从 `2.0.0` 开始作为规范化发布起点
- 早期迭代版本主要在 Gitee 完成验证与演进

说明：

- `SmartQuery` 当前作为统一查询抽象的预留注解存在，暂未作为主执行入口启用
- 当前 JOIN 中显式 `on` 条件已经可运行，但后续仍会继续优化 SQL 结构表达方式与安全策略

---

## 🗂️ 文档索引

- [更新日志（CHANGELOG）](./CHANGELOG.md)
- [AOP / Aspect 执行层说明](./docs/architecture/aspect.md)
- [Builder 构建层说明](./docs/architecture/builder.md)
- [Resolver 解析层说明](./docs/architecture/resolver.md)
- [统一查询抽象与查询规划说明](./docs/design/query-planning.md)
- [Demo 数据库、SQL 与测试说明](./docs/guide/setup-and-test.md)

---

## 🛠️ 安装方式

### 方式一：源码 / 模块方式引入（当前推荐）

当前版本尚未发布到 Maven Central。  
你可以通过以下方式使用：

- 直接将源码作为模块引入项目
- 或将核心包拷贝到自己的项目中进行学习与二次开发

适用场景：

- 学习原理
- 定制扩展
- 本地验证
- 参与继续开发

### 方式二：Maven 依赖引入（规划中）

后续版本计划提供标准依赖坐标，例如：

```xml
<dependency>
    <groupId>io.github.forgottenlab</groupId>
    <artifactId>smartorm</artifactId>
    <version>2.0.0</version>
</dependency>
```

### 方式三：Spring Boot Starter 自动装配（规划中）

后续计划提供 Starter，使 AOP、Handler、Resolver、Builder、Executor 等核心组件自动装配，降低接入成本。

---

## 🔧 快速开始

### 1. 定义实体类

```java
@TableName("user")
public class User {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_name")
    private String userName;

    private Integer age;

    @TableField("create_time")
    private Date createTime;

    private Integer status;

    // getter / setter
}
```

### 2. 继承 SmartMapper

```java
public interface UserMapper extends SmartMapper<User> {
}
```

### 3. 使用注解声明方法

#### 查询

```java
@SmartSelect(
        fields = {"id", "user_name", "age", "status"},
        where = "age > #{0} AND status = #{1}",
        orderBy = "create_time",
        desc = true,
        limit = 10
)
List<User> findActiveUsersByAge(Integer minAge, Integer status);
```

#### 插入

```java
@SmartInsert(
        fields = {"user_name", "age", "status"},
        values = {"#{0}", "#{1}", "#{2}"}
)
int insertUser(String userName, Integer age, Integer status);
```

#### 更新

```java
@SmartUpdate(
        fields = {"user_name", "age"},
        values = {"#{0}", "#{1}"},
        where = "id = #{2}"
)
int updateUserNameAndAge(String userName, Integer age, Long id);
```

#### 删除

```java
@SmartDelete(where = "status = #{0} AND age < #{1}")
int deleteInactiveYoungUsers(Integer status, Integer maxAge);
```

`@SmartUpdate` 和 `@SmartDelete` 默认拒绝最终未生成有效 WHERE 谓词的操作，避免意外全表更新或删除。
如果业务确实要求全表操作，必须在目标方法上显式声明 `allowFullTable = true`：

```java
@SmartDelete(allowFullTable = true)
int deleteAllUsers();
```

该 opt-in 只放行空 WHERE 安全检查，不改变其他 SQL 解析或校验语义。升级后，原先依赖无条件全表操作的调用需要显式迁移。

#### 分页

```java
@SmartPage(
        where = "age BETWEEN #{0} AND #{1}",
        orderBy = "age",
        page = 1,
        pageSize = 15
)
PageResult<User> findUsersByAgeRangePage(Integer minAge, Integer maxAge);
```

---

## 🧪 Demo 数据库与测试

如果你希望直接运行 demo 与测试，推荐按下面步骤操作：

🟦 1. 创建并初始化数据库

- 推荐数据库名：`smartorm_demo`
- 建表脚本位于：`src/main/resources/demo/sql/`
- 推荐文件名：`init-smartorm-demo.sql`

🟦 2. 修改本地数据源配置

典型配置示例：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:13306/smartorm_demo?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
```

🟦 3. 执行自动化测试

```bash
mvn clean test
```

更完整的数据库脚本、配置与测试说明见：

- [Demo 数据库、SQL 与测试说明](./docs/guide/setup-and-test.md)

---

## 📘 查询返回类型

当前查询返回支持：

- Entity List
- Map List
- DTO List
- 单对象（AUTO 推断）
- PageResult

你也可以通过 `SelectResultType` 显式指定结果类型：

```java
@SmartSelect(
        fields = {"id", "user_name"},
        resultType = SelectResultType.DTO_LIST
)
List<UserSimpleDTO> findAllUsersAsDTO();
```

---

## 🏗️ 软件架构

### 当前包结构

```text
io.github.forgottenlab.smartorm
├── annotations
├── aspect
├── handler
├── resolver
│   └── meta
├── builder
├── executor
├── mapper
├── model
├── exception
├── sql
│   ├── provider
│   ├── renderer
│   └── model
├── support
│   ├── annotation
│   ├── insert
│   ├── join
│   ├── mapping
│   ├── metadata
│   └── query
├── util
└── demo
```

### 执行链路

```text
Mapper Method
   ↓
Aspect
   ↓
Handler
   ↓
Resolver
   ↓
Builder / SQL Renderer
   ↓
Executor
   ↓
Result
```

### 各层职责

- `annotations`：定义 Smart 注解与相关枚举
- `aspect`：作为统一入口，识别并路由 Smart 注解方法
- `handler`：按注解类型分发具体执行逻辑
- `resolver`：将注解与方法信息解析为 Meta
- `builder`：构建 MyBatis-Plus Wrapper
- `sql.renderer`：为 JOIN / Native 查询渲染 SQL
- `executor`：负责实际执行
- `support`：提供 join 推断、bean 映射、表名解析等支撑能力
- `util`：底层通用工具类
- `demo`：示例代码与验证环境

---

## 🔄 执行模型演进

### 旧版本

旧版本主要依赖 `SmartMapper` 中的 `default` 方法来驱动执行。

```text
Mapper default method
   ↓
反射获取 Method
   ↓
解析注解
   ↓
构建 SQL / Wrapper
   ↓
调用 BaseMapper 执行
```

这种方式在早期验证阶段足够直接，但逐渐出现：

- 职责集中
- 扩展成本高
- 新能力容易堆到同一个位置
- 不利于统一调度与异常处理

### 2.0.0 新版本

当前版本已经转向更清晰的执行链：

```text
Mapper 注解方法
   ↓
Spring AOP 自动拦截
   ↓
SmartAnnotationAspect
   ↓
SmartXXXHandler
   ↓
Resolver -> Meta
   ↓
Builder / SQL Renderer
   ↓
Executor
```

### 新旧模型对比

| 对比项 | 旧版本 | 新版本 |
|---|---|---|
| 执行入口 | default 方法 | Mapper 注解方法 |
| 解析位置 | 分散 | Resolver 统一 |
| SQL / Wrapper 构建 | 混合 | Builder / Renderer 分层 |
| 扩展方式 | 修改核心逻辑 | 新增 Handler / Resolver |
| AOP 支持 | 弱 | 原生支持 |
| 维护成本 | 高 | 更低 |

---

## 🔌 SmartMapper 的增强能力

在保留 MyBatis-Plus 原有能力的基础上，`SmartMapper` 还提供了一组高频快捷方法：

- `insertAndReturn`
- `existsById`
- `existsBy`
- `findOneBy`
- `findOne`

以及生命周期 Hook：

- `beforeSmartOperation`
- `afterSmartOperation`
- `onSmartException`

这些能力的目标不是替代原生用法，而是在高频场景下减少重复代码。

---

## 🧭 测试与示例

项目当前已经包含完整的 demo 与测试代码，覆盖能力包括：

- `SmartSelect`
- `SmartInsert`
- `SmartUpdate`
- `SmartDelete`
- `SmartPage`
- `SmartJoin`
- `SelectResultType`
- `SmartMapper` 默认增强方法
- 生命周期 Hook

如果你希望快速理解项目行为，最推荐从 `demo` 与对应测试类开始阅读。

---

## ⚠️ 已知限制

当前版本仍有一些边界与限制需要说明：

- `SmartQuery` 仅作为统一查询抽象的预留注解，尚未接入主执行链
- Native SQL 路径当前不支持 `ENTITY_LIST`
- JOIN 中显式 `on` 条件已经可运行，但仍有进一步优化空间
- 当前版本更适合作为学习、实验、二次开发与本地模块引入使用
- Maven Central 与 Starter 方式尚未正式发布

---

## 🗺️ Roadmap

未来版本计划继续推进：

🟦 1. 统一查询抽象  
将 `SmartSelect` 与 `SmartPage` 进一步抽象为统一查询能力。

🟦 2. 组合注解与元注解能力  
支持更灵活的注解复用与组合定义。

🟦 3. JOIN 能力增强  
继续完善显式 `on` 条件、安全策略与多表推断能力。

🟦 4. 多数据库方言支持  
逐步为不同数据库方言的 SQL 渲染提供适配空间。

🟦 5. 新增 Smart 注解类型  
例如：

- `@SmartCount`
- `@SmartExists`
- `@SmartBatchInsert`
- `@SmartProcedure`

🟦 6. Spring Boot Starter  
提供更低接入成本的自动装配支持。

---

## 🙏 特别致谢

感谢大学阶段给予我知识启发、方法指导与学习鼓励的老师们。  
也感谢家人的支持与理解，以及自己始终没有熄灭的那份对编程的热爱。

这个项目不是为了“重写一个 ORM”，而是希望在成熟生态之上，做一次更贴近真实使用体验的增强实践。

---

## 📎 结语

MyBatis-Plus-Helper（SmartORM）并不是一个试图颠覆现有生态的框架。  
它更像是一种持续改进：在理解 MyBatis-Plus 的基础上，让它在更多真实业务场景里写起来更顺手、结构更清楚、后续更容易演进。
