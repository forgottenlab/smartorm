# 快速开始

[English](getting-started.md) · [简体中文](getting-started.zh-CN.md)

> **摘要：** 从一个 Mapper 和一个有测试的方法开始。已验证的 2.0.x core 路径继续可用，2.1.x 开发线则增加可本地安装的 Starter。两条路径都尚未发布到 Maven Central。

## ✅ 环境要求

- Java 17。
- 当前仓库验证点使用 Spring Boot 3.5.5。
- MyBatis-Plus 3.5.14。
- Spring AOP 与 MyBatis Mapper 扫描。
- 仅在复现数据库测试时需要 MySQL 9.4.0。

其他版本可能可以运行，但当前不能据此声明为已验证支持。

## 📦 安装当前开发版 Reactor

评估时，克隆并验证本仓库，然后把当前 reactor 安装到本地评估环境可访问的 Maven 仓库：

```powershell
mvn -DskipTests install
```

应先单独运行测试门禁；`-DskipTests` 只安装已经验证的 checkout。这些开发版坐标尚未在 Maven Central 提供。

### 推荐的 2.1.x 开发版 Starter 路径

Spring Boot 使用方可依赖本地安装的合并式 Starter：

```xml
<dependency>
    <groupId>io.github.forgottenlab</groupId>
    <artifactId>smartorm-spring-boot-starter</artifactId>
    <version>2.1.0-SNAPSHOT</version>
</dependency>
```

Starter 通过 `AutoConfiguration.imports` 发现。它只在观察到恰好一个应用自有 `SmartNativeMapper` 后，注册 SmartORM executor、五个 handler、registry 与 aspect。

### 直接 Core 路径

明确保留原有手动集成的应用可依赖兼容的 core artifact：

```xml
<dependency>
    <groupId>io.github.forgottenlab</groupId>
    <artifactId>smartorm</artifactId>
    <version>2.1.0-SNAPSHOT</version>
</dependency>
```

Core JAR 已排除 demo class、`application.yaml` 和 demo SQL，但 `smartorm` 子模块仍保留 demo 源码。

MyBatis-Plus-Join 保持传递依赖。JSqlParser 为 optional；Spring Web 与 MySQL Connector/J 为 runtime + optional。应用需要相应能力时应显式添加 optional 依赖。接入其他项目之前请先阅读[兼容性](compatibility.zh-CN.md)。

## 🔧 注册 Mapper 边界

使用 Starter 时，component scan 保持由应用所有，并在应用已有 MyBatis 扫描中加入 SmartORM Native Mapper：

```java
@SpringBootApplication
@MapperScan({
        "com.example.app.mapper",
        "io.github.forgottenlab.smartorm.mapper"
})
public class ExampleApplication {
}
```

这是应用已有的 MyBatis 注册，不是 SmartORM 所有的第二套 scanner。必须恰好注册一个 `SmartNativeMapper`。Starter 不创建 Mapper scanner、`DataSource`、`SqlSessionFactory`、`SqlSessionTemplate`、事务管理器或分页拦截器，自身 bootstrap 也不访问数据库。

Native Mapper 缺失或有歧义，或运行时 Bean 图与应用 Bean 冲突时，当前 Starter 会在没有 Validator 或 FailureAnalyzer 的情况下 back off。如果使用 `@SmartPage`，请按应用原有方式注册 MyBatis-Plus 分页拦截器。

仅在直接 core 路径中，还需把 `io.github.forgottenlab.smartorm` 加入 Spring component scan。Mapper 扫描与上述配置相同。

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

使用 Starter 时，确认应用恰好注册一个 `SmartNativeMapper`，且没有冲突的 SmartORM 运行时 Bean 名称或已存在的 `SmartAnnotationAspect`。使用直接 core 路径时，还需确认 component scan 包含 `io.github.forgottenlab.smartorm`。

### `SmartNativeMapper` 未注册

JOIN 查询会使用 Native 执行路径。请在应用已有 `@MapperScan` 中加入 `io.github.forgottenlab.smartorm.mapper`；Starter 不会自动扫描。

### 没有诊断说明 Starter 为何 back off

该 foundation 尚未实现配置属性/元数据、声明校验、Doctor 或 FailureAnalyzer。请使用聚焦的上下文测试并检查 Mapper/Bean 所有权边界，同时避免记录敏感 datasource 值。

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
