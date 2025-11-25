package com.smartorm.resover;

import com.smartorm.annotation.SmartSelect;

/**
 * @author <a href="#">Forgotten.</a>
 * @Details 注解 @SmartSelect解析器
 * @CreateDate 2025/11/25
 * @LastModified 2025/11/25
 * @VersionHistory [版本历史]
 */
public class SmartSelectResolver {

    public static class SelectMeta {
        public String[] fields;
        public String where;
        public String orderBy;
        public boolean desc;
        public int limit;
    }

    public static SelectMeta resolve(SmartSelect annotation) {
        SelectMeta meta = new SelectMeta();
        meta.fields = annotation.fields();
        meta.where = annotation.where();
        meta.orderBy = annotation.orderBy();
        meta.desc = annotation.desc();
        meta.limit = annotation.limit();
        return meta;
    }
}
