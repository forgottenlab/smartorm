package com.smartorm.aop;

import com.smartorm.aop.handler.SmartHandler;
import com.smartorm.aop.handler.SmartHandlerRegistry;
import com.smartorm.model.SmartContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details Smart ORM 注解统一入口切面，并委托给对应 Handler
 * @CreateDate 2025/12/11
 * @LastModified 2025/12/15
 * @VersionHistory [版本历史]
 */
@Aspect
@Component
public class SmartAnnotationAspect {

    private final SmartHandlerRegistry registry;

    /** 根据注解类型从 Registry 中获取对应的 */
    public SmartAnnotationAspect(SmartHandlerRegistry registry) {
        this.registry = registry;
    }

    /** SmartORM 注解统一拦截入口 */
    @Around(
            "@annotation(com.smartorm.annotation.SmartSelect) || " +
                    "@annotation(com.smartorm.annotation.SmartInsert) || " +
                    "@annotation(com.smartorm.annotation.SmartUpdate) || " +
                    "@annotation(com.smartorm.annotation.SmartDelete) || " +
                    "@annotation(com.smartorm.annotation.SmartPage)"
    )
    public Object smartOrmEntry(ProceedingJoinPoint joinPoint) throws Throwable {

        // 当前执行的方法
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        // Mapper 代理对象（MyBatis 生成）
        Object mapper = joinPoint.getTarget();

        // 方法参数
        Object[] args = joinPoint.getArgs();

        // Mapper 接口类型
        Class<?> mapperInterface = method.getDeclaringClass();

        // 构建执行上下文（不再包含 entityClass）
        SmartContext ctx = new SmartContext(
                mapper,
                method,
                mapperInterface,
                args
        );

        // 查找方法上的 Smart 注解
        Annotation smartAnnotation = findSmartAnnotation(method);

        // 根据注解类型获取对应的 Handler
        // TODO: findSmartAnnotation中其实已经进行了类型的初步判断，但是编译器无法识别还需要对类型进行校验
        SmartHandler handler = registry.getHandler(smartAnnotation.annotationType());

        // 委托给 Handler 执行
        return handler.handle(smartAnnotation, ctx);
    }

    /**
     * 从方法上查找第一个 SmartORM 注解
     *
     * <p>约定：
     * 一个方法上只能声明一个 Smart 注解，
     * 若声明多个，仅第一个生效。
     */
    private Annotation findSmartAnnotation(Method method) {
        for (Annotation ann : method.getAnnotations()) {
            if (ann.annotationType().getName().startsWith("com.smartorm.annotation"))
                return ann;
        }
        throw new RuntimeException("未找到 smart 注解");
    }
}


