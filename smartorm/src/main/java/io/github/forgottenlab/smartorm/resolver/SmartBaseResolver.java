package io.github.forgottenlab.smartorm.resolver;

import io.github.forgottenlab.smartorm.annotations.SmartJoin;
import io.github.forgottenlab.smartorm.resolver.meta.BaseMeta;
import io.github.forgottenlab.smartorm.resolver.meta.JoinMeta;
import io.github.forgottenlab.smartorm.util.SmartEntityGenericUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details Smart Resolver 抽象基类
 * @CreateDate 2025/12/10
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public abstract class SmartBaseResolver {

    /**
     * 填充 BaseMeta 的公共字段
     */
    protected static void fillCommonMeta(BaseMeta meta, Method method, Class<?> mapperInterface) {
        meta.method = method;
        meta.mapperClass = mapperInterface;
        meta.returnType = method.getReturnType();
        meta.entityClass = SmartEntityGenericUtil.resolveEntityClass(mapperInterface);
    }

    /**
     * 解析 @SmartJoin 注解数组为 JoinMeta 列表
     */
    protected static List<JoinMeta> resolveJoins(SmartJoin[] joins) {
        if (joins == null || joins.length == 0) {
            return Collections.emptyList();
        }

        List<JoinMeta> result = new ArrayList<>();

        for (SmartJoin join : joins) {
            JoinMeta jm = new JoinMeta();
            jm.joinTable = join.table();
            jm.alias = join.alias();
            jm.joinType = join.type();
            jm.on = join.on();
            jm.userDefinedOn = join.on() != null && !join.on().isBlank();
            result.add(jm);
        }

        return result;
    }
}