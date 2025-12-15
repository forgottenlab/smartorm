package com.smartorm.aop.handler;

import com.smartorm.exception.SmartOrmException;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details
 * SmartHandler 注册中心<br>
 * 在启动阶段自动收集并注册所有 SmartHandler
 * @CreateDate 2025/12/11
 * @LastModified 2025/12/11
 * @VersionHistory [版本历史]
 */
@Component
public class SmartHandlerRegistry {

    /**
     * 注解类型 -> 对应的 SmartHandler<br>
     * 使用 Map 的原因：<br>
     * - 避免运行期遍历 Handler 列表<br>
     * - 提供 O(1) 的查找效率
     */
    private final Map<Class<? extends Annotation>, SmartHandler<?>> handlerMap = new HashMap<>();

    /** 构造时由 Spring 注入所有 SmartHandler 实现 */
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

    /** 根据注解类型获取对应的 SmartHandler */
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
