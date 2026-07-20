package io.github.forgottenlab.smartorm.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.forgottenlab.smartorm.annotations.SmartDelete;
import io.github.forgottenlab.smartorm.builder.SmartWrapperBuilder;
import io.github.forgottenlab.smartorm.exception.SmartOrmException;
import io.github.forgottenlab.smartorm.executor.SmartMapperExecutor;
import io.github.forgottenlab.smartorm.model.SmartContext;
import io.github.forgottenlab.smartorm.resolver.SmartDeleteResolver;
import io.github.forgottenlab.smartorm.resolver.meta.DeleteMeta;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartDelete 注解执行处理器
 * @CreateDate 2025/12/11
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@Component
public class SmartDeleteHandler implements SmartHandler<SmartDelete> {

    @Override
    public Class<SmartDelete> supportedAnnotation() {
        return SmartDelete.class;
    }

    @Override
    public Object handle(SmartDelete ann, SmartContext ctx) {
        try {
            // 1. 解析注解为 Meta
            DeleteMeta meta = SmartDeleteResolver.resolve(
                    ctx.getMethod(),
                    ann,
                    ctx.getMapperInterface()
            );

            // 2. 构建 QueryWrapper
            QueryWrapper<?> wrapper = SmartWrapperBuilder.buildDeleteWrapper(
                    meta,
                    ctx.getArgs()
            );

            // 3. 执行 Mapper 方法
            return SmartMapperExecutor.delete(ctx.getMapper(), wrapper);

        } catch (SmartOrmException e) {
            throw e;
        } catch (Exception e) {
            throw new SmartOrmException("处理 @SmartDelete 发生异常", e);
        }
    }
}
