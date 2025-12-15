package com.smartorm.aop.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.smartorm.annotation.SmartSelect;
import com.smartorm.executor.SmartMapperExecutor;
import com.smartorm.builder.SmartWrapperBuilder;
import com.smartorm.exception.SmartOrmException;
import com.smartorm.model.PageResult;
import com.smartorm.model.SmartContext;
import com.smartorm.resover.SmartSelectResolver;
import com.smartorm.resover.meta.SelectMeta;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details
 * SmartSelect 注解执行处理器<br>
 * 支持基于 @SmartSelect 的实体构建与执行
 * @CreateDate 2025/12/11
 * @LastModified 2025/12/11
 * @VersionHistory [版本历史]
 */
@Component
public class SmartSelectHandler implements SmartHandler<SmartSelect> {

    @Override
    public Class<SmartSelect> supportedAnnotation() {
        return SmartSelect.class;
    }

    @Override
    public Object handle(SmartSelect ann, SmartContext ctx) {

        try {
            // 1. 解析注解为 Meta
            SelectMeta meta = SmartSelectResolver.resolve(
                    ctx.getMethod(), ann, ctx.getMapperInterface());

            // 2. 构建 QueryWrapper
            QueryWrapper<?> wrapper = SmartWrapperBuilder.buildSelectWrapper(
                    meta, ctx.getArgs()
            );

            // 3. 根据返回值类型选择执行方式
            Class<?> returnType = ctx.getMethod().getReturnType();

            if (List.class.isAssignableFrom(returnType)) {
                return SmartMapperExecutor.selectList(ctx.getMapper(), wrapper);
            }

            // TODO：分页属于普通查询的加强版在考虑是否将@SmartSelect和@SmartPage合并或继承关系
            // 当前下列的if是不会成立的，因为SmartSelect中没有分页的相关参数（分页大小和页数）
            if (PageResult.class.isAssignableFrom(returnType)) {
                return SmartMapperExecutor.selectPage(ctx.getMapper(), wrapper);
            }

            // 4. 执行 Mapper 方法
            return SmartMapperExecutor.selectOne(ctx.getMapper(), wrapper);
        }
        catch (Exception e) {
            throw new SmartOrmException("处理 @SmartSelect 发生异常", e);
        }
    }
}

