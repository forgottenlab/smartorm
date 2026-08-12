package io.github.forgottenlab.smartorm.builder;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.forgottenlab.smartorm.resolver.meta.BaseMeta;
import io.github.forgottenlab.smartorm.resolver.meta.DeleteMeta;
import io.github.forgottenlab.smartorm.resolver.meta.PageMeta;
import io.github.forgottenlab.smartorm.resolver.meta.SelectMeta;
import io.github.forgottenlab.smartorm.support.query.SmartQuerySupport;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 通用基础 Wrapper 构建器
 * @CreateDate 2025/12/11
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public class SmartWrapperCommonBuilder {

    /**
     * 从 BaseMeta 构建基础 QueryWrapper
     *
     * <p>该方法负责处理查询类操作的公共部分，包括：
     * 字段选择与 WHERE 条件应用。
     */
    public static <T> QueryWrapper<T> buildBaseWrapper(BaseMeta meta, Object[] args) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();

        // 处理字段选择
        applyFieldsByMetaType(wrapper, meta);

        // 处理 WHERE 条件
        applyWhereByMetaType(wrapper, meta, args);

        return wrapper;
    }

    /**
     * 应用排序配置
     */
    public static void applyOrderBy(QueryWrapper<?> wrapper, String orderBy, boolean desc) {
        if (orderBy != null && !orderBy.isEmpty()) {
            wrapper.orderBy(true, !desc, orderBy);
        }
    }

    /**
     * 应用结果限制（LIMIT）
     *
     * <p>仅适用于普通 SELECT 查询，分页查询不使用该方法。
     */
    public static void applyLimit(QueryWrapper<?> wrapper, int limit) {
        if (limit > 0) {
            wrapper.last("LIMIT " + limit);
        }
    }

    /**
     * 根据 Meta 类型应用字段选择
     */
    private static void applyFieldsByMetaType(QueryWrapper<?> wrapper, BaseMeta meta) {
        if (meta instanceof SelectMeta selectMeta) {
            SmartQuerySupport.applyFields(wrapper, selectMeta.fields);
        } else if (meta instanceof PageMeta pageMeta) {
            SmartQuerySupport.applyFields(wrapper, pageMeta.fields);
        }
    }

    /**
     * 根据 Meta 类型应用查询条件
     */
    public static void applyWhereByMetaType(QueryWrapper<?> wrapper, BaseMeta meta, Object[] args) {
        if (meta instanceof SelectMeta selectMeta) {
            SmartQuerySupport.applyWhere(wrapper, selectMeta.where, args);
        } else if (meta instanceof PageMeta pageMeta) {
            SmartQuerySupport.applyWhere(wrapper, pageMeta.where, args);
        } else if (meta instanceof DeleteMeta deleteMeta) {
            SmartQuerySupport.applyWhere(wrapper, deleteMeta.where, args);
        }
    }
}