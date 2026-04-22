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
 * @Details SmartUpdate 注解能力测试
 * @CreateDate 2025/12/19
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@SpringBootTest(classes = io.github.forgottenlab.smartorm.demo.SmartOrmDemoApplication.class)
@Transactional // 每个测试结束自动回滚
class MapperUpdateTest {

    @Autowired
    private UserMapper userMapper;

    /**
     * 每个测试前准备基础数据
     * 保证测试互不影响
     */
    @BeforeEach
    void setUp() {
        userMapper.insertUser("Alice", 20, 1);
        userMapper.insertUser("Bob", 25, 1);
        userMapper.insertUser("Charlie", 30, 0);
    }

    /* ============================================================
     *                      SmartUpdate
     * ============================================================ */

    /**
     * 根据用户 ID 更新用户名和年龄
     * 验证：
     *  - update 返回值
     *  - 数据是否真实落库
     */
    @Test
    void test_updateUserNameAndAge() {

        // 准备数据
        userMapper.insertUser("旧名字", 25, 1);
        User before = userMapper.findUsersByName("旧名字").get(0);

        // 执行更新
        int rows = userMapper.updateUserNameAndAge(
                "新名字",
                30,
                before.getId()
        );

        // 校验更新行数
        assertEquals(1, rows);

        // 再次查询校验数据
        User after = userMapper.findUserById(before.getId());
        assertNotNull(after);
        assertEquals("新名字", after.getUserName());
        assertEquals(30, after.getAge());
    }

    /**
     * 根据用户 ID 更新状态字段
     * 验证：
     *  - 单字段 update
     *  - where 条件是否生效
     */
    @Test
    void test_updateUserStatus() {

        userMapper.insertUser("状态测试用户", 22, 1);
        User user = userMapper.findUsersByName("状态测试用户").get(0);

        int rows = userMapper.updateUserStatus(0, user.getId());
        assertEquals(1, rows);

        User updated = userMapper.findUserById(user.getId());
        assertNotNull(updated);
        assertEquals(0, updated.getStatus());
    }

    /**
     * 使用 SQL 表达式递增年龄
     * 验证：
     *  - values = "age + #{0}"
     *  - 表达式是否正确执行
     */
    @Test
    void test_incrementAge() {

        userMapper.insertUser("年龄递增用户", 25, 1);
        User before = userMapper.findUsersByName("年龄递增用户").get(0);

        int rows = userMapper.incrementAge(5, before.getId());
        assertEquals(1, rows);

        User after = userMapper.findUserById(before.getId());
        assertNotNull(after);
        assertEquals(30, after.getAge());
    }

    /**
     * 批量更新符合条件的记录
     * 验证：
     *  - where 条件作用于多行
     *  - 返回值为影响行数
     */
    @Test
    void test_updateStatusForOldUsers() {

        userMapper.insertUser("OldUser1", 45, 1);
        userMapper.insertUser("OldUser2", 46, 1);
        userMapper.insertUser("YoungUser", 18, 1);

        int rows = userMapper.updateStatusForOldUsers(0, 40);

        // 至少应更新 2 条
        assertTrue(rows >= 2);

        // 校验结果
        List<User> users = userMapper.findUsersByName("OldUser");
        for (User user : users) {
            assertEquals(0, user.getStatus());
        }
    }

    /**
     * 更新不存在的记录
     * 验证：
     *  - where 不命中时返回 0
     */
    @Test
    void test_updateWithNoMatch() {

        int rows = userMapper.updateUserStatus(0, -1L);

        assertEquals(0, rows);
    }
}
