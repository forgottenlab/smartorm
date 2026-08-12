# 快速开始

[English](getting-started.md) · [简体中文](getting-started.zh-CN.md)

> **摘要：** 从一个 Mapper 和一个有测试的方法开始。SmartORM 2.0.x 支持源码/本地模块与本地安装 artifact 接入；当前尚未提供已发布的 Spring Boot Starter 或 Maven Central 制品。

## ✅ 环境要求

- Java 17。
- 当前仓库验证点使用 Spring Boot 3.5.5。
- MyBatis-Plus 3.5.14。
- Spring AOP 与 MyBatis Mapper 扫描。
- 仅在复现数据库测试时需要 MySQL 9.4.0。

其他版本可能可以运行，但当前不能据此声明为已验证支持。

## 📦 引入当前项目

评估时可以把本仓库作为 Maven 项目打开、作为本地源码模块引入，或把当前 artifact 安装到仅供评估环境访问的 Maven 仓库。隔离 install 和外部使用方 2 个 smoke tests 已通过，其中使用方还成功离线复跑。

完成本地安装后，使用方可以声明当前坐标：

```xml
<dependency>
    <groupId>io.github.forgottenlab</groupId>
    <artifactId>smartorm</artifactId>
    <version>2.0.0</version>
</dependency>
```

本轮 preflight 尚未把这些坐标发布到 Maven Central。类发布主 JAR 已排除 demo class、`application.yaml` 和 demo SQL，但仓库仍是包含 demo 的单一源码模块。

MyBatis-Plus-Join 保持传递依赖。JSqlParser 为 optional；Spring Web 与 MySQL Connector/J 为 runtime + optional。应用需要相应能力时应显式添加 optional 依赖。接入其他项目之前请先阅读[兼容性](compatibility.zh-CN.md)。

## 🔧 注册当前运行组件

当前没有 AutoConfiguration。使用方除了扫描自己的包，还必须扫描 SmartORM 组件和内部 Native Mapper：

```java
@SpringBootApplication(scanBasePackages = {
        "com.example.app",
        "io.github.forgottenlab.smartorm"
})
@MapperScan({
        "com.example.app.mapper",
        "io.github.forgottenlab.smartorm.mapper"
})
public class ExampleApplication {
}
```

如果使用 `@SmartPage`，请按应用原有方式注册 MyBatis-Plus 分页拦截器。仓库中的 `MyBatisConfig` 提供了当前示例。

## 🧱 定义实体

SmartORM 继续使用 MyBatis-Plus 的实体元数据：

```java
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_name")
    private String userName;

    private Integer status;

    // getters and setters
}
```

## ⚡ 定义第一个 Mapper

```java
public interface UserMapper extends SmartMapper<User> {

    @SmartSelect(
            fields = {"id", "user_name", "status"},
            where = "status = #{0}",
            orderBy = "id"
    )
    List<User> findByStatus(Integer status);
}
```

`#{0}` 引用 Java 方法的第一个参数。字段名与 SQL 结构必须由开发者静态声明；运行时标量值应放入占位符。

## 🔁 保留现有 MyBatis-Plus 代码

同一个 Mapper 可以继续使用继承的 `BaseMapper` 方法、Wrapper、XML、Provider 以及没有 Smart 注解的自定义方法。SmartORM 只拦截带有已启用 Smart 注解的方法。

## 🧪 验证第一个方法

使用能够自己创建 fixture 的事务型集成测试：

```java
@SpringBootTest
@Transactional
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void findsOnlyRequestedStatus() {
        User row = new User();
        row.setUserName("fixture-user");
        row.setStatus(1);
        userMapper.insert(row);

        List<User> result = userMapper.findByStatus(1);

        assertEquals(1, result.size());
        assertEquals("fixture-user", result.get(0).getUserName());
    }
}
```

运行仓库测试时请遵循[测试指南](testing.zh-CN.md)，禁止把包含破坏性 DDL 的 demo schema 脚本指向已有数据库。

## 🩺 常见错误

### Smart Handler 或 Aspect 未生效

确认组件扫描包含 `io.github.forgottenlab.smartorm`。未来 Starter 可能消除该手动步骤，但当前尚不存在 Starter。

### `SmartNativeMapper` 未注册

JOIN 查询会使用 Native 执行路径。请在 `@MapperScan` 中加入 `io.github.forgottenlab.smartorm.mapper`。

### 占位符下标越界

数字占位符从零开始并对应 Java 方法签名。使用 `#{2}` 时，方法至少需要三个参数。

### DTO 映射失败

当前反射映射器需要无参构造器和可写、类型兼容的字段。迁移前应增加聚焦的 DTO 映射测试。

### Native 查询请求 `ENTITY_LIST`

当前 Native/JOIN 执行器不支持 `ENTITY_LIST`。JOIN/Native 路径请使用 `Map` 或 DTO 结果。

### 分页行为不符合预期

确认已经注册 MyBatis-Plus 分页拦截器，并用测试覆盖分页与排序预期。

### 更新或删除被拒绝

当没有生成有效 `WHERE` 谓词时，SmartORM 会拒绝 `@SmartUpdate` 和 `@SmartDelete`。除非经过评审并确实需要全表操作，否则不要绕过保护；详见[安全](safety.zh-CN.md)。

## 📚 下一步阅读

- [从 MyBatis-Plus 迁移](migration-from-mybatis-plus.zh-CN.md)
- [安全](safety.zh-CN.md)
- [架构](architecture.zh-CN.md)
- [兼容性](compatibility.zh-CN.md)
