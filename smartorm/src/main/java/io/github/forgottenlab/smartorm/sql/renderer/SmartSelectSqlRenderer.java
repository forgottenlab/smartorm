package io.github.forgottenlab.smartorm.sql.renderer;

import io.github.forgottenlab.smartorm.resolver.meta.QuerySemantic;
import io.github.forgottenlab.smartorm.resolver.meta.SelectMeta;
import io.github.forgottenlab.smartorm.sql.model.RenderedSql;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 普通查询 SQL 渲染器
 * @CreateDate 2025/12/21
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public final class SmartSelectSqlRenderer extends SmartBaseSqlRenderer {

    private SmartSelectSqlRenderer() {
    }

    /**
     * 根据查询语义渲染普通查询 SQL
     */
    public static RenderedSql render(QuerySemantic meta, Object[] args) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        // SELECT
        renderSelect(sql, meta.getFields());

        // FROM
        renderFrom(sql, meta.getEntityClass());

        // JOIN
        renderJoins(sql, meta, args, params);

        // WHERE
        renderWhere(sql, meta.getWhere(), args, params);

        // ORDER BY
        renderOrderBy(sql, meta.getOrderBy(), meta.isDesc());

        // LIMIT
        renderLimit(sql, ((SelectMeta) meta).limit);

        return new RenderedSql(sql.toString(), params);
    }

    /**
     * 渲染 LIMIT 子句
     */
    private static void renderLimit(StringBuilder sql, int limit) {
        if (limit > 0) {
            sql.append(" LIMIT ").append(limit);
        }
    }
}
