package com.smartorm.resover;

import com.smartorm.annotation.SmartSelect;
import com.smartorm.resover.meta.SelectMeta;

import java.lang.reflect.Method;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details @SmartSelect注解解析器
 * @CreateDate 2025/11/25
 * @LastModified 2025/12/10
 * @VersionHistory
 * v1.0.0 2025/11/25
 * - 初始版本：对 @SmartSelect 注解的相关信息进行解析<br>
 * v2.0.0 2025/12/10
 * - 版本更迭：将所有解释器中公共的代码提取到 SmartResolverBase 中<br>
 */
public class SmartSelectResolver extends SmartBaseResolver {

    /** 解析 @SmartSelect 注解并生成对应 Meta */
    public static SelectMeta resolve(Method method, SmartSelect ann, Class<?> mapperInterface) {
        SelectMeta meta = new SelectMeta();
        fillCommonMeta(meta, method, mapperInterface);

        meta.fields = ann.fields();
        meta.where = ann.where();
        meta.orderBy = ann.orderBy();
        meta.desc = ann.desc();
        meta.limit = ann.limit();

        System.out.println("limit: " + meta.limit);

        return meta;
    }
}
