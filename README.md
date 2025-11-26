# Mybatis-Plus-Helper

> 🚀 一个基于 **MyBatis-Plus** 的二次封装增强工具，提供更智能、更简洁的 CRUD 能力
> 通过注解驱动 SQL 拼装，让 Mapper 使用像“声明式编程”一样简单。

## 🔥 项目简介

**Mybatis-Plus-Helper** 是在 MyBatis-Plus 基础上的强化工具。
它通过封装 `SmartMapper<T>` 并引入多个自定义注解，让开发者仅需定义 Mapper 接口即可自动生成 SQL，无需 XML，无需手写方法。

当前支持的智能注解包括：

- `@SmartSelect`
- `@SmartInsert`
- `@SmartUpdate`
- `@SmartDelete`
- `@SmartPage`

工具会根据注解参数自动生成 MySQL SQL 语句（当前版本仅支持 MySQL）。

## 🏗️ 软件架构

整体结构如下：

```
mybatis-plus-helper
├── _demo 							// 用于了解功能的案例
│   ├── entity
|	|	├── User.java				// 用户实体
│   ├── mapper
|	|	├── UserMapper 				// 用户的 Mapper 继承 SmartMapper<T>
│   └── user.sql
├── annotations						// 自定义的注解
│   ├── SmartSelect.java
│   ├── SmartInsert.java
│   ├── SmartUpdate.java
│   ├── SmartDelete.java
│   └── SmartPage.java
├── config
│   └── MyBatisConfig.java			// 配置文件（MyBatis-Plus 插件配置）
├── core
│   ├── SmartSqlExecutor			// 智能语句生成器
│   └── SmartSqlBuilder.java    	// 根据注解动态构建 SQL
├── mapper
│   └── SmartMapper.java        	// 继承 BaseMapper<T>
├── model
│   └── PageResult.java        		// 分页的基础返回类型
└── resover							// 注解的解析器
|   ├── SmartDeleteResover.java
|   ├── SmartInsertResolver.java
|   ├── martPageResolver.java
|   ├── SmartSelectResolver.java           
|   └── SmartUpdateResolver.java
└── util							
    ├── AnnotationCacheUtil.java	// 注解缓存工具
    └── SmartExpressionUtil.java	// 表达式解析工具
```

## 📦 安装

Maven 方式（暂未发行）：

```xml
<dependency>
    <groupId>com.xxx</groupId>
    <artifactId>mybatis-plus-helper</artifactId>
    <version>1.0.0</version>
</dependency>
```

当前使用：可以将除demo包外的其他包下载到项目中改写代码即可运行

## 📝 使用说明

### 1️⃣ 定义实体类

```java
@Data
public class User {
    private Long id;
    private String userName;
    private Integer age;
    private Integer status;
}
```

### 2️⃣ 继承 SmartMapper

```java
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends SmartMapper<User> {
    // 接口
}
```

### 3️⃣ 使用智能注解

#### ✔ 自动拼接 SELECT

```java
@SmartSelect(
        fields = {"id", "user_name", "age", "status"},
        where = "age > #{0} AND status = #{1}",
        orderBy = "create_time",
        desc = true,
        limit = 10
)
default List<User> findActiveUsersByAge(Integer minAge, Integer status) {
    return findByAnnotation("findActiveUsersByAge", new Object[]{minAge, status});
}
```

生成的 SQL：

```Sql
SELECT id, user_name, age, status
FROM user
WHERE age > #{0} AND status = #{1}
ORDER BY create_time DESC
LIMIT 10
```

#### ✔ 自动 INSERT

```java
@SmartInsert(
        fields = {"user_name", "age", "status"},
        values = {"#{0}", "#{1}", "#{2}"}
)
default int insertUser(String userName, Integer age, Integer status) {
    return insertByAnnotation("insertUser", new Object[]{userName, age, status});
}
```

#### ✔ 自动 UPDATE

```java
@SmartUpdate(
        fields = {"status"},
        values = {"#{0}"},
        where = "id = #{1}"
)
default int updateUserStatus(Integer status, Long id) {
    User user = new User();
    user.setStatus(status);
    return updateByAnnotation("updateUserStatus", user, new Object[]{status, id});
}
```

#### ✔ 自动 DELETE

```java
@SmartDelete(where = "id = #{0}")
default int deleteUserById(Long id) {
    return deleteByAnnotation("deleteUserById", new Object[]{id});
}
```

#### ✔ 自动分页

```java
@SmartPage(
        where = "age BETWEEN #{0} AND #{1}",
        orderBy = "age",
        page = 1,
        pageSize = 15
)
default PageResult<User> findUsersByAgeRangePage(Integer minAge, Integer maxAge) {
    return findByPageAnnotation("findUsersByAgeRangePage", new Object[]{minAge, maxAge});
}
```

## 🧪 测试示例

示例测试文件结构：

```
test/
└── UserMapperTests.java
```

示例代码：

```java
@SpringBootTest
@Transactional // 测试后回滚数据
class UserMapperTest {

    @Resource
    private UserMapper userMapper;

    // 详细请查阅代码
}
```

## 🙏 特别致谢

1. **感谢大学中众多老师的专业授课与悉心指导。**
    正是你们的鼓励与建议，让我在大学开启了全新的青春旅程。
    特别感谢（按开课先后顺序）：

   - 大一上《网页设计与制作》的 **王老师**

   - 大一下《数据库原理与应用》的 **孙老师**
   - 大一下《Java 语言程序设计》的 **赵老师**
   - 大一小学期《Java语言编程实践》的 **曹老师**
   - 大二上《数据结构》的 **孙老师**
   - 大二上《软件测试》的 **赵老师**
   - 大二下《Python语言程序设计》的 **张老师**
   - 大二下《软件体系结构与架构技术》的 **王老师**
   - 大二小学期《软件技术项目训练》的 **刘老师**

   你们在课堂上的讲解、在学习上的鼓励、在技术方向上的建议，持续推动着我前进，也成为我创作本项目的重要底气。

2. **感谢家人的支持与理解。**
    谢谢你们在背后默默支持我追逐热爱的事情，是你们给予我最大的力量。

3. **感谢自己保持着一颗热爱编程的心。**
    正是持续的热情与好奇，让我能够不断探索、不断创造、不断前行。

## 🤝 特别致谢

欢迎一起完善，让 Mybatis-Plus-Helper 更强大！

## 🌟 特性扩展（计划中）

- 新增一份命名规范，模仿 Spring Data JPA 根据命名来弥补该工具在简单语句上代码量反而比原生代码多的问题。例如当用户使用方法名：findNameAndAgeById(Long id)就会被解析为

  ```sql
  SELECT name, age FROM User WHERE id = #{id}
  ```

- 当前mapper接口的书写比较繁琐不光需要加default还需要有return...所以我想后续通过AOP切片当方法被调用的时候就直接获取方法名以及参数进行传递到时候大家就可以像书写原生代码一样

  ```java
  User findUserById(int id);
  ```

  