package com.smartorm.resover;

import com.smartorm.resover.meta.BaseMeta;
import com.smartorm.util.SmartEntityGenericUtil;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details Smart Resolver 抽象基类<br>
 * 统一封装所有 SmartXXXResolver 的公共解析逻辑，包括：<br>
 * - Mapper 方法信息<br>
 * - 返回类型分析（List / PageResult / 单对象）<br>
 * - 实体泛型解析<br>
 *  所有具体 Resolver 仅需关注注解自身字段的解析
 * @CreateDate 2025/12/10
 * @LastModified 2025/12/15
 * @VersionHistory
 * v1.0.0 2025/12/10
 * - 初始版本：抽取公共解析逻辑到 SmartBaseResolver
 * v1.1.0 2025/12/15
 * - 优化版本：为使继承类可以快捷的调用方法 fillCommonMeta 无需 new 对象修改为静态方法并且指定范围为 protected
 */
public abstract class SmartBaseResolver {

    /** 填充 BaseMeta 的公共字段 */
    protected static void fillCommonMeta(BaseMeta meta, Method method, Class<?> mapperInterface) {
        meta.method = method;
        meta.mapperClass = mapperInterface;
        meta.returnType = method.getReturnType();
        meta.entityClass = SmartEntityGenericUtil.resolveEntityClass(mapperInterface);
        meta.isList = List.class.isAssignableFrom(meta.returnType);
        meta.isPageResult = isPageResult(meta.returnType);
    }

    /** 依据返回类型判断是否为 PageResult（按类名判定 */
    private static boolean isPageResult(Class<?> returnType) {
        if (returnType == null) return false;
        try {
            Class<?> pageResultClass = Class.forName("com.smartorm.model.PageResult");
            return pageResultClass.isAssignableFrom(returnType);
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

}
