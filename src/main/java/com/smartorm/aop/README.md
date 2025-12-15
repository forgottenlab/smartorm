## 📘 SmartORM AOP 执行层说明

> **运行期执行中枢**

该模块是 SmartORM 在运行期的**核心调度与执行入口**。

------

## 一、模块职责

SmartORM 执行层负责：

- 拦截 Mapper 方法调用
- 路由 Smart 注解到对应 Handler
- 串联 Resolver / Builder / Executor

------

## 二、职责划分

### 1️⃣ AOP 层（入口）

负责：

- 拦截方法
- 查找 Smart 注解
- 构建 SmartContext
- 委托 Handler 执行

------

### 2️⃣ Handler 层（执行协调）

- 一个 Smart 注解对应一个 Handler
- Handler 是 Resolver 与 Executor 的桥梁
- 不感知 AOP 的存在

------

## 三、统一执行流程

```
Mapper 方法调用
 ↓
AOP 拦截
 ↓
Handler 定位
 ↓
Resolver → Meta
 ↓
Builder → Wrapper / Entity
 ↓
Executor → BaseMapper
 ↓
返回结果
```

------

## 四、Handler 职责差异

| Handler            | 职责                   |
| ------------------ | ---------------------- |
| SmartSelectHandler | list / one / page 路由 |
| SmartInsertHandler | 构建实体并插入         |
| SmartUpdateHandler | update(null, wrapper)  |
| SmartDeleteHandler | 条件删除               |

------

## 五、异常处理约定

- Handler 捕获所有异常
- 统一封装为 `SmartOrmException`
- 上层统一处理

------

## 六、设计原则

- 一个注解 = 一个 Handler
- AOP 只做调度
- Handler 专注执行
- 各层职责清晰、可替换
