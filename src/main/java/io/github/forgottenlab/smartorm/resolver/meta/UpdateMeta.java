package io.github.forgottenlab.smartorm.resolver.meta;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartUpdate 注解的元数据模型
 * @CreateDate 2025/12/10
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public class UpdateMeta extends BaseMeta {

    /** 待更新字段 */
    public String[] fields;

    /** 值表达式 */
    public String[] values;

    /** 更新条件 */
    public String where;
}