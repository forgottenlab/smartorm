package com.smartorm.builder;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.smartorm.util.SmartQueryUtil;
import com.smartorm.resover.meta.BaseMeta;
import com.smartorm.resover.meta.DeleteMeta;
import com.smartorm.resover.meta.PageMeta;
import com.smartorm.resover.meta.SelectMeta;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details
 * 通用基础 Wrapper 构建器<br>
 * 拆分 SmartWrapperBuilder 中重复的构建组成基础构建器<br>
 * 为不同类型的 Meta 提供统一的构建逻辑，包含字段选择、条件应用、排序、限制等通用功能
 * @CreateDate 2025/12/11
 * @LastModified 2025/12/11
 * @VersionHistory [版本历史]
 */
public class SmartWrapperCommonBuilder {

    // ============================================================================
    // 基础构建方法
    // ============================================================================

    /**
     * 从 BaseMeta 构建基础 QueryWrapper<br>
     * 根据 Meta 的具体类型，应用相应的字段选择和条件设置<br>
     * 这是所有查询类型（SELECT、DELETE）的通用基础构建方法
     */
    public static <T> QueryWrapper<T> buildBaseWrapper(BaseMeta meta, Object[] args) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();

        // 处理字段选择（SELECT 和 PAGE 查询有此配置）
        applyFieldsByMetaType(wrapper, meta);

        // 处理 WHERE 条件（所有查询类型都有此配置）
        applyWhereByMetaType(wrapper, meta, args);

        return wrapper;
    }

    /** 应用排序配置 */
    public static void applyOrderBy(QueryWrapper<?> wrapper, String orderBy, boolean desc) {
        if (orderBy != null && !orderBy.isEmpty()) {
            wrapper.orderBy(true, !desc, orderBy);
        }
    }

    /**
     * 应用结果限制（LIMIT）<br>
     * 为 QueryWrapper 添加 LIMIT 子句，限制查询结果数量<br>
     * 仅适用于普通 SELECT 查询，分页查询不使用此方法
     */
    public static void applyLimit(QueryWrapper<?> wrapper, int limit) {
        if (limit > 0) {
            wrapper.last("LIMIT " + limit);
        }
    }

    // ============================================================================
    // 内部类型分发方法
    // ============================================================================

    /** 根据 Meta 类型应用字段选择 */
    private static void applyFieldsByMetaType(QueryWrapper<?> wrapper, BaseMeta meta) {
        if (meta instanceof SelectMeta) {
            // SELECT 查询：应用指定的查询字段
            SmartQueryUtil.applyFields(wrapper, ((SelectMeta) meta).fields);
        } else if (meta instanceof PageMeta) {
            // 分页查询：应用指定的查询字段
            SmartQueryUtil.applyFields(wrapper, ((PageMeta) meta).fields);
        }
        // DELETE 查询没有字段选择配置
    }

    /** 根据 Meta 类型应用查询条件 */
    private static void applyWhereByMetaType(QueryWrapper<?> wrapper, BaseMeta meta, Object[] args) {
        if (meta instanceof SelectMeta) {
            // SELECT 查询：应用 WHERE 条件
            SmartQueryUtil.applyWhere(wrapper, ((SelectMeta) meta).where, args);
        } else if (meta instanceof PageMeta) {
            // 分页查询：应用 WHERE 条件
            SmartQueryUtil.applyWhere(wrapper, ((PageMeta) meta).where, args);
        } else if (meta instanceof DeleteMeta) {
            // 删除查询：应用 WHERE 条件
            SmartQueryUtil.applyWhere(wrapper, ((DeleteMeta) meta).where, args);
        }
    }
}