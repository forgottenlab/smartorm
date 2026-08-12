package io.github.forgottenlab.smartorm.util;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 字段名映射工具类
 * @CreateDate 2025/12/09
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public final class SmartFieldUtil {

    private SmartFieldUtil() {
    }

    /**
     * 数据库字段名转换为 Java 字段名（下划线 -> 驼峰）
     */
    public static String dbFieldToJavaField(String dbField) {
        if (dbField == null || dbField.isEmpty()) {
            return dbField;
        }

        StringBuilder javaField = new StringBuilder();
        boolean nextUpper = false;

        for (char c : dbField.toCharArray()) {
            if (c == '_') {
                nextUpper = true;
            } else {
                javaField.append(nextUpper ? Character.toUpperCase(c) : c);
                nextUpper = false;
            }
        }
        return javaField.toString();
    }
}