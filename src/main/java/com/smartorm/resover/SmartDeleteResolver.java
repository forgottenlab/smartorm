package com.smartorm.resover;

import com.smartorm.annotation.SmartDelete;
import com.smartorm.resover.meta.DeleteMeta;

import java.lang.reflect.Method;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details @SmartDelete注解解析器
 * @CreateDate 2025/11/25
 * @LastModified 2025/12/10
 * @VersionHistory
 * v1.0.0 2025/11/25
 * - 初始版本：对 @SmartDelete 注解的相关信息进行解析<br>
 * v2.0.0 2025/12/10
 * - 版本更迭：将所有解释器中公共的代码提取到 SmartResolverBase 中<br>
 */
public class SmartDeleteResolver extends SmartBaseResolver {

    /** 解析 @SmartDelete 注解并生成对应 Meta */
    public static DeleteMeta resolve(Method method, SmartDelete ann, Class<?> mapperInterface) {
        DeleteMeta meta = new DeleteMeta();
        fillCommonMeta(meta, method, mapperInterface);

        meta.where = ann.where();
        return meta;
    }
}