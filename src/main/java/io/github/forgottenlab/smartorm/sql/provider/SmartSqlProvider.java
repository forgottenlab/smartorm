package io.github.forgottenlab.smartorm.sql.provider;

import java.util.Map;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 原生 SQL Provider
 * @CreateDate 2025/12/22
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public class SmartSqlProvider {

    /**
     * 从参数中提取最终 SQL
     */
    public String provide(Map<String, Object> params) {
        return (String) params.get("sql");
    }
}