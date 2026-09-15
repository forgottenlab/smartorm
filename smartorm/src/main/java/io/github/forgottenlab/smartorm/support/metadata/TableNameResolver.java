package io.github.forgottenlab.smartorm.support.metadata;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 表名解析工具类
 * @CreateDate 2025/12/16
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public final class TableNameResolver {

    private TableNameResolver() {
    }

    /**
     * 解析实体类对应的表名
     */
    public static String resolve(Class<?> entityClass) {

        TableName tableName = entityClass.getAnnotation(TableName.class);
        if (tableName != null && !tableName.value().isEmpty()) {
            return tableName.value();
        }

        return camelToUnderline(entityClass.getSimpleName());
    }

    /**
     * 驼峰命名转换为下划线命名
     */
    private static String camelToUnderline(String name) {
        return name
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase();
    }
}