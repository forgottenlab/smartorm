package io.github.forgottenlab.smartorm.resolver.meta;

import io.github.forgottenlab.smartorm.annotations.SelectResultType;

import java.util.List;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartSelect 注解的元数据模型
 * @CreateDate 2025/12/10
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public class SelectMeta extends BaseMeta implements QuerySemantic {

    /** 查询字段 */
    public String[] fields;

    /** 查询条件 */
    public String where;

    /** 排序字段 */
    public String orderBy;

    /** 是否降序 */
    public boolean desc;

    /** JOIN 元信息 */
    public List<JoinMeta> joins;

    /** 限制条数（非分页） */
    public int limit = 0;

    /** DTO 返回类型（仅 DTO_LIST 使用） */
    public Class<?> dtoClass;

    /** 执行结果类型 */
    public SelectResultType resultType = SelectResultType.AUTO;

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