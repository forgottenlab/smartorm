package com.smartorm.util;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details Wrapper构建工具类
 * @CreateDate 2025/12/11
 * @LastModified 2025/12/11
 * @VersionHistory [版本历史]
 */
public final class SmartQueryUtil {

    /** 工具类禁止实例化 */
    private SmartQueryUtil(){}

    /**
     * 内部方法 - 设置查询字段
     * @param wrapper QueryWrapper 实例
     * @param fields  需要查询的字段数组，为 null 或空时不设置（查询所有字段）
     */
    public static void applyFields(QueryWrapper<?> wrapper, String[] fields) {
        if (fields != null && fields.length > 0) {
            wrapper.select(fields);
        }
    }

    /**
     * 内部方法 - 设置查询条件<br>
     * 将 WHERE 条件中的 #{i} 占位符替换为实际参数值<br>
     * 如果参数数组为空，则直接使用原始条件字符串
     * @param wrapper QueryWrapper 实例
     * @param where   条件字符串，支持 #{0} 占位符
     * @param args    方法参数数组
     */
    public static void applyWhere(
            AbstractWrapper<?, ?, ?> wrapper,
            String where,
            Object[] args
    ) {
        if (where == null || where.isEmpty()) return;

        String realWhere;
        if (args == null || args.length == 0) {
            realWhere = where; // 无参数时直接使用原始条件
        } else {
            realWhere = SmartExpressionUtil.fillExpression(where, args);
        }

        wrapper.apply(realWhere);
    }

    /**
     * 内部方法 - 设置排序
     * @param wrapper QueryWrapper 实例
     * @param orderBy 排序字段名，为 null 或空时不设置排序
     * @param desc    是否倒序（true - 倒序，false - 正序）
     */
    public static void applyOrder(QueryWrapper<?> wrapper, String orderBy, boolean desc) {
        if (orderBy != null && !orderBy.isEmpty()) {
            wrapper.orderBy(true, !desc, orderBy);
        }
    }

    /**
     * 判断是否为 SQL 表达式<br>
     * 包含运算符、函数调用、字段引用的表达式被认为是 SQL 表达式<br>
     * 如："age + 1"、"NOW()"、"field_name" 等
     * @param expr 待判断的字符串
     * @return true - 是 SQL 表达式，false - 不是 SQL 表达式
     */
    public static boolean isSqlExpression(String expr) {
        if (expr == null || expr.trim().isEmpty()) return false;

        String val = expr.trim();

        // 1. 含有运算符
        if (val.matches(".*[+\\-*/].*")) return true;

        // 2. 括号包裹，可能是函数或表达式
        if (val.startsWith("(") && val.endsWith(")")) return true;

        // 3. 函数调用，例如 NOW()、DATEDIFF()
        if (val.matches("(?i)[a-zA-Z_]+\\(.*\\)")) return true;

        // 4. 普通字段名，假设包含字母且无空格可认为是字段名
        if (val.matches("[a-zA-Z_][a-zA-Z0-9_]*")) return true;

        return false;
    }

}
