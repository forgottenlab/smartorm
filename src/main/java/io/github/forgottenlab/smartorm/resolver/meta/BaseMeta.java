package io.github.forgottenlab.smartorm.resolver.meta;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details Smart 注解解析后的基础元数据模型
 * @CreateDate 2025/12/10
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public class BaseMeta {

    /** 当前被解析的 Mapper 方法 */
    public Method method;

    /** Mapper 接口类型 */
    public Class<?> mapperClass;

    /** 方法返回类型 */
    public Class<?> returnType;

    /** SmartMapper<T> 中的实体类型 */
    public Class<?> entityClass;
}