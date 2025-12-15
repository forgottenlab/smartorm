## 📘 SmartORM Builder 模块说明

> **Wrapper / Entity 构建层**

Builder 模块负责将 Resolver 输出的 Meta 元数据
 **转换为可被 MyBatis-Plus 执行的 Wrapper 或实体对象**。

------

## 一、模块职责

Builder 模块 **只负责构建 Wrapper / Entity**，不负责执行。

核心职责：

- 根据 Meta 构建 `QueryWrapper / UpdateWrapper`
- 统一字段选择、条件、排序、限制逻辑
- 提供兼容旧 API 的通用构建能力

❌ Builder 不负责：

- SQL 执行
- AOP 拦截
- 注解解析
- BaseMapper 调用

------

## 二、模块在整体架构中的位置

```
Resolver
 ↓
Builder（Meta → Wrapper / Entity）
 ↓
Executor
```

------

## 三、核心构建方法

| 方法                 | 说明                 | 返回类型           |
| -------------------- | -------------------- | ------------------ |
| `buildSelectWrapper` | 构建普通查询 Wrapper | `QueryWrapper<T>`  |
| `buildPageWrapper`   | 构建分页查询 Wrapper | `QueryWrapper<T>`  |
| `buildUpdateWrapper` | 构建更新 Wrapper     | `UpdateWrapper<T>` |
| `buildDeleteWrapper` | 构建删除 Wrapper     | `QueryWrapper<T>`  |

------

### 通用构建能力

- `buildFullWrapper`
- `buildWrapper`

> 新版本推荐全部使用 **基于 Meta 的构建方法**

------

## 四、SmartWrapperCommonBuilder

公共构建逻辑集中处理：

- 字段选择
- 条件应用
- 排序规则
- Limit 处理

核心方法：

- `buildBaseWrapper(BaseMeta meta, Object[] args)`
- `applyOrderBy(...)`
- `applyLimit(...)`

------

## 五、执行流程（以 SELECT 为例）

1. Resolver 输出 Meta
2. Handler 调用 Builder
3. Builder 构建 QueryWrapper
4. Wrapper 交由 Executor 执行

------

## 六、设计原则

- Builder 只构建，不执行
- 所有通用逻辑集中封装
- Meta 驱动构建过程
- 单一职责，便于扩展
