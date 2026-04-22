package io.github.forgottenlab.smartorm.test;

import io.github.forgottenlab.smartorm.demo.entity.User;
import io.github.forgottenlab.smartorm.demo.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartSelect 注解能力测试
 * @CreateDate 2025/12/19
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@SpringBootTest(classes = io.github.forgottenlab.smartorm.demo.SmartOrmDemoApplication.class)
@Transactional // 每个测试结束自动回滚
class MapperSelectTest {

    @Autowired
    private UserMapper userMapper;

    /**
     * 每个测试前统一准备基础数据
     * 避免测试之间互相依赖
     */
    @BeforeEach
    void setUp() {
        userMapper.insertUser("Alice", 20, 1);
        userMapper.insertUser("Bob", 25, 1);
        userMapper.insertUser("Charlie", 30, 0);
        userMapper.insertUser("David", 35, 1);
    }

    /* ============================================================
     *                      SmartSelect
     * ============================================================ */

    /**
     * 查询年龄大于指定值且状态匹配的活跃用户
     * 验证：where + orderBy + desc + limit
     */
    @Test
    void test_findActiveUsersByAge() {

        List<User> users = userMapper.findActiveUsersByAge(22, 1);

        assertNotNull(users);
        assertFalse(users.isEmpty());

        for (User user : users) {
            assertTrue(user.getAge() > 22);
            assertEquals(1, user.getStatus());
        }

        // limit = 10（这里只验证不超过）
        assertTrue(users.size() <= 10);
    }

    /**
     * 根据 limit=1 查询单条用户
     * 验证：limit + 单对象返回
     */
    @Test
    void test_findUserOrderByAgeLimit1() {

        User user = userMapper.findUserOrderByAgeLimit1();

        assertNotNull(user);
        assertNotNull(user.getId());
    }

    /**
     * 根据用户名进行模糊查询
     * 验证：LIKE + 参数占位 #{0}
     */
    @Test
    void test_findUsersByName() {

        List<User> users = userMapper.findUsersByName("a");

        assertNotNull(users);
        assertFalse(users.isEmpty());

        for (User user : users) {
            assertTrue(
                    user.getUserName().contains("a")
                            || user.getUserName().contains("A")
            );
        }
    }

    /**
     * 根据 ID 精确查询单个用户
     * 验证：where + limit=1 + 返回单对象
     */
    @Test
    void test_findUserById() {

        userMapper.insertUser("Target", 28, 1);

        User inserted = userMapper.findUsersByName("Target").get(0);

        User result = userMapper.findUserById(inserted.getId());

        assertNotNull(result);
        assertEquals("Target", result.getUserName());
        assertEquals(28, result.getAge());
    }

    /**
     * 多条件组合查询
     * 验证：多参数 where（#{0} #{1} #{2}）
     */
    @Test
    void test_findComplexUsers() {

        List<User> users =
                userMapper.findComplexUsers(20, 1, "a");

        assertNotNull(users);
        assertFalse(users.isEmpty());

        for (User user : users) {
            assertTrue(user.getAge() > 20);
            assertEquals(1, user.getStatus());
            assertTrue(
                    user.getUserName().contains("a")
                            || user.getUserName().contains("A")
            );
        }
    }

    /**
     * 查询最近 30 天内创建的活跃用户
     * 验证：时间函数 + 单参数 where
     */
    @Test
    void test_findRecentActiveUsers() {

        List<User> users = userMapper.findRecentActiveUsers(1);

        assertNotNull(users);

        for (User user : users) {
            assertEquals(1, user.getStatus());
        }
    }
}
