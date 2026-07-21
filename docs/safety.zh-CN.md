# 安全

[English](safety.md) | [简体中文](safety.zh-CN.md)

SmartORM 对破坏性注解写操作采用 fail-closed 策略。本文只描述 2.0.x 已实现行为；这不代表 SmartORM 可以替代应用授权、事务设计、数据库权限、备份或数据库自身保护。

## 默认写操作策略

当完成构建的 MyBatis-Plus Wrapper 不包含有效 normal predicate segment 时，`@SmartUpdate` 与 `@SmartDelete` 会拒绝执行。

会被拒绝的示例：

```java
@SmartDelete
int deleteAllRowsAccidentally();
```

```java
@SmartUpdate(
        fields = {"status"},
        values = {"#{0}"},
        where = ""
)
int updateAllRowsAccidentally(Integer status);
```

空白/纯空格表达式、只有 `WHERE` 的空壳以及运行时被禁用的谓词都保持 fail-closed。

## 显式全表 opt-in

有意执行全表操作时，必须使用方法级标志：

```java
@SmartUpdate(
        fields = {"status"},
        values = {"#{0}"},
        allowFullTable = true
)
int resetEveryStatus(Integer status);
```

```java
@SmartDelete(allowFullTable = true)
int deleteEveryRow();
```

两个注解的默认值都是 `false`。

## `allowFullTable = true` 的作用

它只绕过该 Mapper 方法的“缺少有效 `WHERE`”拒绝逻辑。

它不会：

- 增加授权或用户确认；
- 启动或强制事务；
- 绕过数据库权限、约束、触发器或拦截器；
- 验证业务意图；
- 改变字段/值解析或占位符语义；
- 让操作可逆；
- 影响 `BaseMapper`、XML、Provider 或非 SmartORM SQL 路径。

每个 opt-in 都应按安全敏感代码处理：必须进行评审，定义事务/回滚方案，提供确定性测试并指定负责人。

## 谓词与 SET 边界

安全检查在 Wrapper 完成构建后、执行前发生。SET 赋值不能算作谓词，只有最终结构化 normal predicate segment 才能满足安全检查。

因此，`status = 0` 之类的更新值不能被误认为已经存在限制性 `WHERE`。

## SQL 结构与运行时值

`fields`、`where`、`orderBy`、JOIN 表/别名和显式 `ON` 等注解属性属于开发者编写的 SQL 结构。禁止直接根据任意客户端输入生成这些内容。

运行时方法参数使用 `#{0}` 这样的数字占位符。当前路径存在差异：

- Native/JOIN Renderer 会把占位符转换为有序 MyBatis 参数绑定。
- Wrapper 路径目前会通过 `SmartExpressionUtil.fillExpression` 渲染转义后的标量字面量，再用于 Wrapper `apply`/表达式 `setSql`。

因此，不应宣称当前所有路径具有完全一致的 prepared-statement 绑定语义。请保持注解结构静态，在应用边界验证输入，并为引号、null、顺序和数据库副作用增加回归测试。

## JOIN `ON` 边界

显式 `ON` 被视为开发者声明的结构，不会整体作为标量参数。`ON` 内的 `#{n}` 在 Native 路径中属于绑定值。

当前校验会拒绝分号和部分 DML 关键字，但它不是通用 SQL Parser 或 Sanitizer。禁止接收任意外部 `ON` 表达式。优先使用固定列引用，并通过测试验证别名与占位符顺序。

## 保护范围

空谓词保护只覆盖 SmartORM 的 `@SmartUpdate` 与 `@SmartDelete` 执行路径。现有 MyBatis-Plus 方法、Wrapper、XML、Provider SQL、脚本和直接 JDBC 仍由各自控制机制负责。

数据库拦截器可以作为纵深防御，但不能替代 SmartORM 方法级安全契约。

## 全表操作评审清单

- [ ] 没有 `WHERE` 是有意行为，而不是占位符或条件错误。
- [ ] 方法名清楚表达全表影响。
- [ ] 调用方已经授权，任意外部输入无法直接触达该方法。
- [ ] 已定义事务、备份、回滚和维护窗口要求。
- [ ] 数据库集成测试验证影响行数与最终状态。
- [ ] 数据库侧保护仍保持启用。
- [ ] 代码评审中能够看到 opt-in 及其理由。

## 回归证据

`SmartMutationWhereSafetyTest` 包含 18 个数据库无关测试，覆盖空白/纯空格/`WHERE` 空壳、运行时禁用谓词、有效谓词、Handler 拒绝、显式 opt-in、参数顺序和输入不可变性。数据库套件还会使用可销毁 MySQL 9.4.0 验证注解更新/删除行为。

准确证据与命令见[测试](testing.zh-CN.md)。
