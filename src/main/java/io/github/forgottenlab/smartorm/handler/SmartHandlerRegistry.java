package io.github.forgottenlab.smartorm.handler;

import io.github.forgottenlab.smartorm.exception.SmartOrmException;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartHandler 注册中心
 * @CreateDate 2025/12/11
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@Component
public class SmartHandlerRegistry {

    /**
     * 注解类型与对应 SmartHandler 的映射关系
     */
    private final Map<Class<? extends Annotation>, SmartHandler<?>> handlerMap = new HashMap<>();

    /**
     * 构造时由 Spring 自动注入所有 SmartHandler 实现
     */
    public SmartHandlerRegistry(List<SmartHandler<?>> handlers) {
        for (SmartHandler<?> handler : handlers) {
            Class<? extends Annotation> annType = handler.supportedAnnotation();

            if (handlerMap.containsKey(annType)) {
                throw new SmartOrmException(
                        "重复注册 SmartHandler，注解类型：" + annType.getName()
                );
            }

            handlerMap.put(annType, handler);
        }
    }

    /**
     * 根据注解类型获取对应的 SmartHandler
     */
    @SuppressWarnings("unchecked")
    public <A extends Annotation> SmartHandler<A> getHandler(Class<A> annType) {
        SmartHandler<?> handler = handlerMap.get(annType);

        if (handler == null) {
            throw new SmartOrmException(
                    "未注册 SmartHandler，注解类型：" + annType.getName()
            );
        }

        return (SmartHandler<A>) handler;
    }
}