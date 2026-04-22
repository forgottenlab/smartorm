package io.github.forgottenlab.smartorm.resolver.meta;

import java.util.List;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 查询语义统一抽象接口
 * @CreateDate 2025/12/26
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public interface QuerySemantic {

    /** 主实体（FROM 的表） */
    Class<?> getEntityClass();

    /** 查询字段 */
    String[] getFields();

    /** WHERE 条件 */
    String getWhere();

    /** ORDER BY 字段 */
    String getOrderBy();

    /** 是否降序 */
    boolean isDesc();

    /** JOIN 语义 */
    List<JoinMeta> getJoins();
}