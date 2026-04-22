package io.github.forgottenlab.smartorm.resolver;

import io.github.forgottenlab.smartorm.annotations.SmartInsert;
import io.github.forgottenlab.smartorm.resolver.meta.InsertMeta;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartInsert 注解解析器
 * @CreateDate 2025/11/25
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public class SmartInsertResolver extends SmartBaseResolver {

    /**
     * 解析 @SmartInsert 注解并生成对应 Meta
     */
    public static InsertMeta resolve(Method method, SmartInsert ann, Class<?> mapperInterface) {
        InsertMeta meta = new InsertMeta();
        fillCommonMeta(meta, method, mapperInterface);
        meta.fields = ann.fields();
        meta.values = ann.values();
        return meta;
    }
}