package io.github.forgottenlab.smartorm.annotations;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 查询结果类型定义
 * @CreateDate 2025/12/16
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public enum SelectResultType {

    /** 自动判断返回类型 */
    AUTO,

    /** 返回实体列表（selectList） */
    ENTITY_LIST,

    /** 返回 Map 列表（selectMaps） */
    MAP_LIST,

    /** 返回 DTO / VO 列表 */
    DTO_LIST

}