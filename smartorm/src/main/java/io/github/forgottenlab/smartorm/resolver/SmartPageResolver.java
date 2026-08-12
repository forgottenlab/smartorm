package io.github.forgottenlab.smartorm.resolver;

import io.github.forgottenlab.smartorm.annotations.SmartPage;
import io.github.forgottenlab.smartorm.resolver.meta.PageMeta;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartPage 注解解析器
 * @CreateDate 2025/11/25
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public class SmartPageResolver extends SmartBaseResolver {

    /**
     * 解析 @SmartPage 注解并生成对应 Meta
     */
    public static PageMeta resolve(Method method, SmartPage ann, Class<?> mapperInterface) {
        PageMeta meta = new PageMeta();
        fillCommonMeta(meta, method, mapperInterface);

        meta.fields = ann.fields();
        meta.where = ann.where();
        meta.orderBy = ann.orderBy();
        meta.desc = ann.desc();
        meta.page = ann.page();
        meta.pageSize = ann.pageSize();
        meta.joins = resolveJoins(ann.join());

        return meta;
    }
}