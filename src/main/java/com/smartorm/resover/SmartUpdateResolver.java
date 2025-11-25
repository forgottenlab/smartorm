package com.smartorm.resover;

import com.smartorm.annotation.SmartUpdate;

/**
 * @author <a href="#">Forgotten.</a>
 * @Details 注解 @SmartUpdate解析器
 * @CreateDate 2025/11/25
 * @LastModified 2025/11/25
 * @VersionHistory [版本历史]
 */
public class SmartUpdateResolver {

    public static class UpdateMeta {
        public String[] fields;
        public String[] values;
        public String where;
    }

    public static UpdateMeta resolve(SmartUpdate annotation) {
        UpdateMeta meta = new UpdateMeta();
        meta.fields = annotation.fields();
        meta.values = annotation.values();
        meta.where = annotation.where();
        return meta;
    }
}