package com.smartorm.core;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.smartorm.resover.SmartDeleteResolver;
import com.smartorm.resover.SmartPageResolver;
import com.smartorm.resover.SmartSelectResolver;
import com.smartorm.resover.SmartUpdateResolver;
import com.smartorm.util.SmartExpressionUtil;
import org.springframework.stereotype.Component;

/**
 * @author <a href="#">Forgotten.</a>
 * @Details 智能 wrapper构建器
 * @CreateDate 2025/11/25
 * @LastModified 2025/11/25
 * @VersionHistory [版本历史]
 */
// TODO：只实现了部分功能，后续需要补充完整
@Component
public class SmartWrapperBuilder {

    /**
     * 通用构建 -- 查询 wrapper（fields / where（带参数替换desc） / order / orderBy）
     * @param fields    查询字段数组，为 null时查询所有字段
     * @param where     条件（支持 #{0} 占位符）
     * @param args      参数
     * @param orderBy   排序字段
     * @param desc      是否倒序（true - 倒叙，false - 正序）
     * @param <T>       实体类型
     * @return 配置好的 QueryWrapper
     */
    public static <T> QueryWrapper<T> buildFullWrapper(
            String[] fields,
            String where,
            Object[] args,
            String orderBy,
            boolean desc
    ) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();

        applyFields(wrapper, fields); // 字段选择
        applyWhere(wrapper, where, args); // args 为 null 时，直接原样 apply
        applyOrder(wrapper, orderBy, desc); // 排序

        return wrapper;
    }

    /**
     * 通用构建 -- 查询 Wrapper（简化版 - 不需要参数替换）
     *
     * @param fields    查询字段数组
     * @param where     条件
     * @param orderBy   排序字段
     * @param desc      是否倒序
     * @param <T>       实体类型
     * @return 配置好的QueryWrapper实例
     */
    public static <T> QueryWrapper<T> buildWrapper(
            String[] fields,
            String where,
            String orderBy,
            boolean desc
    ) {
        return buildFullWrapper(fields, where, null, orderBy, desc);
    }

    /**
     * 构建 -- @SmartSelect 查询 Wrapper
     * @param meta  注解元数据
     * @param args  参数
     * @param <T>   实体类型
     * @return 部分QueryWrapper
     */
    public static <T> QueryWrapper<T> buildSelectWrapper(
            SmartSelectResolver.SelectMeta meta,
            Object[] args
    ) {
        return buildFullWrapper(meta.fields, meta.where, args, meta.orderBy, meta.desc);
    }

    /**
     * 构建 -- @SmartPage 分页查询 Wrapper
     * @param meta  注解元数据
     * @param args  参数
     * @param <T>   实体类型
     * @return 部分QueryWrapper
     */
    public static <T> QueryWrapper<T> buildPageWrapper(
            SmartPageResolver.PageMeta meta,
            Object[] args
    ) {
        return buildFullWrapper(meta.fields, meta.where, args,  null, false);
    }

    /**
     * 构建 -- @SmartUpdate 更新 Wrapper
     * @param meta 注解元数据
     * @param args 参数
     * @param <T>   实体类型
     * @return 部分UpdateWrapper
     */
    public static <T> UpdateWrapper<T> buildUpdateWrapper(
            SmartUpdateResolver.UpdateMeta meta,
            Object[] args
    ) {
        UpdateWrapper<T> wrapper = new UpdateWrapper<>();

        // set 字段（meta.fields 与 meta.values 对应）
        if (meta.fields != null && meta.values != null) {
            int len = Math.min(meta.fields.length, meta.values.length);
            for (int i = 0; i < len; i++) {
                String field = meta.fields[i];

                String valueExpr="";
                if (i < meta.values.length)
                    valueExpr = meta.values[i];

                // 判断是否是 SQL 表达式（包含字段名或函数）
                // 如果不这样写就会出现例如age = age + #{age} 出现错误
                if (isSqlExpression(valueExpr)) {
                    // 使用 SQL SET 表达式
                    String sqlExpression = SmartExpressionUtil.fillExpression(
                            String.format("%s = %s", field, valueExpr), args);
                    wrapper.setSql(sqlExpression);
                } else {
                    // 普通值设置
                    Object value = parseValueExpression(valueExpr, args);
                    wrapper.set(field, value);
                }
            }
        }

        if (meta.where != null && !meta.where.isEmpty()) {
            wrapper.apply(SmartExpressionUtil.fillExpression(meta.where, args));
        }

        return wrapper;
    }


    /**
     * 构建 -- @SmartDelete 删除 Wrapper
     * @param meta 注解元数据
     * @param args 参数
     * @param <T> 实体类型
     * @return 部分QueryWrapper
     *
     */
    public static <T> QueryWrapper<T> buildDeleteWrapper(
            SmartDeleteResolver.DeleteMeta meta,
            Object[] args
    ) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        applyWhere(wrapper, meta.where, args);
        return wrapper;
    }

    /**
     * 私有方法 -- 判断是否是 SQL 表达式
     */
    private static boolean isSqlExpression(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        // 如果包含字段名、数学运算符、函数等，认为是 SQL 表达式
        return value.contains("+") || value.contains("-") || value.contains("*") ||
                value.contains("/") || value.matches(".*[a-zA-Z_]+.*") ||
                value.trim().startsWith("(") || value.trim().endsWith(")");
    }

    /**
     * 私有方法 -- 设置 QueryWrapper 的 select 字段
     */
    private static void applyFields(QueryWrapper<?> wrapper, String[] fields) {
        if (fields != null && fields.length > 0) {
            wrapper.select(fields);
        }
    }

    /**
     * 私有方法 -- 设置 QueryWrapper 的排序
     */
    private static void applyOrder(QueryWrapper<?> wrapper, String orderBy, boolean desc) {
        if (orderBy != null && !orderBy.isEmpty()) {
            wrapper.orderBy(true, !desc, orderBy);
        }
    }

    /**
     * 私有方法 -- 设置 QueryWrapper 的 where 条件<br><br>
     * 将 where 中的 #{i} 或 #{i.prop} 替换为 SQL 字面量，并调用 wrapper.apply(...)
     * 如果 args 为 null（或长度为0），会直接 apply 原始 where（不替换）
     */
    private static void applyWhere(
            AbstractWrapper<?, ?, ?> wrapper,
            String where,
            Object[] args
    ) {
        if (where == null || where.isEmpty()) return;
        String realWhere;
        if (args == null || args.length == 0) {
            realWhere = where;
        } else {
            realWhere = SmartExpressionUtil.fillExpression(where, args);
        }
        wrapper.apply(realWhere);
    }

    /**
     * 私有方法 -- 解析值表达式<br><br>
     * 支持 #{i}、纯数字、纯小数、'字符串'
     */
    public static Object parseValueExpression(String expression, Object[] args) {

        // 1. #{0} → args[0]
        if (expression != null && expression.matches("^#\\{\\d+}$")) {
            int index = Integer.parseInt(expression.substring(2, expression.length() - 1));
            return args[index];
        }

        // 2. 纯数字 → Integer
        if (expression != null && expression.matches("^\\d+$")) {
            return Integer.parseInt(expression);
        }

        // 3. 纯小数 → Double
        if (expression != null && expression.matches("^\\d+\\.\\d+$")) {
            return Double.parseDouble(expression);
        }

        // 4. 'xxx' → 字符串
        if (expression != null && expression.startsWith("'") && expression.endsWith("'")) {
            return expression.substring(1, expression.length() - 1);
        }

        // 5. 默认：表达式原样返回
        return expression;
    }


}