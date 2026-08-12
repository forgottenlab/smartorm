package io.github.forgottenlab.smartorm.support.insert;

import io.github.forgottenlab.smartorm.exception.SmartOrmException;
import io.github.forgottenlab.smartorm.resolver.meta.InsertMeta;
import io.github.forgottenlab.smartorm.util.SmartExpressionUtil;
import io.github.forgottenlab.smartorm.util.SmartReflectionUtil;

import java.lang.reflect.Field;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartInsert 实体构建工具类
 * @CreateDate 2025/12/10
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public final class SmartInsertUtil {

    private SmartInsertUtil() {
    }

    /**
     * 根据 InsertMeta 构建实体对象
     *
     * @param meta InsertMeta，包含实体类型、字段名和值表达式
     * @param args Mapper 方法调用参数
     * @return 构建完成的实体对象
     */
    public static Object buildEntityFromInsertMeta(InsertMeta meta, Object[] args) {
        try {
            Class<?> entityClass = meta.entityClass;
            Object entity = entityClass.getDeclaredConstructor().newInstance();

            int len = Math.min(
                    meta.fields == null ? 0 : meta.fields.length,
                    meta.values == null ? 0 : meta.values.length
            );

            for (int i = 0; i < len; i++) {
                String dbField = meta.fields[i];
                String expr = meta.values[i];

                Object val = SmartExpressionUtil.parseValueExpression(expr, args);

                Field field = SmartReflectionUtil.findJavaFieldOrNull(entityClass, dbField);
                if (field == null) {
                    throw new SmartOrmException(
                            "找不到实体字段: " + entityClass.getSimpleName() + "." + dbField
                    );
                }

                SmartReflectionUtil.setFieldValue(entity, field, val);
            }

            return entity;

        } catch (Exception e) {
            throw new SmartOrmException("构建实体失败（SmartInsertUtil）", e);
        }
    }
}