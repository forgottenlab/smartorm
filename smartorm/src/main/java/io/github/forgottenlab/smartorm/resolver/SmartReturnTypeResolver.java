package io.github.forgottenlab.smartorm.resolver;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.forgottenlab.smartorm.annotations.SelectResultType;
import io.github.forgottenlab.smartorm.resolver.meta.SelectMeta;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 根据方法签名与 Meta 推断查询执行模式
 * @CreateDate 2025/12/17
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public final class SmartReturnTypeResolver {

    private SmartReturnTypeResolver() {
    }

    public static SelectResultType resolve(Method method, SelectMeta meta) {

        // 1. 注解显式指定，优先级最高
        if (meta.resultType != SelectResultType.AUTO) {
            return meta.resultType;
        }

        // 2. List 返回
        if (List.class.isAssignableFrom(meta.returnType)) {

            // JOIN 默认返回 Map 列表
            if (meta.joins != null && !meta.joins.isEmpty()) {
                return SelectResultType.MAP_LIST;
            }

            // List<Map>
            if (isListOfMap(method)) {
                return SelectResultType.MAP_LIST;
            }

            // List<DTO / VO>
            Class<?> generic = getListGenericType(method);
            if (generic != null && isDtoType(generic)) {
                meta.dtoClass = generic;
                return SelectResultType.DTO_LIST;
            }

            // 默认实体列表
            return SelectResultType.ENTITY_LIST;
        }

        // 3. 兜底：单条对象
        return SelectResultType.AUTO;
    }

    private static boolean isListOfMap(Method method) {
        Type type = method.getGenericReturnType();
        if (!(type instanceof ParameterizedType)) {
            return false;
        }

        Type arg = ((ParameterizedType) type).getActualTypeArguments()[0];
        if (arg instanceof Class<?>) {
            return Map.class.isAssignableFrom((Class<?>) arg);
        }
        if (arg instanceof ParameterizedType) {
            return Map.class.isAssignableFrom(
                    (Class<?>) ((ParameterizedType) arg).getRawType()
            );
        }
        return false;
    }

    private static Class<?> getListGenericType(Method method) {
        Type type = method.getGenericReturnType();
        if (!(type instanceof ParameterizedType)) {
            return null;
        }

        Type arg = ((ParameterizedType) type).getActualTypeArguments()[0];
        return arg instanceof Class<?> ? (Class<?>) arg : null;
    }

    private static boolean isDtoType(Class<?> clazz) {
        if (Map.class.isAssignableFrom(clazz)) {
            return false;
        }

        if (clazz.isAnnotationPresent(TableName.class)) {
            return false;
        }

        return true;
    }
}