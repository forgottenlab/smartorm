package io.github.forgottenlab.smartorm.builder;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.github.forgottenlab.smartorm.resolver.meta.DeleteMeta;
import io.github.forgottenlab.smartorm.resolver.meta.PageMeta;
import io.github.forgottenlab.smartorm.resolver.meta.SelectMeta;
import io.github.forgottenlab.smartorm.resolver.meta.UpdateMeta;
import io.github.forgottenlab.smartorm.support.query.SmartQuerySupport;
import io.github.forgottenlab.smartorm.util.SmartExpressionUtil;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 智能 Wrapper 构建器核心实现
 * @CreateDate 2025/11/25
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@Component
public class SmartWrapperBuilder {

    /**
     * 构建 @SmartSelect 查询 Wrapper
     */
    public static <T> QueryWrapper<T> buildSelectWrapper(
            SelectMeta meta,
            Object[] args
    ) {
        // 1. 创建基础 Wrapper（字段、条件）
        QueryWrapper<T> wrapper = SmartWrapperCommonBuilder.buildBaseWrapper(meta, args);

        // 2. 应用排序配置
        SmartWrapperCommonBuilder.applyOrderBy(wrapper, meta.orderBy, meta.desc);

        // 3. 应用结果限制
        SmartWrapperCommonBuilder.applyLimit(wrapper, meta.limit);

        return wrapper;
    }

    /**
     * 构建 @SmartPage 分页查询 Wrapper
     */
    public static <T> QueryWrapper<T> buildPageWrapper(
            PageMeta meta,
            Object[] args
    ) {
        // 1. 创建基础 Wrapper（字段、条件）
        QueryWrapper<T> wrapper = SmartWrapperCommonBuilder.buildBaseWrapper(meta, args);

        // 2. 应用排序配置
        SmartWrapperCommonBuilder.applyOrderBy(wrapper, meta.orderBy, meta.desc);

        return wrapper;
    }

    /**
     * 构建 @SmartUpdate 更新 Wrapper
     */
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
                String valueExpr = meta.values[i];

                // SQL 表达式：直接拼接 SET 子句
                if (SmartQuerySupport.isSqlExpression(valueExpr)) {
                    String sqlExpression = SmartExpressionUtil.fillExpression(
                            String.format("%s = %s", field, valueExpr),
                            args
                    );
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

    /**
     * 构建 @SmartDelete 删除 Wrapper
     */
    public static <T> QueryWrapper<T> buildDeleteWrapper(
            DeleteMeta meta,
            Object[] args
    ) {
        return SmartWrapperCommonBuilder.buildBaseWrapper(meta, args);
    }
}