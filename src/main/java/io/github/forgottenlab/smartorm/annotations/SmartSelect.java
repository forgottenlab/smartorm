package io.github.forgottenlab.smartorm.annotations;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 智能查询注解
 * @CreateDate 2025/11/25
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SmartSelect {

    /** 查询字段 */
    String[] fields() default {};

    /** 查询条件 */
    String where() default "";

    /** 排序字段 */
    String orderBy() default "";

    /** 是否降序 */
    boolean desc() default false;

    /** 限制条数，默认 0 表示不限制 */
    int limit() default 0;

    /** 表连接声明 */
    SmartJoin[] join() default {};

    /**
     * 查询结果类型。
     * 默认 AUTO，由框架根据方法返回类型自动推断。
     */
    SelectResultType resultType() default SelectResultType.AUTO;

}