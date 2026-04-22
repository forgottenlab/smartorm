# 🚦 SmartORM AOP / Aspect 执行层说明

> 运行期统一入口与调度中枢

`aspect` 层负责把 **Mapper 注解方法调用** 接入到 SmartORM 的执行链中。  
它不直接参与 SQL 构建，也不负责数据库执行，而是负责：

- 拦截 Smart 注解方法
- 识别当前方法上的 Smart 注解
- 构建 `SmartContext`
- 路由到对应 `SmartHandler`
- 在执行前后触发生命周期 Hook

---

## ✨ 模块职责

当前入口类为：

- `SmartAnnotationAspect`

它是 SmartORM 在运行期的统一入口，主要目标是：

1. 让使用者像调用普通 Mapper 方法一样调用 Smart 注解方法
2. 让执行链从一开始就具备统一入口
3. 将“调度”与“执行”明确分离

---

## 🧭 在整体架构中的位置

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

`aspect` 层只负责把调用正确地送入后续链路。

---

## 🔄 统一执行流程

当前版本下，执行流程可以概括为：

🟦 1. 用户调用 Mapper 注解方法  
🟦 2. Spring AOP 拦截调用  
🟦 3. `SmartAnnotationAspect` 识别方法上的 Smart 注解  
🟦 4. 构建 `SmartContext`  
🟦 5. 根据注解类型定位对应 `SmartHandler`  
🟦 6. 进入 `Handler -> Resolver -> Builder / SQL Renderer -> Executor`  
🟦 7. 返回结果

---

## 📦 SmartContext 的作用

`SmartContext` 用于在执行链中传递当前运行时信息，当前主要包含：

- 当前 Mapper 实例
- 当前 Method
- Mapper 接口类型
- 方法参数数组

这样后续 `Handler`、`Resolver`、`Executor` 不需要重复自己解析这些上下文信息。

---

## 🔌 与 Handler 的边界

Aspect 与 Handler 的职责边界如下：

### Aspect 负责

- 拦截
- 找注解
- 构建上下文
- 触发 Hook
- 路由 Handler

### Handler 负责

- 接收具体注解类型
- 调用 Resolver 解析 Meta
- 选择 Builder 或 SQL Renderer
- 调用 Executor 执行

也就是说：

> Aspect 只做“入口调度”，Handler 才是“注解执行协调层”。

---

## 🪝 生命周期 Hook

当前版本支持通过 `SmartMapper` 提供以下 Hook：

- `beforeSmartOperation`
- `afterSmartOperation`
- `onSmartException`

Aspect 层会在合适时机触发它们：

- 执行前：`beforeSmartOperation`
- 成功后：`afterSmartOperation`
- 异常时：`onSmartException`

这样可以为日志、审计、调试、扩展行为预留统一插点。

---

## 📐 设计原则

当前 `aspect` 层遵循以下原则：

- Mapper 方法本身就是执行入口
- AOP 只负责调度，不负责业务语义解析
- 一个方法只允许声明一个 Smart 主注解
- Aspect 不直接参与 SQL 或 Wrapper 构建
- Hook 能力统一在入口处编排

---

## ⚠️ 当前限制

当前版本需要注意：

- `SmartQuery` 仍为预留注解，尚未接入主执行入口
- 当前入口依赖于明确的 Smart 注解匹配
- 生命周期 Hook 已可用，但后续仍有继续抽象为统一执行器包装层的空间

---

## 🗺️ 后续演进方向

### 1. 统一查询抽象接入
后续如果 `SmartQuery` 成为统一查询入口，Aspect 的注解识别将进一步简化。

### 2. 更统一的执行包装
未来可能将部分 Hook 包装能力从 Aspect 继续抽象到统一执行器中，使职责边界更稳定。

---

## 📎 总结

Aspect 层不是 SmartORM 的“业务核心”，但它是整个运行期执行链的**统一入口**。

它解决的问题不是“怎么查数据库”，而是：

> 如何把一个普通的 Mapper 注解方法，稳定地接入一条清晰、可扩展、可维护的执行链。
