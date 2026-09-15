package io.github.forgottenlab.smartorm.handler;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.github.forgottenlab.smartorm.annotations.SmartUpdate;
import io.github.forgottenlab.smartorm.builder.SmartWrapperBuilder;
import io.github.forgottenlab.smartorm.exception.SmartOrmException;
import io.github.forgottenlab.smartorm.executor.SmartMapperExecutor;
import io.github.forgottenlab.smartorm.model.SmartContext;
import io.github.forgottenlab.smartorm.resolver.SmartUpdateResolver;
import io.github.forgottenlab.smartorm.resolver.meta.UpdateMeta;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartUpdate 注解执行处理器
 * @CreateDate 2025/12/11
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@Component
public class SmartUpdateHandler implements SmartHandler<SmartUpdate> {

    @Override
    public Class<SmartUpdate> supportedAnnotation() {
        return SmartUpdate.class;
    }

    @Override
    public Object handle(SmartUpdate ann, SmartContext ctx) {
        try {
            // 1. 解析注解为 Meta
            UpdateMeta meta = SmartUpdateResolver.resolve(
                    ctx.getMethod(),
                    ann,
                    ctx.getMapperInterface()
            );

            // 2. 构建 UpdateWrapper
            UpdateWrapper<?> wrapper = SmartWrapperBuilder.buildUpdateWrapper(
                    meta,
                    ctx.getArgs()
            );

            // 3. 执行 Mapper 方法
            return SmartMapperExecutor.update(ctx.getMapper(), wrapper);

        } catch (SmartOrmException e) {
            throw e;
        } catch (Exception e) {
            throw new SmartOrmException("处理 @SmartUpdate 发生异常", e);
        }
    }
}
