package io.github.forgottenlab.smartorm.resolver;

import io.github.forgottenlab.smartorm.annotations.SelectResultType;
import io.github.forgottenlab.smartorm.annotations.SmartSelect;
import io.github.forgottenlab.smartorm.exception.SmartOrmException;
import io.github.forgottenlab.smartorm.resolver.meta.SelectMeta;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartSelect 注解解析器
 * @CreateDate 2025/11/25
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public class SmartSelectResolver extends SmartBaseResolver {

    /**
     * 解析 @SmartSelect 注解并生成 Meta
     */
    public static SelectMeta resolve(
            Method method,
            SmartSelect ann,
            Class<?> mapperInterface
    ) {
        SelectMeta meta = new SelectMeta();

        // 1. 填充 BaseMeta
        fillCommonMeta(meta, method, mapperInterface);

        // 2. 查询语义
        meta.fields = ann.fields();
        meta.where = ann.where();
        meta.orderBy = ann.orderBy();
        meta.desc = ann.desc();
        meta.joins = resolveJoins(ann.join());

        // 3. 执行语义
        meta.limit = ann.limit();
        meta.resultType = ann.resultType();

        // 4. DTO_LIST 显式解析 DTO 类型
        if (meta.resultType == SelectResultType.DTO_LIST) {
            meta.dtoClass = resolveDtoClass(method);
        }

        return meta;
    }

    /**
     * 解析 List<DTO> 中的 DTO 类型
     */
    private static Class<?> resolveDtoClass(Method method) {
        Type type = method.getGenericReturnType();

        if (type instanceof ParameterizedType) {
            Type actual = ((ParameterizedType) type).getActualTypeArguments()[0];
            if (actual instanceof Class<?>) {
                return (Class<?>) actual;
            }
        }

        throw new SmartOrmException(
                "DTO_LIST 需要返回 List<DTO>，但无法解析: " + method
        );
    }
}