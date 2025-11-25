package com.smartorm.annotation;

import java.lang.annotation.*;

/**
 * @author <a href="#">Forgotten.</a>
 * @Details 智能更新注解
 * @CreateDate 2025/11/25
 * @LastModified 2025/11/25
 * @VersionHistory [版本历史]
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SmartUpdate {
    String[] fields() default {}; // 修改字段
    String[] values() default {}; // 对应值占位符
    String where() default "";    // 条件
}

