package io.github.forgottenlab.smartorm.builder;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import io.github.forgottenlab.smartorm.exception.SmartOrmException;
import io.github.forgottenlab.smartorm.resolver.meta.BaseMeta;
import io.github.forgottenlab.smartorm.resolver.meta.DeleteMeta;
import io.github.forgottenlab.smartorm.resolver.meta.UpdateMeta;

import java.lang.reflect.Method;

/**
 * SmartUpdate / SmartDelete 的无条件全表操作安全边界。
 */
final class SmartMutationSafetyGuard {

    private SmartMutationSafetyGuard() {
    }

    static void verifyUpdate(UpdateMeta meta, AbstractWrapper<?, ?, ?> wrapper) {
        verify("@SmartUpdate", "UPDATE", meta, meta.allowFullTable, wrapper);
    }

    static void verifyDelete(DeleteMeta meta, AbstractWrapper<?, ?, ?> wrapper) {
        verify("@SmartDelete", "DELETE", meta, meta.allowFullTable, wrapper);
    }

    private static void verify(
            String annotation,
            String operation,
            BaseMeta meta,
            boolean allowFullTable,
            AbstractWrapper<?, ?, ?> wrapper
    ) {
        if (allowFullTable || hasEffectiveWhere(wrapper)) {
            return;
        }

        throw new SmartOrmException(
                annotation + " " + operation + " rejected for " + describeMethod(meta.method)
                        + ": no effective WHERE condition was generated. Full-table " + operation
                        + " is denied by default; set allowFullTable = true explicitly only when a full-table "
                        + operation + " is intended."
        );
    }

    private static boolean hasEffectiveWhere(AbstractWrapper<?, ?, ?> wrapper) {
        if (wrapper == null || wrapper.getExpression() == null) {
            return false;
        }

        // Inspect the final structured predicate segments so runtime-disabled conditions stay fail-closed.
        return wrapper.getExpression().getNormal().stream()
                .map(ISqlSegment::getSqlSegment)
                .anyMatch(SmartMutationSafetyGuard::isEffectivePredicateSegment);
    }

    private static boolean isEffectivePredicateSegment(String segment) {
        if (segment == null) {
            return false;
        }

        String candidate = segment.strip();
        while (candidate.length() >= 2 && candidate.startsWith("(") && candidate.endsWith(")")) {
            candidate = candidate.substring(1, candidate.length() - 1).strip();
        }

        // Raw apply segments are not parsed by MyBatis-Plus; exclude its two empty-shell forms explicitly.
        return !candidate.isEmpty() && !candidate.equalsIgnoreCase("WHERE");
    }

    private static String describeMethod(Method method) {
        if (method == null) {
            return "unknown mapper method";
        }
        return method.getDeclaringClass().getName() + "#" + method.getName();
    }
}
