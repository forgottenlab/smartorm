package io.github.forgottenlab.smartorm.mapper;

import io.github.forgottenlab.smartorm.sql.provider.SmartSqlProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 原生 SQL Mapper 接口
 * @CreateDate 2025/12/22
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@Mapper
public interface SmartNativeMapper {

    /**
     * 执行原生 SQL 查询并返回结果集
     */
    @SelectProvider(type = SmartSqlProvider.class, method = "provide")
    List<Map<String, Object>> selectBySql(
            @Param("sql") String sql,
            @Param("params") List<Object> params
    );

    /**
     * 执行原生 SQL 统计查询
     */
    @SelectProvider(type = SmartSqlProvider.class, method = "provide")
    Long selectCountBySql(
            @Param("sql") String sql,
            @Param("params") List<Object> params
    );
}