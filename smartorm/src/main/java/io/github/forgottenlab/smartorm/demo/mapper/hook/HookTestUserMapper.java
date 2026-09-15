package io.github.forgottenlab.smartorm.demo.mapper.hook;

import io.github.forgottenlab.smartorm.annotations.SmartInsert;
import io.github.forgottenlab.smartorm.annotations.SmartSelect;
import io.github.forgottenlab.smartorm.demo.entity.User;
import io.github.forgottenlab.smartorm.mapper.SmartMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 生命周期 Hook 测试 Mapper
 * @CreateDate 2025/12/27
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@Mapper
public interface HookTestUserMapper extends SmartMapper<User> {

    List<String> INVOCATION_LOG = new ArrayList<>();

    @Override
    default void beforeSmartOperation(String methodName, Object[] args) {
        INVOCATION_LOG.add("before:" + methodName);
    }

    @Override
    default void afterSmartOperation(String methodName, Object result) {
        INVOCATION_LOG.add("after:" + methodName);
    }

    @Override
    default void onSmartException(String methodName, Throwable e) {
        INVOCATION_LOG.add("exception:" + methodName);
    }

    @SmartInsert(
            fields = {"user_name", "age", "status"},
            values = {"#{0}", "#{1}", "#{2}"}
    )
    int insertUser(String name, Integer age, Integer status);

    @SmartSelect(where = "not_exist_field = #{0}")
    User selectWillFail(String value);
}