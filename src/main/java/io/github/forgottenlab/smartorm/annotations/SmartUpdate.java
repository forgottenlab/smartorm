package io.github.forgottenlab.smartorm.annotations;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 智能更新注解
 * @CreateDate 2025/11/25
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SmartUpdate {

    /** 待更新字段数组 */
    String[] fields() default {};

    /** 与字段一一对应的值表达式数组 */
    String[] values() default {};

    /** 更新条件 */
    String where() default "";

}