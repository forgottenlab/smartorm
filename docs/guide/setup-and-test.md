# 🧪 Demo 数据库、SQL 与测试说明

> 面向本地运行、数据库初始化与自动化测试的说明文档

这个文档主要解决三个问题：

- SQL 文件应该放在哪里
- 本地数据库应该如何配置
- 项目测试应该如何执行

---

## 📁 SQL 文件存放位置

当前项目将 demo 相关 SQL 放在：

```text
src/main/resources/demo/sql/
```

推荐文件名：

```text
init-smartorm-demo.sql
```

如果后续需要继续拆分，也可以采用：

```text
schema-smartorm-demo.sql
data-smartorm-demo.sql
```

---

## 🗃️ 数据库命名

当前示例数据库统一使用：

```text
smartorm_demo
```

这与项目中的本地演示配置保持一致。

---

## 🧱 建库与建表

推荐在 SQL 文件开头补上：

```sql
CREATE DATABASE IF NOT EXISTS `smartorm_demo`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE `smartorm_demo`;
```

然后继续执行建表语句。  
这样执行一个脚本即可完成：

🟦 1. 建库  
🟦 2. 切库  
🟦 3. 建表

---

## ⚙️ application.yaml 示例

推荐配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:13306/smartorm_demo?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: io.github.forgottenlab.smartorm.demo.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

logging:
  level:
    io.github.forgottenlab.smartorm: debug
    org.mybatis: debug
```

---

## ▶️ 本地运行前检查

在运行项目或测试前，建议先确认：

🟦 1. MySQL 已启动  
🟦 2. `smartorm_demo` 数据库已创建  
🟦 3. 建表 SQL 已执行  
🟦 4. `application.yaml` 中的账号密码与端口无误

如果连接失败，优先排查：

- MySQL 服务是否启动
- 端口是否正确
- 数据库名是否正确
- 用户名密码是否正确

---

## ✅ 测试执行方式

执行全部测试：

```bash
mvn clean test
```

执行单个测试类：

```bash
mvn -Dtest=MapperPageTest test
```

---

## 🧭 当前测试覆盖

当前测试主要覆盖：

- SmartSelect
- SmartInsert
- SmartUpdate
- SmartDelete
- SmartPage
- SmartJoin
- SelectResultType
- SmartMapper 默认增强方法
- 生命周期 Hook

这些测试既用于回归验证，也可以作为使用示例参考。

---

## ⚠️ 关于测试数据

当前测试数据主要通过 `@BeforeEach` 或测试内部插入完成。  
因此数据库脚本主要负责提供表结构，测试运行时再准备所需数据。

---

## 📎 总结

当前项目的本地运行与测试路径可以概括为：

> 一个 demo 数据库、一份初始化 SQL、一套本地配置、一组可直接运行的测试。

这也正是当前 GitHub 版本所提供的默认体验。
