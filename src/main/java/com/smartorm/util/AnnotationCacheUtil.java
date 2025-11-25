package com.smartorm.util;

import com.smartorm.annotation.*;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="#">Forgotten.</a>
 * @Details 注解缓存工具类，解决MyBatis代理导致的注解丢失问题
 * @CreateDate 2025/11/25
 * @LastModified 2025/11/25
 * @VersionHistory [版本历史]
 */
public class AnnotationCacheUtil {

    /**
     * 缓存注解
     */
    private static final Map<String, SmartSelect> SELECT_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, SmartPage> PAGE_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, SmartInsert> INSERT_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, SmartUpdate> UPDATE_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, SmartDelete> DELETE_CACHE = new ConcurrentHashMap<>();

    /**
     * 获取 -- @SmartSelect 注解
     * @param mapperInterface   Mapper 接口类
     * @param methodName        方法名
     * @param paramTypes        方法参数类型
     * @return SmartSelect 注解实例
     */
    public static SmartSelect getSmartSelect(Class<?> mapperInterface, String methodName, Class<?>[] paramTypes) {
        String cacheKey = buildCacheKey(mapperInterface, methodName, paramTypes);

        // computeIfAbsent 保证缓存存在，不存在时通过反射获取注解
        return SELECT_CACHE.computeIfAbsent(cacheKey, key -> {
            try {
                Method method = mapperInterface.getMethod(methodName, paramTypes);
                SmartSelect annotation = method.getAnnotation(SmartSelect.class);

                // 必须存在注解
                if (annotation == null) {
                    throw new IllegalArgumentException("方法必须使用@SmartSelect注解: " + methodName);
                }
                return annotation;
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("找不到方法: " + methodName, e);
            }
        });
    }

    /**
     * 获取 -- @SmartPage 注解
     */
    public static SmartPage getSmartPage(Class<?> mapperInterface, String methodName, Class<?>[] paramTypes) {
        String cacheKey = buildCacheKey(mapperInterface, methodName, paramTypes);
        return PAGE_CACHE.computeIfAbsent(cacheKey, key -> {
            try {
                Method method = mapperInterface.getMethod(methodName, paramTypes);
                SmartPage annotation = method.getAnnotation(SmartPage.class);
                if (annotation == null) {
                    throw new IllegalArgumentException("方法必须使用@SmartPage注解: " + methodName);
                }
                return annotation;
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("找不到方法: " + methodName, e);
            }
        });
    }

    /**
     * 获取 -- @SmartInsert 注解
     */
    public static SmartInsert getSmartInsert(Class<?> mapperInterface, String methodName, Class<?>[] paramTypes) {
        String cacheKey = buildCacheKey(mapperInterface, methodName, paramTypes);
        return INSERT_CACHE.computeIfAbsent(cacheKey, key -> {
            try {
                Method method = mapperInterface.getMethod(methodName, paramTypes);
                SmartInsert annotation = method.getAnnotation(SmartInsert.class);
                if (annotation == null) {
                    throw new IllegalArgumentException("方法必须使用@SmartInsert注解: " + methodName);
                }
                return annotation;
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("找不到方法: " + methodName, e);
            }
        });
    }

    /**
     * 获取 -- @SmartUpdate 注解
     */
    public static SmartUpdate getSmartUpdate(Class<?> mapperInterface, String methodName, Class<?>[] paramTypes) {
        String cacheKey = buildCacheKey(mapperInterface, methodName, paramTypes);
        return UPDATE_CACHE.computeIfAbsent(cacheKey, key -> {
            try {
                Method method = mapperInterface.getMethod(methodName, paramTypes);
                SmartUpdate annotation = method.getAnnotation(SmartUpdate.class);
                if (annotation == null) {
                    throw new IllegalArgumentException("方法必须使用@SmartUpdate注解: " + methodName);
                }
                return annotation;
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("找不到方法: " + methodName, e);
            }
        });
    }

    /**
     * 获取 -- @SmartDelete 注解
     */
    public static SmartDelete getSmartDelete(Class<?> mapperInterface, String methodName, Class<?>[] paramTypes) {
        String cacheKey = buildCacheKey(mapperInterface, methodName, paramTypes);
        return DELETE_CACHE.computeIfAbsent(cacheKey, key -> {
            try {
                Method method = mapperInterface.getMethod(methodName, paramTypes);
                SmartDelete annotation = method.getAnnotation(SmartDelete.class);
                if (annotation == null) {
                    throw new IllegalArgumentException("方法必须使用@SmartDelete注解: " + methodName);
                }
                return annotation;
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("找不到方法: " + methodName, e);
            }
        });
    }

    /**
     * 私有方法 -- 构建缓存键<br>
     * 格式：Mapper全类名 + 方法名 + 参数类型名
     */
    private static String buildCacheKey(Class<?> mapperInterface, String methodName, Class<?>[] paramTypes) {
        StringBuilder key = new StringBuilder();
        key.append(mapperInterface.getName()).append(".").append(methodName);
        if (paramTypes != null) {
            for (Class<?> paramType : paramTypes) {
                key.append("_").append(paramType.getSimpleName());
            }
        }
        return key.toString();
    }

    /**
     * TODO: 缓存清除
     */
    public static void clearCache() {
        SELECT_CACHE.clear();
        PAGE_CACHE.clear();
        INSERT_CACHE.clear();
        UPDATE_CACHE.clear();
        DELETE_CACHE.clear();
    }

    /**
     * TODO: 获取缓存大小（用于监控）
     */
    public static int getCacheSize() {
        return SELECT_CACHE.size() + PAGE_CACHE.size() + INSERT_CACHE.size() + UPDATE_CACHE.size() + DELETE_CACHE.size();
    }
}
