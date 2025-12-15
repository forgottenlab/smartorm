package com.smartorm.model;

import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details Smart ORM 切面执行上下文
 * @CreateDate 2025/12/10
 * @LastModified 2025/12/10
 * @VersionHistory [版本历史]
 */
public class SmartContext {
    private Object mapper;           // mapper实例
    private Method method;           // 当前执行的方法
    private Class<?> mapperInterface; // Mapper接口

    private Object[] args;           // 方法参数

    public SmartContext(
            Object mapper,
            Method method,
            Class<?> mapperInterface,
            Object[] args
    ) {
        this.mapper = mapper;
        this.method = method;
        this.mapperInterface = mapperInterface;
        this.args = args;
    }

    public Object getMapper() {
        return mapper;
    }

    public void setMapper(Object mapper) {
        this.mapper = mapper;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getMapperInterface() {
        return mapperInterface;
    }

    public void setMapperInterface(Class<?> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

}