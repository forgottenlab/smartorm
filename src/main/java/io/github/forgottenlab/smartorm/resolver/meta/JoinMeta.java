package io.github.forgottenlab.smartorm.resolver.meta;

import io.github.forgottenlab.smartorm.annotations.JoinType;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details JOIN 元数据模型
 * @CreateDate 2025/12/16
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public class JoinMeta {

    /** 要 JOIN 的表名 */
    public String joinTable;

    /** 表别名 */
    public String alias;

    /** JOIN 类型 */
    public JoinType joinType;

    /** JOIN 的 ON 片段 */
    public String on;

    /** 是否为用户显式指定的 ON 条件 */
    public boolean userDefinedOn;

    /** 左表字段（预留） */
    public String leftColumn;

    /** 右表字段（预留） */
    public String rightColumn;
}