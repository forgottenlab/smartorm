package io.github.forgottenlab.smartorm.resolver.meta;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartInsert 注解的元数据模型
 * @CreateDate 2025/12/10
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public class InsertMeta extends BaseMeta {

    /** 字段数组 */
    public String[] fields;

    /** 值表达式数组 */
    public String[] values;
}