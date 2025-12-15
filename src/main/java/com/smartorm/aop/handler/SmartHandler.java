package com.smartorm.aop.handler;

import com.smartorm.model.SmartContext;

import java.lang.annotation.Annotation;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details Smart 注解处理器统一接口
 * @CreateDate 2025/12/11
 * @LastModified 2025/12/11
 * @VersionHistory [版本历史]
 */
public interface SmartHandler<A extends Annotation> {

    /**
     * 返回当前 Handler 所支持的注解类型
     * 例如：SmartSelect.class
     */
    Class<A> supportedAnnotation();

    /**
     * 处理注解逻辑的核心入口
     *
     * @param annotation 当前方法上的 Smart 注解实例
     * @param ctx        SmartContext（封装 Mapper / Method / 参数等运行时信息）
     * @return           数据库操作结果
     * @throws Exception 交由上层统一处理
     */
    Object handle(A annotation, SmartContext ctx) throws Exception;
}