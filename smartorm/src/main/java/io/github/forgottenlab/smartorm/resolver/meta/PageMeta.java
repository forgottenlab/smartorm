package io.github.forgottenlab.smartorm.resolver.meta;

import java.util.List;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartPage 注解的元数据模型
 * @CreateDate 2025/12/10
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public class PageMeta extends BaseMeta implements QuerySemantic {

    /** 查询字段 */
    public String[] fields;

    /** 查询条件 */
    public String where;

    /** 排序字段 */
    public String orderBy;

    /** 是否降序 */
    public boolean desc;

    /** 当前页 */
    public long page;

    /** 每页条数 */
    public long pageSize;

    /** JOIN 元信息 */
    public List<JoinMeta> joins;

    @Override
    public Class<?> getEntityClass() {
        return entityClass;
    }

    @Override
    public String[] getFields() {
        return fields;
    }

    @Override
    public String getWhere() {
        return where;
    }

    @Override
    public String getOrderBy() {
        return orderBy;
    }

    @Override
    public boolean isDesc() {
        return desc;
    }

    @Override
    public List<JoinMeta> getJoins() {
        return joins;
    }
}