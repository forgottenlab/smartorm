package io.github.forgottenlab.smartorm.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartORM 表连接注解
 * @CreateDate 2025/12/16
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SmartJoin {

    /** 被连接的表名 */
    String table();

    /** JOIN 类型，默认 INNER */
    JoinType type() default JoinType.INNER;

    /**
     * 显式指定 ON 条件。
     * 当框架无法自动推断连接条件时，可通过该属性手动指定。
     * 例如：u.id = o.user_id
     */
    String on() default "";

    /**
     * 表别名。
     * 当前版本保留该属性，供 JOIN 场景扩展使用。
     */
    String alias() default "";

}