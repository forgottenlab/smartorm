package io.github.forgottenlab.smartorm.annotations;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 通用查询注解预留定义
 * @CreateDate 2025/12/15
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SmartQuery {

    /** 查询字段 */
    String[] fields() default {};

    /** 查询条件 */
    String where() default "";

    /** 排序字段 */
    String orderBy() default "";

    /** 是否降序 */
    boolean desc() default false;

    /** 表连接声明 */
    SmartJoin[] join() default {};

}