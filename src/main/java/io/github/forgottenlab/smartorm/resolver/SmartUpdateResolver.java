package io.github.forgottenlab.smartorm.resolver;

import io.github.forgottenlab.smartorm.annotations.SmartUpdate;
import io.github.forgottenlab.smartorm.resolver.meta.UpdateMeta;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartUpdate 注解解析器
 * @CreateDate 2025/11/25
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public class SmartUpdateResolver extends SmartBaseResolver {

    /**
     * 解析 @SmartUpdate 注解并生成对应 Meta
     */
    public static UpdateMeta resolve(Method method, SmartUpdate ann, Class<?> mapperInterface) {
        UpdateMeta meta = new UpdateMeta();
        fillCommonMeta(meta, method, mapperInterface);

        meta.fields = ann.fields();
        meta.values = ann.values();
        meta.where = ann.where();
        meta.allowFullTable = ann.allowFullTable();

        return meta;
    }
}
