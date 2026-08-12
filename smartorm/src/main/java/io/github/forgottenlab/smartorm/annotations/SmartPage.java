package io.github.forgottenlab.smartorm.annotations;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 智能分页注解
 * @CreateDate 2025/11/25
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SmartPage {

    /** 查询字段 */
    String[] fields() default {};

    /** 查询条件 */
    String where() default "";

    /** 排序字段 */
    String orderBy() default "";

    /** 是否降序 */
    boolean desc() default false;

    /** 当前页，从 1 开始 */
    long page() default 1;

    /** 每页条数 */
    long pageSize() default 10;

    /** 表连接声明 */
    SmartJoin[] join() default {};

}