# Getting Started

[English](getting-started.md) · [简体中文](getting-started.zh-CN.md)

> **Summary:** Start with one Mapper and one tested method. The verified 2.0.x core path remains available, while the 2.1.x development line adds a locally installable Starter. Neither path is published to Maven Central.

## ✅ Requirements

- Java 17.
- Spring Boot 3.5.5 for the verified repository point.
- MyBatis-Plus 3.5.14.
- Spring AOP and MyBatis Mapper scanning.
- MySQL 9.4.0 only when reproducing the verified database suite.

Other versions may work, but they are not currently verified support claims.

## 📦 Install the Current Development Reactor

For evaluation, clone and validate this repository, then install the current reactor into a Maven repository available to your local evaluation environment:

```powershell
mvn -DskipTests install
```

Run the test gates separately first; `-DskipTests` only installs an already-validated checkout. These development coordinates are not available from Maven Central.

### Recommended 2.1.x development Starter path

A Spring Boot consumer can depend on the locally installed combined Starter:

```xml
<dependency>
    <groupId>io.github.forgottenlab</groupId>
    <artifactId>smartorm-spring-boot-starter</artifactId>
    <version>2.1.0-SNAPSHOT</version>
</dependency>
```

The Starter is discovered through `AutoConfiguration.imports`. It registers the SmartORM executor, five handlers, registry, and aspect only after it observes exactly one application-owned `SmartNativeMapper`.

### Direct core path

Applications that intentionally retain the established manual integration can depend on the compatible core artifact:

```xml
<dependency>
    <groupId>io.github.forgottenlab</groupId>
    <artifactId>smartorm</artifactId>
    <version>2.1.0-SNAPSHOT</version>
</dependency>
```

The core JAR excludes demo classes, `application.yaml`, and demo SQL even though the `smartorm` child retains the demo sources.

MyBatis-Plus-Join remains transitive. JSqlParser is optional; Spring Web and MySQL Connector/J are runtime-optional. Add optional capabilities explicitly when your application needs them. Review [Compatibility](compatibility.md) before adopting SmartORM in another project.

## 🔧 Register the Mapper Boundary

With the Starter, keep component scanning application-owned and include the SmartORM native Mapper in the application's existing MyBatis scan:

```java
@SpringBootApplication
@MapperScan({
        "com.example.app.mapper",
        "io.github.forgottenlab.smartorm.mapper"
})
public class ExampleApplication {
}
```

This is the application's existing MyBatis registration, not a second scanner owned by SmartORM. Exactly one `SmartNativeMapper` must be registered. The Starter does not create a Mapper scanner, `DataSource`, `SqlSessionFactory`, `SqlSessionTemplate`, transaction manager, or pagination interceptor, and its own bootstrap does not access the database.

If the native Mapper is missing or ambiguous, or the runtime graph conflicts with an application bean, the current Starter backs off without a Validator or FailureAnalyzer. If you use `@SmartPage`, register the MyBatis-Plus pagination interceptor as your application normally would.

For the direct core path only, also include `io.github.forgottenlab.smartorm` in Spring component scanning. Mapper scanning remains the same as above.

## 🧱 Define an Entity

SmartORM uses the same MyBatis-Plus entity metadata:

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

## ⚡ Define the First Mapper

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

`#{0}` references the first Java method argument. Keep field names and SQL structure developer-authored and static; runtime scalar values belong in placeholders.

## 🔁 Keep Existing MyBatis-Plus Code

The same Mapper can continue to use inherited `BaseMapper` methods, Wrapper calls, XML statements, Provider methods, and unannotated custom methods. SmartORM intercepts only methods carrying an active Smart annotation.

## 🧪 Verify the First Method

Use a transaction-scoped integration test that creates its own fixture:

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

For repository tests, follow [Testing](testing.md); never point the destructive demo schema script at an existing database.

## 🩺 Common Errors

### No Smart handler or Aspect is active

With the Starter, confirm the application has registered exactly one `SmartNativeMapper` and has no conflicting SmartORM runtime bean names or pre-existing `SmartAnnotationAspect`. With the direct core path, also confirm `io.github.forgottenlab.smartorm` is included in component scanning.

### `SmartNativeMapper` is not registered

JOIN queries use the native execution path. Include `io.github.forgottenlab.smartorm.mapper` in the application's existing `@MapperScan`; the Starter does not scan it automatically.

### No diagnostic explains why the Starter backed off

Configuration properties/metadata, declaration validation, a Doctor, and a FailureAnalyzer are not implemented in this foundation. Use focused context tests and inspect the Mapper/bean ownership boundary without logging sensitive datasource values.

### Placeholder index is out of range

Numeric placeholders are zero-based and refer to the Java method signature. `#{2}` requires at least three method arguments.

### DTO mapping fails

The current reflection mapper expects a no-argument constructor and writable compatible fields. Add a focused DTO mapping test before migration.

### Native query requests `ENTITY_LIST`

The current native/JOIN executor does not support `ENTITY_LIST`. Use `Map` or DTO results for JOIN/native paths.

### Pagination does not behave as expected

Confirm the MyBatis-Plus pagination interceptor is registered and that page/order expectations are covered by tests.

### Update or delete is rejected

SmartORM rejects `@SmartUpdate` and `@SmartDelete` when no effective `WHERE` predicate is generated. Do not bypass the guard unless a reviewed full-table operation is intentional; see [Safety](safety.md).

## 📚 Next Reading

- [Migration from MyBatis-Plus](migration-from-mybatis-plus.md)
- [Safety](safety.md)
- [Architecture](architecture.md)
- [Compatibility](compatibility.md)
