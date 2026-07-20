package io.github.forgottenlab.smartorm.annotations;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 智能删除注解
 * @CreateDate 2025/11/25
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SmartDelete {

    /** 删除条件 */
    String where() default "";

    /** 是否显式允许无有效 WHERE 条件的全表删除 */
    boolean allowFullTable() default false;

}
