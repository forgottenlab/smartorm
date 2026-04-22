package io.github.forgottenlab.smartorm.aspect;

import io.github.forgottenlab.smartorm.exception.SmartOrmException;
import io.github.forgottenlab.smartorm.handler.SmartHandler;
import io.github.forgottenlab.smartorm.handler.SmartHandlerRegistry;
import io.github.forgottenlab.smartorm.mapper.SmartMapper;
import io.github.forgottenlab.smartorm.model.SmartContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartORM 注解统一入口切面，并委托给对应 Handler
 * @CreateDate 2025/12/11
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@Aspect
@Component
public class SmartAnnotationAspect {

    private static final String SMART_ANNOTATION_PACKAGE =
            "io.github.forgottenlab.smartorm.annotations";

    private final SmartHandlerRegistry registry;

    /**
     * 构造注入 SmartHandler 注册中心
     */
    public SmartAnnotationAspect(SmartHandlerRegistry registry) {
        this.registry = registry;
    }

    /**
     * SmartORM 注解统一拦截入口
     */
    @Around(
            "@annotation(io.github.forgottenlab.smartorm.annotations.SmartSelect) || " +
                    "@annotation(io.github.forgottenlab.smartorm.annotations.SmartInsert) || " +
                    "@annotation(io.github.forgottenlab.smartorm.annotations.SmartUpdate) || " +
                    "@annotation(io.github.forgottenlab.smartorm.annotations.SmartDelete) || " +
                    "@annotation(io.github.forgottenlab.smartorm.annotations.SmartPage)"
    )
    public Object smartOrmEntry(ProceedingJoinPoint joinPoint) throws Throwable {

        // 当前执行的方法
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        // Mapper 代理对象（通常为 MyBatis 生成的代理对象）
        Object mapper = joinPoint.getTarget();

        // 方法参数
        Object[] args = joinPoint.getArgs();

        // Mapper 接口类型
        Class<?> mapperInterface = method.getDeclaringClass();

        // 构建执行上下文
        SmartContext ctx = new SmartContext(
                mapper,
                method,
                mapperInterface,
                args
        );

        // 查找方法上的 Smart 注解
        Annotation smartAnnotation = findSmartAnnotation(method);

        String methodName = method.getName();

        // Hook: before
        if (mapper instanceof SmartMapper<?> smartMapper) {
            smartMapper.beforeSmartOperation(methodName, args);
        }

        try {
            // 根据注解类型路由到对应 Handler
            Object result = invokeHandler(smartAnnotation, ctx);

            // Hook: after
            if (mapper instanceof SmartMapper<?> smartMapper) {
                smartMapper.afterSmartOperation(methodName, result);
            }

            return result;

        } catch (Throwable e) {

            // Hook: exception
            if (mapper instanceof SmartMapper<?> smartMapper) {
                smartMapper.onSmartException(methodName, e);
            }

            throw e;
        }
    }

    /**
     * 调用具体 Handler 处理当前注解
     */
    private Object invokeHandler(Annotation smartAnnotation, SmartContext ctx) throws Exception {
        return invokeTypedHandler(
                registry.getHandler(smartAnnotation.annotationType()),
                smartAnnotation,
                ctx
        );
    }

    /**
     * 以强类型方式调用 Handler，避免原始类型带来的未经检查警告
     */
    private <A extends Annotation> Object invokeTypedHandler(
            SmartHandler<A> handler,
            Annotation annotation,
            SmartContext ctx
    ) throws Exception {
        return handler.handle(handler.supportedAnnotation().cast(annotation), ctx);
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
            Package annotationPackage = ann.annotationType().getPackage();
            if (annotationPackage != null
                    && SMART_ANNOTATION_PACKAGE.equals(annotationPackage.getName())) {
                return ann;
            }
        }
        throw new SmartOrmException("未找到 SmartORM 注解: " + method);
    }
}