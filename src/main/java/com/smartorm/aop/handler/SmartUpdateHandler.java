package com.smartorm.aop.handler;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.smartorm.exception.SmartOrmException;
import com.smartorm.executor.SmartMapperExecutor;
import com.smartorm.model.SmartContext;
import com.smartorm.resover.SmartUpdateResolver;
import com.smartorm.resover.meta.UpdateMeta;
import com.smartorm.builder.SmartWrapperBuilder;
import com.smartorm.annotation.SmartUpdate;
import org.springframework.stereotype.Component;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details
 * SmartUpdate 注解执行处理器<br>
 * 支持基于 @SmartUpdate 的实体构建与执行
 * @CreateDate 2025/12/11
 * @LastModified 2025/12/15
 * @VersionHistory
 * v1.0.0 2025/12/11
 * - 初始版本：通过注解解析实体，构建 QueryWrapper，执行 Mapper 方法（使用 MP 反射直接调用 BaseMapper）<br>
 * v1.1.0 2025/12/15
 * - 优化版本：修改执行 Mapper 方法（使用新增的 SmartMapperExecutor 中的 update 方法）<br>
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
                    ctx.getMethod(), ann, ctx.getMapperInterface()
            );

            // 2. 构建 QueryWrapper
            UpdateWrapper<?> wrapper = SmartWrapperBuilder.buildUpdateWrapper(
                    meta, ctx.getArgs()
            );

            // 3. 执行 Mapper 方法
            return SmartMapperExecutor.update(ctx.getMapper(), wrapper);
        }
        catch (Exception e) {
            throw new SmartOrmException("处理 @SmartUpdate 发生异常", e);
        }
    }
}

