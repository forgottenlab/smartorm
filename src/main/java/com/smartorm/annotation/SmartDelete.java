package com.smartorm.annotation;

import java.lang.annotation.*;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details 智能删除注解
 * @CreateDate 2025/11/25
 * @LastModified 2025/11/25
 * @VersionHistory [版本历史]
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SmartDelete {
    String where() default "";       // 删除条件
}
