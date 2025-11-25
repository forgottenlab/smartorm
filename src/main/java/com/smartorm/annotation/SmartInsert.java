package com.smartorm.annotation;

import java.lang.annotation.*;

/**
 * @author <a href="#">Forgotten.</a>
 * @Details 智能插入注解
 * @CreateDate 2025/11/25
 * @LastModified 2025/11/25
 * @VersionHistory [版本历史]
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SmartInsert {
    String[] fields() default {};  // 字段名
    String[] values() default {};  // 对应值的占位符，比如 #{0}、#{1}，会按方法参数替换
}

