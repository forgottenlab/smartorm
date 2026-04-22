package io.github.forgottenlab.smartorm.support.query;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.forgottenlab.smartorm.util.SmartExpressionUtil;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 查询构建辅助工具类
 * @CreateDate 2025/12/11
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public final class SmartQuerySupport {

    private SmartQuerySupport() {
    }

    /**
     * 设置查询字段
     *
     * @param wrapper QueryWrapper 实例
     * @param fields  查询字段数组
     */
    public static void applyFields(QueryWrapper<?> wrapper, String[] fields) {
        if (fields != null && fields.length > 0) {
            wrapper.select(fields);
        }
    }

    /**
     * 设置查询条件
     *
     * @param wrapper QueryWrapper 实例
     * @param where   条件字符串
     * @param args    方法参数数组
     */
    public static void applyWhere(
            AbstractWrapper<?, ?, ?> wrapper,
            String where,
            Object[] args
    ) {
        if (where == null || where.isEmpty()) {
            return;
        }

        String realWhere;
        if (args == null || args.length == 0) {
            realWhere = where;
        } else {
            realWhere = SmartExpressionUtil.fillExpression(where, args);
        }

        wrapper.apply(realWhere);
    }

    /**
     * 设置排序
     */
    public static void applyOrder(QueryWrapper<?> wrapper, String orderBy, boolean desc) {
        if (orderBy != null && !orderBy.isEmpty()) {
            wrapper.orderBy(true, !desc, orderBy);
        }
    }

    /**
     * 判断是否为 SQL 表达式
     */
    public static boolean isSqlExpression(String expr) {
        if (expr == null || expr.trim().isEmpty()) {
            return false;
        }

        String val = expr.trim();

        if (val.matches(".*[+\\-*/].*")) {
            return true;
        }

        if (val.startsWith("(") && val.endsWith(")")) {
            return true;
        }

        if (val.matches("(?i)[a-zA-Z_]+\\(.*\\)")) {
            return true;
        }

        return val.matches("[a-zA-Z_][a-zA-Z0-9_]*");
    }
}