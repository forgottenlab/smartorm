package com.smartorm.resover.meta;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details SmartPage 注解的元数据类
 * @CreateDate 2025/12/10
 * @LastModified 2025/12/10
 * @VersionHistory [版本历史]
 */
public class PageMeta extends BaseMeta {
    public String[] fields;
    public String where;
    public String orderBy;
    public boolean desc;
    public long page;
    public long pageSize;
    public boolean join;
}
