package com.smartorm.annotation;

import java.lang.annotation.*;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details 智能分页注解
 * @CreateDate 2025/11/25
 * @LastModified 2025/11/25
 * @VersionHistory [版本历史]
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SmartPage {
    String[] fields() default {};        // 查询字段
    String where() default "";           // 条件
    String orderBy() default "";         // 排序
    boolean desc() default false;        // 是否倒序
    long page() default 1;               // 当前页
    long pageSize() default 10;          // 每页条数
    boolean join() default false;        // TODO:是否需要表连接
}
