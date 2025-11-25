package com.smartorm.util;

import java.lang.reflect.Field;

/**
 * @author <a href="#">Forgotten.</a>
 * @Details 解析注解中表达式的工具类
 * @CreateDate 2025/11/25
 * @LastModified 2025/11/25
 * @VersionHistory [版本历史]
 */
public class SmartExpressionUtil {

    /**
     * 解析 -- 单个表达式为 Java 对象<br><br>
     *
     * 将单个表达式解析为 Java 对象（用于 set(...) 的值）<br><br>
     * 支持：<br>
     * 1.#{0}           -> args[0]<br>
     * 2.#{0.name}      -> read property "name" of args[0]<br>
     * 3.'literal'      -> String literal<br>
     * 4.数字            -> Integer<br>
     * 5.其它直接返回字符串
     */
    public static Object parse(String expr, Object[] args) {
        if (expr == null) return null;
        expr = expr.trim();

        // #{0} 或 #{0.prop}
        if (expr.startsWith("#{") && expr.endsWith("}")) {
            String inner = expr.substring(2, expr.length() - 1).trim(); // 例如 #{0} 或 #{0.name}

            // 带属性访问
            if (inner.contains(".")) {
                String[] parts = inner.split("\\.", 2);
                int idx = Integer.parseInt(parts[0]);
                Object param = args[idx];
                String prop = parts[1];

                // 关键代码：读取对象属性值
                return readFieldValue(param, prop);
            } else {
                int idx = Integer.parseInt(inner);
                return args[idx];
            }
        }

        // 'literal' 字符串
        if (expr.startsWith("'") && expr.endsWith("'") && expr.length() >= 2) {
            return expr.substring(1, expr.length() - 1);
        }

        // 整数数字
        if (expr.matches("-?\\d+")) {
            try {
                return Integer.parseInt(expr);
            } catch (NumberFormatException ignored) {}
        }

        // 默认返回原字符串
        return expr;
    }

    /**
     * 替换 -- 表达式中的所有 #{...} 占位符<br><br>
     * 将 where/表达式中的所有 #{...} 占位符替换成 SQL 字面量（字符串会加单引号）<br>
     * 例如 "status = #{0} AND name LIKE #{1}" -> "status = 1 AND name LIKE '%张%'"
     */
    public static String fillExpression(String text, Object[] args) {
        if (text == null || text.isEmpty()) return text;

        StringBuilder out = new StringBuilder();
        int idx = 0;

        while (idx < text.length()) {
            int start = text.indexOf("#{", idx);
            if (start == -1) {
                out.append(text.substring(idx));
                break;
            }

            out.append(text, idx, start);
            int end = text.indexOf('}', start + 2);
            if (end == -1) {
                // 没有闭合的 #{，直接追加剩余并退出
                out.append(text.substring(start));
                break;
            }

            String expr = text.substring(start, end + 1); // 包括 #{}
            Object val = parse(expr, args);

            // 转换为 SQL 字面量
            out.append(toSqlLiteral(val));
            idx = end + 1;
        }
        return out.toString();
    }

    /**
     * 私有方法 -- 读取对象字段值（支持私有字段和父类字段）
     */
    private static Object readFieldValue(Object obj, String fieldName) {
        if (obj == null) return null;
        try {
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(obj);
        } catch (NoSuchFieldException nsf) {
            // 尝试父类字段
            Class<?> cls = obj.getClass();
            while (cls.getSuperclass() != null) {
                cls = cls.getSuperclass();
                try {
                    Field f = cls.getDeclaredField(fieldName);
                    f.setAccessible(true);
                    return f.get(obj);
                } catch (Exception e) {
                    /* 继续查找 */
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 私有方法 -- 将 Java 值转换为 SQL 字面量<br><br>
     * 例如：<br>
     * 1.null -> "NULL"<br>
     * 2.String -> 带单引号，并转义单引号<br>
     * 3.其它类型 -> 调用 toString()
     */
    private static String toSqlLiteral(Object val) {
        if (val == null) return "NULL";
        if (val instanceof String) {
            String s = (String) val;
            // 简单地对单引号转义
            s = s.replace("'", "''");
            return "'" + s + "'";
        }
        // 其它类型直接调用 toString（数字/布尔等）
        return String.valueOf(val);
    }
}
