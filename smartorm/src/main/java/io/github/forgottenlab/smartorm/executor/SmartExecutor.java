package io.github.forgottenlab.smartorm.executor;

import io.github.forgottenlab.smartorm.mapper.SmartMapper;

import java.util.function.Supplier;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details Smart 执行器统一接口
 * @CreateDate 2025/12/27
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public interface SmartExecutor {

    /**
     * 包装一次带 Hook 的执行过程
     *
     * @param mapper     当前 Mapper
     * @param methodName 当前方法名
     * @param args       方法参数
     * @param action     实际执行逻辑
     * @param <R>        返回结果类型
     * @return 执行结果
     */
    <R> R execute(
            SmartMapper<?> mapper,
            String methodName,
            Object[] args,
            Supplier<R> action
    );
}