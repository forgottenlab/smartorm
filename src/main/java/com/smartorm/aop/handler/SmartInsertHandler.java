package com.smartorm.aop.handler;

import com.smartorm.exception.SmartOrmException;
import com.smartorm.executor.SmartMapperExecutor;
import com.smartorm.model.SmartContext;
import com.smartorm.resover.SmartInsertResolver;
import com.smartorm.resover.meta.InsertMeta;
import com.smartorm.util.SmartInsertUtil;
import com.smartorm.annotation.SmartInsert;
import org.springframework.stereotype.Component;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details
 * SmartInsert 注解执行处理器<br>
 * 支持基于 @SmartInsert 的实体构建与执行
 * @CreateDate 2025/12/11
 * @LastModified 2025/12/15
 * @VersionHistory
 * v1.0.0 2025/12/11
 * - 初始版本：通过注解解析实体，构建 QueryWrapper，执行 Mapper 方法（使用 MP 反射直接调用 BaseMapper）<br>
 * v1.1.0 2025/12/15
 * - 优化版本：修改执行 Mapper 方法（使用新增的 martMapperExecutor 中的 insert 方法）<br>
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
                    ctx.getMethod(), ann, ctx.getMapperInterface()
            );

            // 2. 根据 Meta 构建实体对象
            Object entity = SmartInsertUtil.buildEntityFromInsertMeta(meta, ctx.getArgs());

            // 3. 执行 Mapper 方法
            return SmartMapperExecutor.insert(ctx.getMapper(), entity);

        } catch (Exception e) {
            throw new SmartOrmException("处理 @SmartInsert 发生异常", e);
        }
    }
}


