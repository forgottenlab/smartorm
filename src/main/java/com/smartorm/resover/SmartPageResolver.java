package com.smartorm.resover;

import com.smartorm.annotation.SmartPage;

/**
 * @author <a href="#">Forgotten.</a>
 * @Details 注解 @SmartPage解析器
 * @CreateDate 2025/11/25
 * @LastModified 2025/11/25
 * @VersionHistory [版本历史]
 */
public class SmartPageResolver {

    public static class PageMeta {
        public String[] fields;
        public String where;
        public String orderBy;
        public boolean desc;
        public long page;
        public long pageSize;
        public boolean join;
    }

    public static PageMeta resolve(SmartPage annotation) {
        PageMeta meta = new PageMeta();
        meta.fields = annotation.fields();
        meta.where = annotation.where();
        meta.orderBy = annotation.orderBy();
        meta.desc = annotation.desc();
        meta.page = annotation.page();
        meta.pageSize = annotation.pageSize();
        meta.join = annotation.join();
        return meta;
    }
}