package com.smartorm.resover.meta;

import java.lang.reflect.Method;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details
 * Smart 注解解析后的基础元数据模型<br>
 * BaseMeta 定义了所有 SmartXXX 操作通用的结构信息，是 Resolver 与 Handler 之间的标准数据载体<br>
 * 说明：<br>
 * - Meta 不包含任何业务逻辑<br>
 * - Meta 不负责 SQL 构建或执行
 * @CreateDate 2025/12/10
 * @LastModified 2025/12/10
 * @VersionHistory [版本历史]
 */
public class BaseMeta {
    public Method method;               // 当前被解析的 Mapper 方法
    public Class<?> mapperClass;        // Mapper 接口类型
    public Class<?> returnType;         // 方法返回类型
    public Class<?> entityClass;        // Mapper<T> 中的实体类型
    public boolean isList;              // 返回值是否为 List
    public boolean isPageResult;        // 是否分页结果
}
