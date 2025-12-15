package com.smartorm.resover;

import com.smartorm.annotation.SmartUpdate;
import com.smartorm.resover.meta.UpdateMeta;

import java.lang.reflect.Method;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details @SmartUpdate注解解析器
 * @CreateDate 2025/11/25
 * @LastModified 2025/12/10
 * @VersionHistory
 * v1.0.0 2025/11/25
 * - 初始版本：对 @SmartUpdate 注解的相关信息进行解析<br>
 * v2.0.0 2025/12/10
 * - 版本更迭：将所有解释器中公共的代码提取到 SmartResolverBase 中<br>
 */
public class SmartUpdateResolver extends SmartBaseResolver {

    /** 解析 @SmartUpdate 注解并生成对应 Meta */
    public static UpdateMeta resolve(Method method, SmartUpdate ann, Class<?> mapperInterface) {
        UpdateMeta meta = new UpdateMeta();
        fillCommonMeta(meta, method, mapperInterface);

        meta.fields = ann.fields();
        meta.values = ann.values();
        meta.where = ann.where();

        return meta;
    }
}