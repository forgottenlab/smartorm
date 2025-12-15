package com.smartorm.util;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details
 * Smart 字段名映射工具类<br>
 * 用于在数据库字段名（下划线风格）与 Java 属性名（驼峰风格）之间进行转换<br>
 * 该工具类主要用于：<br>
 * - SmartWrapperBuilder 构建条件时字段名映射<br>
 * - SmartInsert / SmartUpdate 元数据解析<br>
 * - 统一处理数据库命名规范与 Java 命名规范差异
 * @CreateDate 2025/12/09
 * @LastModified 2025/12/09
 * @VersionHistory
 * v1.0.0 2025/12/09
 * -初始版本：提供数据库字段名到 Java 字段名的基础转换能力
 */
public class SmartFieldUtil {

    /** 工具类禁止实例化 */
    private SmartFieldUtil() {}

    /**
     * 数据库字段名转换为 Java 字段名（下划线 → 驼峰）<br>
     * 转换规则：<br>
     * - user_name → userName<br>
     * - create_time → createTime<br>
     * - id → id<br>
     * 注意：<br>
     * 1.不会改变首字母大小写<br>
     * 2.连续下划线按一次处理
     * @param dbField 数据库字段名（下划线命名）
     * @return Java 字段名（驼峰命名）
     */
    public static String dbFieldToJavaField(String dbField) {
        if (dbField == null || dbField.isEmpty()) return dbField;

        StringBuilder javaField = new StringBuilder();
        boolean nextUpper = false;

        for (char c : dbField.toCharArray()) {
            if (c == '_') {
                // 下划线不输出，标记下一个字符需要大写
                nextUpper = true;
            } else {
                // 根据标记决定是否转为大写
                javaField.append(nextUpper ? Character.toUpperCase(c) : c);
                nextUpper = false;
            }
        }
        return javaField.toString();
    }
}
