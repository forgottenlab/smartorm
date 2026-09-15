package io.github.forgottenlab.smartorm.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartMapper 实体泛型解析工具类
 * @CreateDate 2025/12/10
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public final class SmartEntityGenericUtil {

    private SmartEntityGenericUtil() {
    }

    /**
     * 解析 Mapper 接口或其代理实现类中声明的实体类型 T
     */
    public static Class<?> resolveEntityClass(Class<?> candidate) {
        if (candidate == null) {
            return null;
        }

        Class<?> fromInterfaces = extractFromInterfaces(candidate);
        if (fromInterfaces != null) {
            return fromInterfaces;
        }

        Class<?> sup = candidate.getSuperclass();
        while (sup != null && sup != Object.class) {
            fromInterfaces = extractFromInterfaces(sup);
            if (fromInterfaces != null) {
                return fromInterfaces;
            }
            sup = sup.getSuperclass();
        }

        return null;
    }

    /**
     * 从接口泛型中提取 SmartMapper<T> 的 T
     */
    private static Class<?> extractFromInterfaces(Class<?> clazz) {
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        if (genericInterfaces == null || genericInterfaces.length == 0) {
            return null;
        }

        for (Type type : genericInterfaces) {
            if (!(type instanceof ParameterizedType)) {
                continue;
            }

            ParameterizedType pt = (ParameterizedType) type;
            Type rawType = pt.getRawType();

            if (rawType instanceof Class<?>
                    && ((Class<?>) rawType).getSimpleName().equals("SmartMapper")) {

                Type[] actualTypes = pt.getActualTypeArguments();
                if (actualTypes.length > 0 && actualTypes[0] instanceof Class<?>) {
                    return (Class<?>) actualTypes[0];
                }
            }
        }

        return null;
    }
}