# 旧版测试入口

此页面保留旧链接兼容，不再建议手工配置或连接已有 MySQL 数据库。

当前测试方案使用固定版本、项目自有且可删除的 Docker Compose 环境：

- 中文：[测试指南](../testing.zh-CN.md)
- English: [Testing](../testing.md)

禁止把 `src/main/resources/demo/sql/init-smartorm-demo.sql` 指向已有数据库；脚本包含破坏性 DDL。
