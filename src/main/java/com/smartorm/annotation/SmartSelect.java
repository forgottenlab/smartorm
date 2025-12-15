package com.smartorm.annotation;

import java.lang.annotation.*;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details 智能查询注解
 * @CreateDate 2025/11/25
 * @LastModified 2025/11/25
 * @VersionHistory [版本历史]
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@SmartQuery  // 包含基本查询功能
public @interface SmartSelect{
    String[] fields() default {};       // 查询字段
    String where() default "";          // 条件
    String orderBy() default "";        // 排序
    boolean desc() default false;       // 是否倒序
    int limit() default 0;              // 限制条数
}