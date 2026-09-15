package io.github.forgottenlab.smartorm.executor;

import io.github.forgottenlab.smartorm.mapper.SmartMapper;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 默认 Smart 执行器实现
 * @CreateDate 2025/12/27
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@Component
public class DefaultSmartExecutor implements SmartExecutor {

    @Override
    public <R> R execute(
            SmartMapper<?> mapper,
            String methodName,
            Object[] args,
            Supplier<R> action
    ) {
        try {
            mapper.beforeSmartOperation(methodName, args);

            R result = action.get();

            mapper.afterSmartOperation(methodName, result);
            return result;

        } catch (Throwable e) {
            mapper.onSmartException(methodName, e);
            throw e;
        }
    }
}