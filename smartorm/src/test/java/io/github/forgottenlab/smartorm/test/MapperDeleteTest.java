package io.github.forgottenlab.smartorm.test;

import io.github.forgottenlab.smartorm.demo.entity.User;
import io.github.forgottenlab.smartorm.demo.mapper.UserMapper;
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
 * @Details SmartDelete 注解能力测试
 * @CreateDate 2025/12/19
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@SpringBootTest(classes = io.github.forgottenlab.smartorm.demo.SmartOrmDemoApplication.class)
@ActiveProfiles("test")
@Transactional // 每个测试结束自动回滚
class MapperDeleteTest {

    @Autowired
    private UserMapper userMapper;

    /**
     * 每个测试前准备基础数据
     * 避免测试间相互影响
     */
    @BeforeEach
    void setUp() {
        userMapper.insertUser("Alice", 20, 1);
        userMapper.insertUser("Bob", 25, 1);
        userMapper.insertUser("Charlie", 18, 0);
        userMapper.insertUser("David", 30, 0);
    }

    /* ============================================================
     *                      SmartDelete
     * ============================================================ */

    /**
     * 根据用户 ID 删除用户
     * 验证：
     *  - delete 返回影响行数
     *  - 数据是否真实删除
     */
    @Test
    void test_deleteUserById() {

        userMapper.insertUser("ToDelete", 28, 1);
        List<User> matches = userMapper.findUsersByName("ToDelete");
        assertEquals(1, matches.size());
        User user = matches.get(0);

        int rows = userMapper.deleteUserById(user.getId());
        assertEquals(1, rows);

        User deleted = userMapper.findUserById(user.getId());
        assertNull(deleted, "删除后用户应不存在");
    }

    /**
     * 删除状态匹配且年龄小于指定值的用户
     * 验证：
     *  - AND 组合条件
     *  - 批量删除行为
     */
    @Test
    void test_deleteInactiveYoungUsers() {

        // 准备额外数据
        userMapper.insertUser("InactiveYoung1", 17, 0);
        userMapper.insertUser("InactiveYoung2", 19, 0);
        userMapper.insertUser("InactiveOld", 35, 0);
        userMapper.insertUser("ActiveYoung", 18, 1);

        int rows = userMapper.deleteInactiveYoungUsers(0, 20);

        // 至少删除两条
        assertTrue(rows >= 2);

        // 校验：已删除用户不存在
        assertTrue(userMapper.findUsersByName("InactiveYoung1").isEmpty());
        assertTrue(userMapper.findUsersByName("InactiveYoung2").isEmpty());

        // 校验：不应被误删
        assertFalse(userMapper.findUsersByName("InactiveOld").isEmpty());
        assertFalse(userMapper.findUsersByName("ActiveYoung").isEmpty());
    }

    /**
     * 根据用户名精确删除用户
     * 验证：
     *  - 等值条件
     *  - 单条删除
     */
    @Test
    void test_deleteUserByName() {

        userMapper.insertUser("UniqueUser", 26, 1);

        int rows = userMapper.deleteUserByName("UniqueUser");
        assertEquals(1, rows);

        List<User> users = userMapper.findUsersByName("UniqueUser");
        assertTrue(users.isEmpty(), "删除后不应再查询到该用户");
    }

    /**
     * 删除条件未命中任何记录
     * 验证：
     *  - delete 返回 0
     *  - 数据不受影响
     */
    @Test
    void test_deleteWithNoMatch() {

        int rows = userMapper.deleteUserById(-1L);

        assertEquals(0, rows);

        // 原始数据仍存在
        List<User> users = userMapper.findUsersByName("Alice");
        assertFalse(users.isEmpty());
    }
}
