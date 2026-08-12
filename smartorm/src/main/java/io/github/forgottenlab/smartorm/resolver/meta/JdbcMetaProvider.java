package io.github.forgottenlab.smartorm.resolver.meta;

import io.github.forgottenlab.smartorm.exception.SmartOrmException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 基于 JDBC 的数据库元数据提供器
 * @CreateDate 2025/12/16
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public class JdbcMetaProvider {

    private final DataSource dataSource;

    public JdbcMetaProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 查询指定表相关的所有外键关系
     */
    public List<ForeignKeyMeta> loadForeignKeys(String table) {
        List<ForeignKeyMeta> result = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            String catalog = conn.getCatalog();

            // 查询被其他表引用的外键
            try (ResultSet rs = meta.getExportedKeys(catalog, null, table)) {
                while (rs.next()) {
                    result.add(extractForeignKey(rs));
                }
            }

            // 查询当前表引用的外键
            try (ResultSet rs = meta.getImportedKeys(catalog, null, table)) {
                while (rs.next()) {
                    result.add(extractForeignKey(rs));
                }
            }

        } catch (SQLException e) {
            throw new SmartOrmException("读取数据库外键元数据失败", e);
        }

        return result;
    }

    /**
     * 从 ResultSet 中提取外键信息
     */
    private static ForeignKeyMeta extractForeignKey(ResultSet rs) throws SQLException {
        ForeignKeyMeta fk = new ForeignKeyMeta();
        fk.pkTable = rs.getString("PKTABLE_NAME");
        fk.pkColumn = rs.getString("PKCOLUMN_NAME");
        fk.fkTable = rs.getString("FKTABLE_NAME");
        fk.fkColumn = rs.getString("FKCOLUMN_NAME");
        return fk;
    }
}