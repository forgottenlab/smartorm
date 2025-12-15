package com.smartorm.aop.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.smartorm.annotation.SmartPage;
import com.smartorm.builder.SmartWrapperBuilder;
import com.smartorm.exception.SmartOrmException;
import com.smartorm.executor.SmartMapperExecutor;
import com.smartorm.model.SmartContext;
import com.smartorm.resover.SmartPageResolver;
import com.smartorm.resover.meta.PageMeta;
import org.springframework.stereotype.Component;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details
 * SmartPage 注解执行处理器<br>
 * 支持基于 @SmartPage 的实体构建与插入执行
 * @CreateDate 2025/12/11
 * @LastModified 2025/12/11
 * @VersionHistory [版本历史]
 */
// TODO：后续可能和 SmartSelectHandler 合并
@Component
public class SmartPageHandler implements SmartHandler<SmartPage> {

    @Override
    public Class<SmartPage> supportedAnnotation() {
        return SmartPage.class;
    }

    @Override
    public Object handle(SmartPage ann, SmartContext ctx) {

        try {
            // 1. 解析注解为 Meta
            PageMeta meta = SmartPageResolver.resolve(
                    ctx.getMethod(), ann, ctx.getMapperInterface()
            );

            // 2. 构建 QueryWrapper
            QueryWrapper<?> wrapper = SmartWrapperBuilder.buildPageWrapper(
                    meta, ctx.getArgs()
            );

            // 3. 执行 Mapper 方法
            return SmartMapperExecutor.selectPage(
                    ctx.getMapper(),
                    wrapper,
                    meta.page,
                    meta.pageSize
            );
        }
        catch (Exception e) {
            throw new SmartOrmException("处理 @SmartPage 发生异常", e);
        }
    }
}