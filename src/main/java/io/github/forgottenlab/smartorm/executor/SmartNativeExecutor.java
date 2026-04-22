package io.github.forgottenlab.smartorm.executor;

import io.github.forgottenlab.smartorm.annotations.SelectResultType;
import io.github.forgottenlab.smartorm.exception.SmartOrmException;
import io.github.forgottenlab.smartorm.mapper.SmartNativeMapper;
import io.github.forgottenlab.smartorm.model.PageResult;
import io.github.forgottenlab.smartorm.resolver.meta.PageMeta;
import io.github.forgottenlab.smartorm.resolver.meta.SelectMeta;
import io.github.forgottenlab.smartorm.sql.model.RenderedPageSql;
import io.github.forgottenlab.smartorm.sql.model.RenderedSql;
import io.github.forgottenlab.smartorm.support.mapping.SmartBeanMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 原生 SQL 执行器
 * @CreateDate 2025/12/22
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@Component
public class SmartNativeExecutor {

    private final SmartNativeMapper nativeMapper;

    public SmartNativeExecutor(SmartNativeMapper nativeMapper) {
        this.nativeMapper = nativeMapper;
    }

    /**
     * 执行普通原生查询
     */
    public Object execute(
            RenderedSql rendered,
            SelectMeta meta,
            SelectResultType mode
    ) {
        List<Map<String, Object>> maps = nativeMapper.selectBySql(
                rendered.getSql(),
                rendered.getParams()
        );

        switch (mode) {
            case MAP_LIST:
                return maps;

            case DTO_LIST:
                return maps.stream()
                        .map(row -> SmartBeanMapper.map(row, meta.dtoClass))
                        .toList();

            case ENTITY_LIST:
                throw new SmartOrmException(
                        "原生 SQL 不支持 ENTITY_LIST，请使用 MyBatis-Plus 查询路径"
                );

            case AUTO:
            default:
                return maps.isEmpty() ? null : maps.get(0);
        }
    }

    /**
     * 执行原生分页查询
     */
    public PageResult<Map<String, Object>> executePage(
            RenderedPageSql rendered,
            PageMeta meta
    ) {
        // 1. 查询总数
        Long total = nativeMapper.selectCountBySql(
                rendered.getCountSql(),
                rendered.getParams()
        );

        // 2. 查询分页数据
        List<Map<String, Object>> rows = nativeMapper.selectBySql(
                rendered.getPageSql(),
                rendered.getParams()
        );

        // 3. 统一返回 PageResult
        return new PageResult<>(
                meta.page,
                meta.pageSize,
                total,
                rows
        );
    }
}