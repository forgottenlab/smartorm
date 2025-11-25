package com.smartorm.resover;

import com.smartorm.annotation.SmartInsert;

/**
 * @author <a href="#">Forgotten.</a>
 * @Details 注解 @SmartInsert的解析器
 * @CreateDate 2025/11/25
 * @LastModified 2025/11/25
 * @VersionHistory [版本历史]
 */
public class SmartInsertResolver {

    public static class InsertMeta {

        public String[] fields;
        public String[] values;
        public Class<?> entityClass;
    }

    public static InsertMeta resolve(SmartInsert annotation,Class<?> entityClass) {
        InsertMeta meta = new InsertMeta();
        meta.entityClass = entityClass;
        meta.fields = annotation.fields();
        meta.values = annotation.values();
        return meta;
    }
}