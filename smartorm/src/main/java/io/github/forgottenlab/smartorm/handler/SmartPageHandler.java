package io.github.forgottenlab.smartorm.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.forgottenlab.smartorm.annotations.SmartPage;
import io.github.forgottenlab.smartorm.builder.SmartWrapperBuilder;
import io.github.forgottenlab.smartorm.exception.SmartOrmException;
import io.github.forgottenlab.smartorm.executor.SmartMapperExecutor;
import io.github.forgottenlab.smartorm.executor.SmartNativeExecutor;
import io.github.forgottenlab.smartorm.model.SmartContext;
import io.github.forgottenlab.smartorm.resolver.SmartPageResolver;
import io.github.forgottenlab.smartorm.resolver.meta.PageMeta;
import io.github.forgottenlab.smartorm.sql.model.RenderedPageSql;
import io.github.forgottenlab.smartorm.sql.renderer.SmartPageSqlRenderer;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartPage 注解执行处理器
 * @CreateDate 2025/12/11
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@Component
public class SmartPageHandler implements SmartHandler<SmartPage> {

    private final SmartNativeExecutor nativeExecutor;

    public SmartPageHandler(SmartNativeExecutor nativeExecutor) {
        this.nativeExecutor = nativeExecutor;
    }

    @Override
    public Class<SmartPage> supportedAnnotation() {
        return SmartPage.class;
    }

    @Override
    public Object handle(SmartPage ann, SmartContext ctx) {
        try {
            // 1. 解析 Meta
            PageMeta meta = SmartPageResolver.resolve(
                    ctx.getMethod(),
                    ann,
                    ctx.getMapperInterface()
            );

            // 2. 判断是否存在 JOIN 语义
            boolean hasJoin = meta.joins != null && !meta.joins.isEmpty();

            // 3. 无 JOIN：走 MyBatis-Plus 分页
            if (!hasJoin) {
                QueryWrapper<?> wrapper = SmartWrapperBuilder.buildPageWrapper(
                        meta,
                        ctx.getArgs()
                );

                return SmartMapperExecutor.selectPage(
                        ctx.getMapper(),
                        wrapper,
                        meta.page,
                        meta.pageSize
                );
            }

            // 4. 有 JOIN：走原生 SQL 分页
            RenderedPageSql rendered = SmartPageSqlRenderer.render(meta, ctx.getArgs());

            return nativeExecutor.executePage(rendered, meta);

        } catch (Exception e) {
            throw new SmartOrmException("处理 @SmartPage 失败", e);
        }
    }
}