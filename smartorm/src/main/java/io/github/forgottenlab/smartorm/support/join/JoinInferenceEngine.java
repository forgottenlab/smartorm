package io.github.forgottenlab.smartorm.support.join;

import io.github.forgottenlab.smartorm.exception.SmartOrmException;
import io.github.forgottenlab.smartorm.resolver.meta.ForeignKeyMeta;
import io.github.forgottenlab.smartorm.resolver.meta.JdbcMetaProvider;
import io.github.forgottenlab.smartorm.resolver.meta.JoinMeta;

import java.util.List;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details JOIN 条件推断引擎
 * @CreateDate 2025/12/16
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public final class JoinInferenceEngine {

    private static JdbcMetaProvider metaProvider;

    private JoinInferenceEngine() {
    }

    /**
     * 在自动装配阶段注入元数据提供器
     */
    public static void init(JdbcMetaProvider provider) {
        metaProvider = provider;
    }

    /**
     * 自动补全 JOIN 的 ON 条件
     *
     * @param mainTable 主表名
     * @param joins     JoinMeta 列表
     */
    public static void inferOnCondition(String mainTable, List<JoinMeta> joins) {

        // 1. 优先尝试外键推断
        if (metaProvider != null) {
            tryInferByForeignKey(mainTable, joins);
        }

        // 2. 处理每一个 JOIN
        for (JoinMeta join : joins) {

            if (join.on == null || join.on.isBlank()) {
                String inferred = inferByConvention(mainTable, join.joinTable, join.alias);
                join.on = inferred;
                join.userDefinedOn = false;
            } else {
                join.on = normalizeUserOn(join.on);
                join.userDefinedOn = true;
            }

            if (join.on == null || join.on.isBlank()) {
                throw new SmartOrmException(
                        String.format("无法为表 [%s] JOIN [%s] 推断 ON 条件", mainTable, join.joinTable)
                );
            }
        }
    }

    private static String normalizeUserOn(String on) {
        String cleaned = on.trim();

        if (cleaned.contains(";")) {
            throw new SmartOrmException("JOIN ON 条件中不允许出现分号");
        }

        String lower = cleaned.toLowerCase();
        if (lower.contains(" drop ")
                || lower.contains(" delete ")
                || lower.contains(" insert ")
                || lower.contains(" update ")) {
            throw new SmartOrmException("非法 JOIN ON 条件");
        }

        return cleaned;
    }

    /**
     * 基于数据库外键元数据推断 ON 条件
     */
    private static void tryInferByForeignKey(String mainTable, List<JoinMeta> joins) {
        try {
            List<ForeignKeyMeta> foreignKeys = metaProvider.loadForeignKeys(mainTable);

            for (JoinMeta join : joins) {
                if (join.on != null && !join.on.isEmpty()) {
                    continue;
                }

                String inferredOn = inferByForeignKey(mainTable, join.joinTable, foreignKeys);
                if (inferredOn != null) {
                    join.on = inferredOn;
                }
            }
        } catch (Exception ignored) {
            // 外键推断失败时，回退到命名约定推断
        }
    }

    /**
     * 基于外键元数据推断单个 JOIN 的 ON 条件
     */
    private static String inferByForeignKey(
            String mainTable,
            String joinTable,
            List<ForeignKeyMeta> foreignKeys
    ) {
        for (ForeignKeyMeta fk : foreignKeys) {
            if (fk.pkTable.equalsIgnoreCase(mainTable)
                    && fk.fkTable.equalsIgnoreCase(joinTable)) {
                return String.format(
                        "%s.%s = %s.%s",
                        mainTable,
                        fk.pkColumn,
                        joinTable,
                        fk.fkColumn
                );
            }

            if (fk.fkTable.equalsIgnoreCase(mainTable)
                    && fk.pkTable.equalsIgnoreCase(joinTable)) {
                return String.format(
                        "%s.%s = %s.%s",
                        mainTable,
                        fk.fkColumn,
                        joinTable,
                        fk.pkColumn
                );
            }
        }
        return null;
    }

    /**
     * 基于命名约定推断 ON 条件（回退方案）
     */
    private static String inferByConvention(
            String mainTable,
            String joinTable,
            String alias
    ) {
        String mainAlias = mainTable;
        String joinAlias = (alias == null || alias.isEmpty()) ? joinTable : alias;
        String fkField = mainTable + "_id";

        return String.format(
                "%s.id = %s.%s",
                mainAlias,
                joinAlias,
                fkField
        );
    }
}