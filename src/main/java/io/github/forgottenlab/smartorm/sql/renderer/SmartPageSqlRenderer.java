package io.github.forgottenlab.smartorm.sql.renderer;

import io.github.forgottenlab.smartorm.resolver.meta.PageMeta;
import io.github.forgottenlab.smartorm.sql.model.RenderedPageSql;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 分页 SQL 渲染器
 * @CreateDate 2025/12/26
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public final class SmartPageSqlRenderer {

    private SmartPageSqlRenderer() {
    }

    /**
     * 根据 PageMeta 渲染分页 SQL 与统计 SQL
     */
    public static RenderedPageSql render(PageMeta meta, Object[] args) {
        StringBuilder baseSql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        // SELECT
        SmartBaseSqlRenderer.renderSelect(baseSql, meta.getFields());

        // FROM
        SmartBaseSqlRenderer.renderFrom(baseSql, meta.getEntityClass());

        // JOIN
        SmartBaseSqlRenderer.renderJoins(baseSql, meta, params);

        // WHERE
        SmartBaseSqlRenderer.renderWhere(baseSql, meta.where, args, params);

        // ORDER BY
        SmartBaseSqlRenderer.renderOrderBy(baseSql, meta.orderBy, meta.desc);

        // COUNT SQL
        String countSql = "SELECT COUNT(*) FROM (" + baseSql + ") tmp";

        // PAGE SQL
        long offset = (meta.page - 1) * meta.pageSize;
        String pageSql = baseSql + " LIMIT " + meta.pageSize + " OFFSET " + offset;

        return new RenderedPageSql(pageSql, countSql, params);
    }
}