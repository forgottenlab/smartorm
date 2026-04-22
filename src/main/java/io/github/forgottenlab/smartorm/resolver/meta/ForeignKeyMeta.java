package io.github.forgottenlab.smartorm.resolver.meta;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 外键关系元数据模型
 * @CreateDate 2025/12/16
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public class ForeignKeyMeta {

    /** 主键表名 */
    public String pkTable;

    /** 主键列名 */
    public String pkColumn;

    /** 外键表名 */
    public String fkTable;

    /** 外键列名 */
    public String fkColumn;
}