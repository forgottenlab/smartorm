package com.smartorm.util;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details
 * Smart 表达式工具类<br>
 * 用于解析 Smart 注解中的值表达式，支持 #{index} 参数占位、数字字面量、字符串字面量等<br>
 * 并可将表达式安全地转换为 SQL 可执行片段<br>
 * 该工具类主要用于：<br>
 * - SmartSelect / SmartInsert / SmartUpdate / SmartDelete<br>
 * - where、values 等表达式的解析与填充
 * @CreateDate 2025/12/09
 * @LastModified 2025/12/09
 * @VersionHistory
 * v1.0.0 2025/12/09
 * -为解决 Smart 注解中值表达式解析混乱问题而创建
 */
public final class SmartExpressionUtil {

    /** 工具类禁止实例化 */
    private SmartExpressionUtil(){}

    /**
     * 解析单个值表达式为 Java 对象<br>
     * 支持的表达式格式：
     * 1.#{n} → 方法参数 args[n]<br>
     * 2.整数（支持负数） → Integer<br>
     * 3.小数（支持负数） → Double<br>
     * 4.'字符串' → String<br>
     * 5.其它 → 原样返回（SQL 表达式）<br>
     * 示例：<br>
     * <pre>
     * #{0} -> args[0]
     * 'abc' -> "abc"
     * 10 -> 10
     * age + 1 -> "age + 1"
     * </pre>
     * @param expr 表达式字符串（如 "#{0}"、"'abc'"、"10"）
     * @param args 方法参数数组
     * @return 解析后的 Java 值
     */
    public static Object parseValueExpression(String expr, Object[] args) {
        if (expr == null) return null;
        expr = expr.trim();

        // 1. #{n} → args[n]
        if (expr.startsWith("#{") && expr.endsWith("}")) {
            String inner = expr.substring(2, expr.length() - 1).trim();
            if (inner.matches("\\d+")) {
                int index = Integer.parseInt(inner);
                // 参数越界保护
                return (args != null && index < args.length) ? args[index] : null;
            }
        }

        // 2. '字符串'
        if (expr.startsWith("'") && expr.endsWith("'")) {
            return expr.substring(1, expr.length() - 1);
        }

        // 3. 数字（支持负数）
        if (expr.matches("-?\\d+")) {
            return Integer.parseInt(expr);
        }

        // 4. 小数（支持负数）
        if (expr.matches("-?\\d+\\.\\d+")) {
            return Double.parseDouble(expr);
        }

        // 5. 其它情况：认为是 SQL 表达式，原样返回
        return expr;
    }

    /**
     * 填充表达式中的 #{...} 占位符，并转换为 SQL 字面量<br>
     * 该方法会：<br>
     * 1.扫描文本中的所有 #{...}<br>
     * 2.使用 {@link #parseValueExpression(String, Object[])} 解析值<br>
     * 3.自动转换为 SQL 字面量（字符串加单引号）<br>
     * 示例：<br>
     * <pre>
     * 输入:  "status = #{0} AND name LIKE #{1}"
     * 参数:  [1, "%张%"]
     * 输出:  "status = 1 AND name LIKE '%张%'"
     * </pre>
     * @param text 含占位符的表达式文本
     * @param args 方法参数数组
     * @return 已填充完成的 SQL 表达式
     */
    public static String fillExpression(String text, Object[] args) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder result = new StringBuilder();
        int index = 0;

        while (index < text.length()) {
            int start = text.indexOf("#{", index);
            if (start == -1) {
                // 没有占位符，直接追加剩余内容
                result.append(text.substring(index));
                break;
            }

            // 追加 #{ 之前的内容
            result.append(text, index, start);

            int end = text.indexOf('}', start + 2);
            if (end == -1) {
                // 未闭合的占位符，直接追加并终止
                result.append(text.substring(start));
                break;
            }

            // 提取完整 #{...}
            String expr = text.substring(start, end + 1);
            Object value = parseValueExpression(expr, args);

            // 转换为 SQL 字面量
            result.append(toSqlLiteral(value));

            index = end + 1;
        }

        return result.toString();
    }



    /**
     * 将 Java 值转换为 SQL 字面量<br>
     * 转换规则：<br>
     * - null → NULL<br>
     * - String → 单引号包裹，并转义内部单引号<br>
     * - 其它类型 → 调用 toString()<br>
     * @param value Java 值
     * @return SQL 可直接使用的字面量
     */
    private static String toSqlLiteral(Object value) {
        if (value == null) return "NULL";

        if (value instanceof String) {
            String s = (String) value;
            // 简单地对单引号转义
            s = s.replace("'", "''");
            return "'" + s + "'";
        }

        // 其它类型直接调用 toString（数字/布尔等）
        return String.valueOf(value);
    }

}
