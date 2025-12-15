package com.smartorm.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details
 * 反射工具类<br>
 * 提供与 Java 类、字段、方法相关的操作封装
 * @CreateDate 2025/12/10
 * @LastModified 2025/12/10
 * @VersionHistory [版本历史]
 */
// TODO：使用自定义的异常SmartOrmException
public final class SmartReflectionUtil {

    /** 工具类禁止实例化 */
    private SmartReflectionUtil(){}

    /**
     * 解析形参类型
     * @deprecated
     */
    public static Class<?>[] getParameterTypes(Object[] args) {
        if (args == null) return new Class[0];
        Class<?>[] types = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = args[i] != null ? args[i].getClass() : Object.class;
        }
        return types;
    }

    /**
     * 查找 Java 字段（支持从 db 字段名 转为驼峰）<br>
     * 返回 Field 或 null（找不到返回 null，便于上层做 fallback）
     */
    public static Field findJavaFieldOrNull(Class<?> entityClass, String dbFieldOrJavaField) {
        if (entityClass == null || dbFieldOrJavaField == null) return null;
        try {
            return entityClass.getDeclaredField(dbFieldOrJavaField);
        } catch (NoSuchFieldException e) {
            // 尝试 db -> java 名称转换
            String javaName = SmartFieldUtil.dbFieldToJavaField(dbFieldOrJavaField);
            try {
                return entityClass.getDeclaredField(javaName);
            } catch (NoSuchFieldException ex) {
                return null;
            }
        }
    }

    /**
     * 反射设置字段值（包括私有字段）<br>
     * 返回 true 表示设置成功，false 表示找不到字段或设置失败（异常被封装为 Runtime）
     */
    public static boolean setFieldValue(Object target, Field field, Object value) {
        if (target == null || field == null) return false;
        try {
            boolean accessible = field.canAccess(target);
            field.setAccessible(true);
            field.set(target, value);
            field.setAccessible(accessible);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("反射设置字段值失败: " + field.getName(), e);
        }
    }

    /**
     * 根据方法名和参数类型获取 Method（非 AOP 场景）
     * @deprecated AOP 模式下不应使用此方法
     */
    @Deprecated
    public static Method getMethodByName(Class<?> clazz, String methodName, Class<?>[] paramTypes) {
        try {
            return clazz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(
                    "找不到方法: " + clazz.getName() + "#" + methodName, e
            );
        }
    }
}
