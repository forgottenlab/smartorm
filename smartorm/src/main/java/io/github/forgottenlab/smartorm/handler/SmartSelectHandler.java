package io.github.forgottenlab.smartorm.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.forgottenlab.smartorm.annotations.SelectResultType;
import io.github.forgottenlab.smartorm.annotations.SmartSelect;
import io.github.forgottenlab.smartorm.builder.SmartWrapperBuilder;
import io.github.forgottenlab.smartorm.exception.SmartOrmException;
import io.github.forgottenlab.smartorm.executor.SmartMapperExecutor;
import io.github.forgottenlab.smartorm.executor.SmartNativeExecutor;
import io.github.forgottenlab.smartorm.model.SmartContext;
import io.github.forgottenlab.smartorm.resolver.SmartReturnTypeResolver;
import io.github.forgottenlab.smartorm.resolver.SmartSelectResolver;
import io.github.forgottenlab.smartorm.resolver.meta.SelectMeta;
import io.github.forgottenlab.smartorm.sql.model.RenderedSql;
import io.github.forgottenlab.smartorm.sql.renderer.SmartSelectSqlRenderer;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartSelect 注解执行处理器
 * @CreateDate 2025/12/11
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@Component
public class SmartSelectHandler implements SmartHandler<SmartSelect> {

    private final SmartNativeExecutor nativeExecutor;

    public SmartSelectHandler(SmartNativeExecutor nativeExecutor) {
        this.nativeExecutor = nativeExecutor;
    }

    @Override
    public Class<SmartSelect> supportedAnnotation() {
        return SmartSelect.class;
    }

    @Override
    public Object handle(SmartSelect ann, SmartContext ctx) {
        try {
            // 1. 解析注解为 Meta
            SelectMeta meta = SmartSelectResolver.resolve(
                    ctx.getMethod(),
                    ann,
                    ctx.getMapperInterface()
            );

            // 2. 推断执行模式
            SelectResultType mode = SmartReturnTypeResolver.resolve(
                    ctx.getMethod(),
                    meta
            );

            // 3. 判断是否存在 JOIN 语义
            boolean hasJoin = meta.joins != null && !meta.joins.isEmpty();

            // 4. 无 JOIN：走 MyBatis-Plus Wrapper 路径
            if (!hasJoin) {
                QueryWrapper<?> wrapper = SmartWrapperBuilder.buildSelectWrapper(
                        meta,
                        ctx.getArgs()
                );
                return executeByMode(ctx, wrapper, meta, mode);
            }

            // 5. 有 JOIN：走原生 SQL 路径
            RenderedSql rendered = SmartSelectSqlRenderer.render(meta, ctx.getArgs());

            return nativeExecutor.execute(rendered, meta, mode);

        } catch (Exception e) {
            throw new SmartOrmException("处理 @SmartSelect 失败", e);
        }
    }

    /**
     * 根据结果类型选择具体执行方式
     */
    private Object executeByMode(
            SmartContext ctx,
            QueryWrapper<?> wrapper,
            SelectMeta meta,
            SelectResultType type
    ) {
        switch (type) {
            case MAP_LIST:
                return SmartMapperExecutor.selectMaps(ctx.getMapper(), wrapper);

            case ENTITY_LIST:
                return SmartMapperExecutor.selectList(ctx.getMapper(), wrapper);

            case DTO_LIST:
                return SmartMapperExecutor.selectDtoList(
                        ctx.getMapper(),
                        wrapper,
                        meta.dtoClass
                );

            case AUTO:
            default:
                return SmartMapperExecutor.selectOne(ctx.getMapper(), wrapper);
        }
    }
}