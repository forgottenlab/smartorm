package io.github.forgottenlab.smartorm.util;

import io.github.forgottenlab.smartorm.exception.SmartOrmException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details Smart 表达式工具类
 * @CreateDate 2025/12/09
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public final class SmartExpressionUtil {

    private SmartExpressionUtil() {
    }

    /**
     * 解析单个值表达式为 Java 对象
     *
     * 支持的表达式格式：
     * 1. #{n} -> 方法参数 args[n]
     * 2. 整数（支持负数） -> Integer
     * 3. 小数（支持负数） -> Double
     * 4. '字符串' -> String
     * 5. 其它 -> 原样返回（SQL 表达式）
     */
    public static Object parseValueExpression(String expr, Object[] args) {
        if (expr == null) {
            return null;
        }
        expr = expr.trim();

        if (expr.startsWith("#{") && expr.endsWith("}")) {
            String inner = expr.substring(2, expr.length() - 1).trim();
            if (inner.matches("\\d+")) {
                int index = Integer.parseInt(inner);
                return (args != null && index < args.length) ? args[index] : null;
            }
        }

        if (expr.startsWith("'") && expr.endsWith("'")) {
            return expr.substring(1, expr.length() - 1);
        }

        if (expr.matches("-?\\d+")) {
            return Integer.parseInt(expr);
        }

        if (expr.matches("-?\\d+\\.\\d+")) {
            return Double.parseDouble(expr);
        }

        return expr;
    }

    /**
     * 填充表达式中的 #{...} 占位符，并转换为 SQL 字面量
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
                result.append(text.substring(index));
                break;
            }

            result.append(text, index, start);

            int end = text.indexOf('}', start + 2);
            if (end == -1) {
                result.append(text.substring(start));
                break;
            }

            String expr = text.substring(start, end + 1);
            Object value = parseValueExpression(expr, args);

            result.append(toSqlLiteral(value));
            index = end + 1;
        }

        return result.toString();
    }

    /**
     * 将 Java 值转换为 SQL 字面量
     */
    private static String toSqlLiteral(Object value) {
        if (value == null) {
            return "NULL";
        }

        if (value instanceof String) {
            String s = ((String) value).replace("'", "''");
            return "'" + s + "'";
        }

        return String.valueOf(value);
    }

    /**
     * 解析 SQL 中的 #{n} 占位符，并收集参数
     */
    public static String parseSqlWithParams(
            String expr,
            Object[] args,
            List<Object> params
    ) {
        Matcher matcher = Pattern.compile("#\\{(\\d+)}").matcher(expr);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            int methodArgIndex = Integer.parseInt(matcher.group(1));
            if (args == null || methodArgIndex >= args.length) {
                int argumentCount = args == null ? 0 : args.length;
                throw new SmartOrmException(
                        "SQL 参数索引越界: #{" + methodArgIndex + "}, 可用参数数量: " + argumentCount
                );
            }

            // 方法参数索引不等于紧凑列表位置；占位符必须指向本次追加的位置。
            int compactParamIndex = params.size();
            params.add(args[methodArgIndex]);
            matcher.appendReplacement(
                    sb,
                    Matcher.quoteReplacement("#{params[" + compactParamIndex + "]}")
            );
        }
        matcher.appendTail(sb);

        return sb.toString();
    }
}
