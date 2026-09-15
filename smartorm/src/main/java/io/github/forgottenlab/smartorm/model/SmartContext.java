package io.github.forgottenlab.smartorm.model;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartORM 执行上下文
 * @CreateDate 2025/12/10
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public class SmartContext {

    /** 当前 Mapper 实例 */
    private Object mapper;

    /** 当前执行的方法 */
    private Method method;

    /** Mapper 接口类型 */
    private Class<?> mapperInterface;

    /** 方法参数 */
    private Object[] args;

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

    @Override
    public String toString() {
        return "SmartContext{" +
                "mapper=" + mapper +
                ", method=" + method +
                ", mapperInterface=" + mapperInterface +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}