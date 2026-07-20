package io.github.forgottenlab.smartorm.resolver;

import io.github.forgottenlab.smartorm.annotations.SmartDelete;
import io.github.forgottenlab.smartorm.resolver.meta.DeleteMeta;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartDelete 注解解析器
 * @CreateDate 2025/11/25
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public class SmartDeleteResolver extends SmartBaseResolver {

    /**
     * 解析 @SmartDelete 注解并生成对应 Meta
     */
    public static DeleteMeta resolve(Method method, SmartDelete ann, Class<?> mapperInterface) {
        DeleteMeta meta = new DeleteMeta();
        fillCommonMeta(meta, method, mapperInterface);
        meta.where = ann.where();
        meta.allowFullTable = ann.allowFullTable();
        return meta;
    }
}
