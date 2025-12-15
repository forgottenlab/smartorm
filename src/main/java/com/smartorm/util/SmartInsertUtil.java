package com.smartorm.util;

import com.smartorm.resover.meta.InsertMeta;

import java.lang.reflect.Field;

/**
 * @author <a href="mailto:wangheran55@gmail.com">Forgotten.</a>
 * @Details
 * SmartORM 插入实体构建工具类<br>
 * <br>用于根据 {@link InsertMeta} 中解析得到的字段信息，动态构建实体对象并为其字段赋值<br>
 * 该工具类主要服务于：<br>
 * - @SmartInsert 注解的 Handler 处理流程<br>
 * - 将注解描述的字段/值转换为真实可插入的实体对象<br>
 * 核心能力：<br>
 * 1.支持 #{n} 表达式解析<br>
 * 2.支持数据库字段名 → Java 字段名映射<br>
 * 3.基于反射安全地为实体字段赋值
 * @CreateDate 2025/12/10
 * @LastModified 2025/12/10
 * @VersionHistory v1.0.0 2025/12/10
 * - 初始版本：根据 InsertMeta 构建实体对象，用于 SmartInsert 执行流程
 */
// TODO：后续替换 RuntimeException 为 SmartOrmException
public final class SmartInsertUtil {

    /** 工具类禁止实例化 */
    private SmartInsertUtil() {}

    /**
     * 根据 SmartInsert 解析得到的 InsertMeta 构建实体对象<br>
     * 构建流程：<br>
     * 1.通过 meta.entityClass 创建实体实例<br>
     * 2.遍历 fields / values 数组<br>
     * 3.解析表达式值（支持 #{n}、字面量等）<br>
     * 4.将数据库字段名映射为 Java 字段名<br>
     * 5.使用反射为实体字段赋值
     * @param meta InsertMeta，包含实体类型、字段名、值表达式等信息
     * @param args Mapper 方法调用时传入的参数数组
     * @return 构建完成并赋值后的实体对象
     */
    public static Object buildEntityFromInsertMeta(InsertMeta meta, Object[] args) {
        try {
            // 1. 创建实体实例
            Class<?> entityClass = meta.entityClass;
            Object entity = entityClass.getDeclaredConstructor().newInstance();

            // 2. 防御性处理 fields / values 长度不一致问题
            int len = Math.min(
                    meta.fields == null ? 0 : meta.fields.length,
                    meta.values == null ? 0 : meta.values.length
            );

            // 3. 逐字段赋值
            for (int i = 0; i < len; i++) {
                String dbField = meta.fields[i];  // 数据库字段名
                String expr = meta.values[i];     // 值表达式（如 #{0}、'abc'）

                // 3.1 解析表达式为真实值
                Object val = SmartExpressionUtil.parseValueExpression(expr, args);

                // 3.2 查找并映射到 Java 字段
                Field field = SmartReflectionUtil.findJavaFieldOrNull(entityClass, dbField);
                if (field == null) {
                    throw new RuntimeException(
                            "找不到实体字段: " + entityClass.getSimpleName() + "." + dbField
                    );
                }

                // 3.3 反射赋值
                SmartReflectionUtil.setFieldValue(entity, field, val);
            }

            return entity;

        } catch (Exception e) {
            throw new RuntimeException("构建实体失败（SmartInsertUtil）", e);
        }
    }
}
