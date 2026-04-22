package io.github.forgottenlab.smartorm.support.mapping;

import io.github.forgottenlab.smartorm.exception.SmartOrmException;
import io.github.forgottenlab.smartorm.util.SmartFieldUtil;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details Map 到 Java Bean 的映射工具类
 * @CreateDate 2025/12/17
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public final class SmartBeanMapper {

    private SmartBeanMapper() {
    }

    /**
     * 将 Map 数据映射为 DTO / VO / 普通 Java Bean
     */
    public static <T> T map(Map<String, Object> source, Class<T> targetType) {
        try {
            T target = targetType.getDeclaredConstructor().newInstance();

            for (Field field : targetType.getDeclaredFields()) {
                field.setAccessible(true);
                String javaFieldName = field.getName();

                for (Map.Entry<String, Object> entry : source.entrySet()) {
                    String mapKey = entry.getKey();
                    String convertedFieldName = SmartFieldUtil.dbFieldToJavaField(mapKey);

                    if (javaFieldName.equals(convertedFieldName)) {
                        field.set(target, entry.getValue());
                        break;
                    }
                }
            }

            return target;
        } catch (Exception e) {
            throw new SmartOrmException(
                    "Bean 映射失败: " + targetType.getName(),
                    e
            );
        }
    }
}