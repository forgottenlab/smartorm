package io.github.forgottenlab.smartorm.handler;

import io.github.forgottenlab.smartorm.annotations.SmartInsert;
import io.github.forgottenlab.smartorm.exception.SmartOrmException;
import io.github.forgottenlab.smartorm.executor.SmartMapperExecutor;
import io.github.forgottenlab.smartorm.model.SmartContext;
import io.github.forgottenlab.smartorm.resolver.SmartInsertResolver;
import io.github.forgottenlab.smartorm.resolver.meta.InsertMeta;
import io.github.forgottenlab.smartorm.support.insert.SmartInsertUtil;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartInsert 注解执行处理器
 * @CreateDate 2025/12/11
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@Component
public class SmartInsertHandler implements SmartHandler<SmartInsert> {

    @Override
    public Class<SmartInsert> supportedAnnotation() {
        return SmartInsert.class;
    }

    @Override
    public Object handle(SmartInsert ann, SmartContext ctx) {
        try {
            // 1. 解析注解为 Meta
            InsertMeta meta = SmartInsertResolver.resolve(
                    ctx.getMethod(),
                    ann,
                    ctx.getMapperInterface()
            );

            // 2. 根据 Meta 构建实体对象
            Object entity = SmartInsertUtil.buildEntityFromInsertMeta(
                    meta,
                    ctx.getArgs()
            );

            // 3. 执行 Mapper 方法
            return SmartMapperExecutor.insert(ctx.getMapper(), entity);

        } catch (Exception e) {
            throw new SmartOrmException("处理 @SmartInsert 发生异常", e);
        }
    }
}