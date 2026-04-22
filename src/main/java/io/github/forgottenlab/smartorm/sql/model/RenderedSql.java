package io.github.forgottenlab.smartorm.sql.model;

import java.util.List;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 普通 SQL 渲染结果模型
 * @CreateDate 2025/12/21
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public class RenderedSql {

    /** 完整 SQL 或带占位符的 SQL */
    private final String sql;

    /** 参数列表（按顺序） */
    private final List<Object> params;

    public RenderedSql(String sql, List<Object> params) {
        this.sql = sql;
        this.params = params;
    }

    public String getSql() {
        return sql;
    }

    public List<Object> getParams() {
        return params;
    }
}