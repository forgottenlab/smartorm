package com.smartorm.resover.meta;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details SmartSelect 注解的元数据类
 * @CreateDate 2025/12/10
 * @LastModified 2025/12/10
 * @VersionHistory [版本历史]
 */
public class SelectMeta extends BaseMeta {
    public String[] fields;
    public String where;
    public String orderBy;
    public boolean desc;
    public int limit;
}
