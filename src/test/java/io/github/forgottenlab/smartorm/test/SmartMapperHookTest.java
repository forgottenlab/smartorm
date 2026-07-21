package io.github.forgottenlab.smartorm.test;

import io.github.forgottenlab.smartorm.demo.mapper.hook.HookTestUserMapper;
import io.github.forgottenlab.smartorm.exception.SmartOrmException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartMapper 生命周期 Hook 测试
 * @CreateDate 2025/12/19
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@SpringBootTest(classes = io.github.forgottenlab.smartorm.demo.SmartOrmDemoApplication.class)
@ActiveProfiles("test")
@Transactional
class SmartMapperHookTest {

    @Autowired
    private HookTestUserMapper userMapper;

    @BeforeEach
    void clearLog() {
        HookTestUserMapper.INVOCATION_LOG.clear();
    }

    /**
     * 验证：
     *  - beforeSmartOperation 被调用
     *  - afterSmartOperation 被调用
     *  - 调用顺序正确
     */
    @Test
    void test_hook_on_success() {

        userMapper.insertUser("HookUser", 20, 1);

        assertEquals(
                List.of(
                        "before:insertUser",
                        "after:insertUser"
                ),
                HookTestUserMapper.INVOCATION_LOG
        );

    }

    /**
     * 验证：
     *  - SQL 执行异常时触发 onSmartException
     */
    @Test
    void test_hook_on_exception() {

        SmartOrmException exception = assertThrows(SmartOrmException.class, () ->
                userMapper.selectWillFail("x")
        );

        assertEquals("处理 @SmartSelect 失败", exception.getMessage());
        assertEquals(
                List.of(
                        "before:selectWillFail",
                        "exception:selectWillFail"
                ),
                HookTestUserMapper.INVOCATION_LOG
        );
    }

}


