package com.smartorm.builder;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.smartorm.util.SmartQueryUtil;
import com.smartorm.resover.meta.DeleteMeta;
import com.smartorm.resover.meta.PageMeta;
import com.smartorm.resover.meta.SelectMeta;
import com.smartorm.resover.meta.UpdateMeta;
import com.smartorm.util.SmartExpressionUtil;
import org.springframework.stereotype.Component;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details
 * 智能 Wrapper 构建器 - 核心构建类<br>
 * 基于各种 Meta 数据构建 Wrapper，包含各类查询、更新、删除操作的构建逻辑
 * @CreateDate 2025/11/25
 * @LastModified 2025/12/11
 * @VersionHistory
 * v1.0.0 2025/11/25
 * - 初始版本：为简化构建时的代码并提高后续拓展性将 Wrapper构建逻辑抽离出来，形成独立的构建器类<br>
 * v1.1.0 2025/12/10
 * - 优化版本：由于参数列表过长并且很多重复于是抽取出元数据 Meta，并且进行替换<br>
 * v1.2.0 2025/12/11
 * - 优化版本：抽取出重复的构建组成基础构建器 SmartWrapperCommonBuilder<br>
 */
@Component
public class SmartWrapperBuilder {

    /** 构建 - @SmartSelect 查询 Wrapper */
    public static <T> QueryWrapper<T> buildSelectWrapper(
            SelectMeta meta,
            Object[] args
    ) {
        // 1. 使用通用构建器创建基础 Wrapper（字段、条件）
        QueryWrapper<T> wrapper = SmartWrapperCommonBuilder.buildBaseWrapper(meta, args);

        // 2. 应用排序配置
        SmartWrapperCommonBuilder.applyOrderBy(wrapper, meta.orderBy, meta.desc);

        // 3. 应用结果限制（LIMIT）
        SmartWrapperCommonBuilder.applyLimit(wrapper, meta.limit);

        return wrapper;
    }

    /** 构建 - @SmartPage 分页查询 Wrapper */
    public static <T> QueryWrapper<T> buildPageWrapper(
            PageMeta meta,
            Object[] args
    ) {
        // 1. 使用通用构建器创建基础 Wrapper（字段、条件）
        QueryWrapper<T> wrapper = SmartWrapperCommonBuilder.buildBaseWrapper(meta, args);

        // 2. 应用排序配置
        SmartWrapperCommonBuilder.applyOrderBy(wrapper, meta.orderBy, meta.desc);

        return wrapper;
    }

    /** 构建 - @SmartUpdate 更新 Wrapper */
    public static <T> UpdateWrapper<T> buildUpdateWrapper(
            UpdateMeta meta,
            Object[] args
    ) {
        UpdateWrapper<T> wrapper = new UpdateWrapper<>();

        // 处理 SET 子句：字段与值的对应关系
        if (meta.fields != null && meta.values != null) {
            int len = Math.min(meta.fields.length, meta.values.length);
            for (int i = 0; i < len; i++) {
                String field = meta.fields[i];
                String valueExpr = (i < meta.values.length) ? meta.values[i] : "";

                // 判断是否为 SQL 表达式（包含字段名或运算符）
                if (SmartQueryUtil.isSqlExpression(valueExpr)) {
                    // SQL 表达式：直接拼接 SET 子句
                    String sqlExpression = SmartExpressionUtil.fillExpression(
                            String.format("%s = %s", field, valueExpr), args);
                    wrapper.setSql(sqlExpression);
                } else {
                    // 普通值：解析后设置
                    Object value = SmartExpressionUtil.parseValueExpression(valueExpr, args);
                    wrapper.set(field, value);
                }
            }
        }

        // 处理 WHERE 条件
        if (meta.where != null && !meta.where.isEmpty()) {
            wrapper.apply(SmartExpressionUtil.fillExpression(meta.where, args));
        }

        return wrapper;
    }

    /** 构建 - @SmartDelete 删除 Wrapper */
    public static <T> QueryWrapper<T> buildDeleteWrapper(
            DeleteMeta meta,
            Object[] args
    ) {
        // 删除操作只需基础 Wrapper（包含 WHERE 条件）
        return SmartWrapperCommonBuilder.buildBaseWrapper(meta, args);
    }

    /**
     * 通用构建 - 查询 Wrapper（完整版）
     * @deprecated
     */
    @Deprecated
    public static <T> QueryWrapper<T> buildFullWrapper(
            String[] fields,
            String where,
            Object[] args,
            String orderBy,
            boolean desc
    ) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();

        SmartQueryUtil.applyFields(wrapper, fields);
        SmartQueryUtil.applyWhere(wrapper, where, args);
        SmartQueryUtil.applyOrder(wrapper, orderBy, desc);

        return wrapper;
    }

    /**
     * 通用构建 - 查询 Wrapper（简化版）
     * @deprecated 建议使用基于 Meta 的构建方法，此方法为兼容旧代码保留
     */
    @Deprecated
    public static <T> QueryWrapper<T> buildWrapper(
            String[] fields,
            String where,
            String orderBy,
            boolean desc
    ) {
        return buildFullWrapper(fields, where, null, orderBy, desc);
    }


}