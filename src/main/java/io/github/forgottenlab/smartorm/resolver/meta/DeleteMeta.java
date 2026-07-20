package io.github.forgottenlab.smartorm.resolver.meta;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartDelete 注解的元数据模型
 * @CreateDate 2025/12/10
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public class DeleteMeta extends BaseMeta {

    /** 删除条件 */
    public String where;

    /** 是否显式允许无有效 WHERE 条件的全表删除 */
    public boolean allowFullTable;
}
