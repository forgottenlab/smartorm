package com.smartorm.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details
 * SmartORM 实体泛型解析工具类<br>
 * 用于从 Mapper 接口或其代理类中，解析出 SmartMapper<T> 中声明的实体类型 T<br>
 * 该工具主要解决以下问题：<br>
 * 1.Mapper 接口直接声明 SmartMapper< User ><br>
 * 2.Spring / MyBatis 生成的代理类导致泛型信息丢失<br>
 * 3.CGLIB / JDK 动态代理场景下无法直接获取泛型（只是了解到可能会出现但是目前未遇到）<br>
 * 适用场景：<br>
 * - AOP 中构建 SmartContext 时解析 entityClass<br>
 * - SmartInsert / SmartSelect 等 Handler 中确定实体类型
 * @CreateDate 2025/12/10
 * @LastModified 2025/12/10
 * @VersionHistory
 * v1.0.0 2025/12/10
 * - 初始版本：支持从接口及父类链中解析 SmartMapper<T> 的实体泛型
 */
public final class SmartEntityGenericUtil {

    /** 工具类禁止实例化 */
    private SmartEntityGenericUtil() {}

    /**
     * 解析 Mapper 接口或其代理实现类中声明的实体类型 T<br>
     * 解析顺序：<br>
     * 1.优先从当前类的接口泛型中解析</li>
     * 2.若失败，则沿着父类链向上查找（应对代理类场景）<br>
     * @param candidate Mapper 接口或代理实现类
     * @return 实体 Class；若未解析到则返回 null
     */
    public static Class<?> resolveEntityClass(Class<?> candidate) {
        if (candidate == null) return null;

        // 1. 先尝试直接从类的 genericInterfaces 中解析
        Class<?> fromInterfaces = extractFromInterfaces(candidate);
        if (fromInterfaces != null) return fromInterfaces;

        // 2. 尝试从父类链上解析（防止 CGLIB / 代理）
        Class<?> sup = candidate.getSuperclass();
        while (sup != null && sup != Object.class) {
            fromInterfaces = extractFromInterfaces(sup);
            if (fromInterfaces != null) return fromInterfaces;
            sup = sup.getSuperclass();
        }

        return null;
    }

    /**
     * 从指定类的接口泛型中提取 SmartMapper<T> 的 T<br>
     * 仅当接口满足以下条件时才会解析：<br>
     * 1.接口是 ParameterizedType<br>
     * 2.原始类型为 SmartMapper<br>
     * 3.泛型参数为具体 Class
     * @param clazz 待解析的类
     * @return 实体 Class；未匹配返回 null
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

            // 仅处理 SmartMapper<T>
            if (rawType instanceof Class
                    && ((Class<?>) rawType).getSimpleName().equals("SmartMapper")) {

                Type[] actualTypes = pt.getActualTypeArguments();
                if (actualTypes.length > 0 && actualTypes[0] instanceof Class) {
                    return (Class<?>) actualTypes[0];
                }
            }
        }

        return null;
    }
}
