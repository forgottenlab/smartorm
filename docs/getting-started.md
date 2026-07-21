# Getting Started

[English](getting-started.md) | [简体中文](getting-started.zh-CN.md)

This guide describes the current 2.0.x source/local-module integration. SmartORM does not yet provide a published Spring Boot Starter or verified Maven Central artifact.

## Requirements

- Java 17.
- Spring Boot 3.5.5 for the verified repository point.
- MyBatis-Plus 3.5.14.
- Spring AOP and MyBatis Mapper scanning.
- MySQL 9.4.0 only when reproducing the verified database suite.

Other versions may work, but they are not currently verified support claims.

## Add the current project

For evaluation, open this repository as a Maven project or include it as a local source module. The project coordinates are `io.github.forgottenlab:smartorm:2.0.0`, but public repository availability has not been verified.

The current single artifact also contains demo code and direct MPJ, MySQL, and Web dependencies. Review [Compatibility](compatibility.md) before adopting it in another project.

## Register current runtime components

There is no AutoConfiguration yet. A consuming application must scan SmartORM components and the internal native Mapper in addition to its own packages:

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

If you use `@SmartPage`, register the MyBatis-Plus pagination interceptor as your application normally would. The repository demo provides a current example in `MyBatisConfig`.

## Define an entity

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

## Define the first Mapper

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

## Keep existing MyBatis-Plus code

The same Mapper can continue to use inherited `BaseMapper` methods, Wrapper calls, XML statements, Provider methods, and unannotated custom methods. SmartORM intercepts only methods carrying an active Smart annotation.

## Verify the first method

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

## Common errors

### No Smart handler or Aspect is active

Confirm `io.github.forgottenlab.smartorm` is included in component scanning. A future Starter may remove this manual step, but it does not exist today.

### `SmartNativeMapper` is not registered

JOIN queries use the native execution path. Include `io.github.forgottenlab.smartorm.mapper` in `@MapperScan`.

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

## Next reading

- [Migration from MyBatis-Plus](migration-from-mybatis-plus.md)
- [Safety](safety.md)
- [Architecture](architecture.md)
- [Compatibility](compatibility.md)
