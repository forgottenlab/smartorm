package com.smartorm.aop.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.smartorm.exception.SmartOrmException;
import com.smartorm.executor.SmartMapperExecutor;
import com.smartorm.model.SmartContext;
import com.smartorm.resover.SmartDeleteResolver;
import com.smartorm.resover.meta.DeleteMeta;
import com.smartorm.builder.SmartWrapperBuilder;
import com.smartorm.annotation.SmartDelete;
import org.springframework.stereotype.Component;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details
 * SmartDelete 注解执行处理器<br>
 * 支持基于 @SmartDelete 的实体构建与执行
 * @CreateDate 2025/12/11
 * @LastModified 2025/12/15
 * @VersionHistory
 * v1.0.0 2025/12/11
 * - 初始版本：通过注解解析实体，构建 QueryWrapper，执行 Mapper 方法（使用 MP 反射直接调用 BaseMapper）<br>
 * v1.1.0 2025/12/15
 * - 优化版本：修改执行 Mapper 方法（使用新增的 SmartMapperExecutor 中的 delete 方法）<br>
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
                    ctx.getMethod(), ann, ctx.getMapperInterface()
            );

            // 2. 构建 QueryWrapper
            QueryWrapper<?> wrapper = SmartWrapperBuilder.buildDeleteWrapper(
                    meta, ctx.getArgs()
            );

            // 3. 执行 Mapper 方法
            return SmartMapperExecutor.delete(ctx.getMapper(), wrapper);

        }
        catch (Exception e) {
            throw new SmartOrmException("处理 @SmartDelete 发生异常", e);
        }
    }
}

