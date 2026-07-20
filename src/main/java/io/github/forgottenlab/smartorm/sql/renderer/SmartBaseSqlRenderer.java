package io.github.forgottenlab.smartorm.sql.renderer;

import io.github.forgottenlab.smartorm.resolver.meta.JoinMeta;
import io.github.forgottenlab.smartorm.resolver.meta.QuerySemantic;
import io.github.forgottenlab.smartorm.support.join.JoinInferenceEngine;
import io.github.forgottenlab.smartorm.support.metadata.TableNameResolver;
import io.github.forgottenlab.smartorm.util.SmartExpressionUtil;

import java.util.List;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SQL 公共渲染基类
 * @CreateDate 2025/12/26
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public abstract class SmartBaseSqlRenderer {

    /**
     * 渲染 SELECT 子句
     */
    protected static void renderSelect(
            StringBuilder sql,
            String[] fields
    ) {
        sql.append("SELECT ");
        if (fields == null || fields.length == 0) {
            sql.append("*");
        } else {
            sql.append(String.join(",", fields));
        }
    }

    /**
     * 渲染 FROM 子句
     */
    protected static void renderFrom(
            StringBuilder sql,
            Class<?> entityClass
    ) {
        String table = TableNameResolver.resolve(entityClass);
        sql.append(" FROM ").append(table);
    }

    /**
     * 渲染 JOIN 子句
     */
    protected static void renderJoins(
            StringBuilder sql,
            QuerySemantic meta,
            Object[] args,
            List<Object> params
    ) {
        List<JoinMeta> joins = meta.getJoins();

        if (joins == null || joins.isEmpty()) {
            return;
        }

        String mainTable = TableNameResolver.resolve(meta.getEntityClass());
        JoinInferenceEngine.inferOnCondition(mainTable, joins);

        for (JoinMeta join : joins) {
            sql.append(" ")
                    .append(join.joinType.name())
                    .append(" JOIN ")
                    .append(join.joinTable);

            if (join.alias != null && !join.alias.isEmpty()) {
                sql.append(" ").append(join.alias);
            }

            // ON 是已由注解/元数据定义并校验的 SQL 结构，只有其中的 #{n} 是绑定值。
            String parsedOn = SmartExpressionUtil.parseSqlWithParams(join.on, args, params);
            sql.append(" ON ").append(parsedOn);
        }
    }

    /**
     * 渲染 WHERE 子句
     */
    protected static void renderWhere(
            StringBuilder sql,
            String where,
            Object[] args,
            List<Object> params
    ) {
        if (where == null || where.isEmpty()) {
            return;
        }

        sql.append(" WHERE ");
        String parsed = SmartExpressionUtil.parseSqlWithParams(where, args, params);
        sql.append(parsed);
    }

    /**
     * 渲染 ORDER BY 子句
     */
    protected static void renderOrderBy(
            StringBuilder sql,
            String orderBy,
            boolean desc
    ) {
        if (orderBy == null || orderBy.isEmpty()) {
            return;
        }

        sql.append(" ORDER BY ").append(orderBy);
        if (desc) {
            sql.append(" DESC");
        }
    }
}
