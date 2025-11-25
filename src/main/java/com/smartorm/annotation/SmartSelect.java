package com.smartorm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href="#">Forgotten.</a>
 * @Details 智能查询注解
 * @CreateDate 2025/11/25
 * @LastModified 2025/11/25
 * @VersionHistory [版本历史]
 */
// 智能查询注解
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SmartSelect {
    String[] fields() default {};  // 查询字段
    String where() default "";     // 条件
    String orderBy() default "";   // 排序
    boolean desc() default false;  // 是否倒序
    int limit() default -1;        // 限制条数
}