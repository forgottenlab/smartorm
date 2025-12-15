package com.smartorm.resover.meta;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details SmartUpdate 注解的元数据类
 * @CreateDate 2025/12/10
 * @LastModified 2025/12/10
 * @VersionHistory [版本历史]
 */
public class UpdateMeta extends BaseMeta {
    public String[] fields;
    public String[] values;
    public String where;
}
