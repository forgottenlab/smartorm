## 📘 SmartORM Resolver 模块说明

> **静态解析层（Compile-like Phase）**

Resolver 模块负责将 Mapper 方法上的 `@SmartXXX` 注解
 **解析为结构化的 Meta 元数据对象**，供运行期执行阶段使用。

------

## 一、模块职责

Resolver 模块 **只负责“注解 → 元数据”的静态解析**，不参与任何执行行为。

核心职责包括：

- 从 `Method + SmartXXX 注解` 中提取结构化信息
- 解析方法返回类型（`List / PageResult / 单对象`）
- 解析 Mapper 泛型，确定实体类型
- 构建对应的 `XXXMeta` 元数据对象

❌ Resolver 不负责：

- SQL / Wrapper 构建
- BaseMapper 执行
- AOP 拦截
- 数据库交互

------

## 二、模块在整体架构中的位置

```
AOP
 ↓
Resolver（注解 → Meta）
 ↓
Handler
 ↓
Builder
 ↓
Executor
```

Resolver 是 **执行链路中第一个“语义解析层”**。

------

## 三、核心组件说明

### 1️⃣ SmartResolverBase

- 所有 Resolver 的抽象基类
- 负责填充 Meta 的公共字段：

包括但不限于：

- `method`
- `mapperClass`
- `returnType`
- `entityClass`
- `isList`
- `isPageResult`

------

### 2️⃣ SmartXXXResolver

- 每一种 Smart 注解对应一个 Resolver
- 仅负责：
  - 调用 `fillCommonMeta(...)`
  - 读取注解属性并写入 Meta
- **不包含任何执行逻辑**

------

### 3️⃣ XXXMeta

- Resolver 的最终输出结果
- Handler 的唯一输入依据
- 仅作为**结构化数据载体**
- ❌ 不包含任何业务逻辑或行为

------

## 四、统一解析流程（以 @SmartSelect 为例）

1. AOP 拦截 Mapper 方法
2. 定位 Method 与 SmartSelect 注解
3. 调用 `SmartSelectResolver.resolve(...)`
4. Resolver：
   - 填充 BaseMeta 公共信息
   - 解析注解字段（fields / where / orderBy / ...）
5. 返回 `SelectMeta`
6. 进入 Handler 阶段

------

## 五、异常与边界约定

- Resolver 内部异常统一抛出为运行期异常
- Resolver 不进行异常封装
- 异常封装由 Handler 或上层处理

------

## 六、设计原则

- Resolver ≠ Handler
- Resolver 只解析，不执行
- Meta 只存数据，不做逻辑
- 一个 Smart 注解必须对应唯一 Resolver
