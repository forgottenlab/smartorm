package io.github.forgottenlab.smartorm.sql.model;

import java.util.List;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 分页 SQL 渲染结果模型
 * @CreateDate 2025/12/26
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public class RenderedPageSql {

    /** 分页查询 SQL */
    private final String pageSql;

    /** 统计总数 SQL */
    private final String countSql;

    /** 参数列表 */
    private final List<Object> params;

    public RenderedPageSql(
            String pageSql,
            String countSql,
            List<Object> params
    ) {
        this.pageSql = pageSql;
        this.countSql = countSql;
        this.params = params;
    }

    public String getPageSql() {
        return pageSql;
    }

    public String getCountSql() {
        return countSql;
    }

    public List<Object> getParams() {
        return params;
    }
}