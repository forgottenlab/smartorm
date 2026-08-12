package io.github.forgottenlab.smartorm.support.annotation;

import io.github.forgottenlab.smartorm.annotations.SmartQuery;
import io.github.forgottenlab.smartorm.annotations.SmartSelect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details Smart 注解辅助工具类
 * @CreateDate 2025/12/26
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public final class SmartAnnotationUtils {

    private SmartAnnotationUtils() {
    }

    /**
     * 获取 SmartQuery 注解，支持从元注解中查找
     */
    public static SmartQuery findSmartQuery(Method method) {

        SmartQuery direct = method.getAnnotation(SmartQuery.class);
        if (direct != null) {
            return direct;
        }

        for (Annotation ann : method.getAnnotations()) {
            SmartQuery meta = ann.annotationType().getAnnotation(SmartQuery.class);
            if (meta != null) {
                return meta;
            }
        }
        return null;
    }

    /**
     * 获取方法上直接声明的 SmartSelect 注解
     */
    public static SmartSelect findSmartSelect(Method method) {
        return method.getAnnotation(SmartSelect.class);
    }
}