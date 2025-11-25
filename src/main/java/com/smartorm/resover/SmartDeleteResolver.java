package com.smartorm.resover;

import com.smartorm.annotation.SmartDelete;

/**
 * @author <a href="#">Forgotten.</a>
 * @Details 注解 @SmartDelete的解析器
 * @CreateDate 2025/11/25
 * @LastModified 2025/11/25
 * @VersionHistory [版本历史]
 */
public class SmartDeleteResolver {

    public static class DeleteMeta {
        public String where;
    }

    public static DeleteMeta resolve(SmartDelete annotation) {
        DeleteMeta meta = new DeleteMeta();
        meta.where = annotation.where();
        return meta;
    }
}