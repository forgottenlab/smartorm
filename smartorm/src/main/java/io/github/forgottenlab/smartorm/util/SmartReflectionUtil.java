package io.github.forgottenlab.smartorm.util;

import io.github.forgottenlab.smartorm.exception.SmartOrmException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 反射工具类
 * @CreateDate 2025/12/10
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public final class SmartReflectionUtil {

    private SmartReflectionUtil() {
    }

    /**
     * 解析形参类型
     *
     * @deprecated AOP 模式下通常无需再通过实参数组反推方法签名
     */
    @Deprecated
    public static Class<?>[] getParameterTypes(Object[] args) {
        if (args == null) {
            return new Class[0];
        }

        Class<?>[] types = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = args[i] != null ? args[i].getClass() : Object.class;
        }
        return types;
    }

    /**
     * 查找 Java 字段（支持从数据库字段名转换为驼峰字段名）
     */
    public static Field findJavaFieldOrNull(Class<?> entityClass, String dbFieldOrJavaField) {
        if (entityClass == null || dbFieldOrJavaField == null) {
            return null;
        }

        try {
            return entityClass.getDeclaredField(dbFieldOrJavaField);
        } catch (NoSuchFieldException e) {
            String javaName = SmartFieldUtil.dbFieldToJavaField(dbFieldOrJavaField);
            try {
                return entityClass.getDeclaredField(javaName);
            } catch (NoSuchFieldException ex) {
                throw new SmartOrmException(
                        "找不到字段: " + entityClass.getName() + "#" + dbFieldOrJavaField,
                        e
                );
            }
        }
    }

    /**
     * 反射设置字段值（包括私有字段）
     */
    public static boolean setFieldValue(Object target, Field field, Object value) {
        if (target == null || field == null) {
            return false;
        }

        try {
            boolean accessible = field.canAccess(target);
            field.setAccessible(true);
            field.set(target, value);
            field.setAccessible(accessible);
            return true;
        } catch (Exception e) {
            throw new SmartOrmException("反射设置字段值失败: " + field.getName(), e);
        }
    }

    /**
     * 根据方法名和参数类型获取 Method
     *
     * @deprecated AOP 模式下不应依赖此方法进行方法定位
     */
    @Deprecated
    public static Method getMethodByName(Class<?> clazz, String methodName, Class<?>[] paramTypes) {
        try {
            return clazz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            throw new SmartOrmException(
                    "找不到方法: " + clazz.getName() + "#" + methodName,
                    e
            );
        }
    }
}