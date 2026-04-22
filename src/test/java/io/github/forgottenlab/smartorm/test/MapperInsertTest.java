package io.github.forgottenlab.smartorm.test;

import io.github.forgottenlab.smartorm.demo.entity.User;
import io.github.forgottenlab.smartorm.demo.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartInsert 注解能力测试
 * @CreateDate 2025/12/19
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@SpringBootTest(classes = io.github.forgottenlab.smartorm.demo.SmartOrmDemoApplication.class)
@Transactional // 每个测试结束自动回滚
class MapperInsertTest {

    @Autowired
    private UserMapper userMapper;

    /* ============================================================
     *                      SmartInsert
     * ============================================================ */

    /**
     * 插入完整用户（用户名 + 年龄 + 状态）
     * 验证：
     *  - 返回影响行数
     *  - 字段完整写入
     */
    @Test
    void test_insertUser() {

        int rows = userMapper.insertUser("InsertUser", 25, 1);
        assertEquals(1, rows);

        List<User> users = userMapper.findUsersByName("InsertUser");
        assertFalse(users.isEmpty());

        User user = users.get(0);
        assertEquals("InsertUser", user.getUserName());
        assertEquals(25, user.getAge());
        assertEquals(1, user.getStatus());
    }

    /**
     * 插入简单用户（用户名 + 年龄）
     * 状态字段依赖数据库默认值
     * 验证：
     *  - 默认值是否生效
     */
    @Test
    void test_insertSimpleUser() {

        int rows = userMapper.insertSimpleUser("SimpleUser", 22);
        assertEquals(1, rows);

        User user = userMapper.findUsersByName("SimpleUser").get(0);

        assertEquals("SimpleUser", user.getUserName());
        assertEquals(22, user.getAge());

        // status 字段应使用数据库默认值（根据表结构为 1）
        assertEquals(1, user.getStatus());
    }

    /**
     * 插入用户时在 SQL 中显式指定状态常量
     * 验证：
     *  - values 中常量表达式生效
     *  - 不依赖数据库默认值
     */
    @Test
    void test_insertUserWithDefaultStatus() {

        int rows =
                userMapper.insertUserWithDefaultStatus("ConstStatusUser", 30);
        assertEquals(1, rows);

        User user =
                userMapper.findUsersByName("ConstStatusUser").get(0);

        assertEquals("ConstStatusUser", user.getUserName());
        assertEquals(30, user.getAge());
        assertEquals(1, user.getStatus());
    }

    /**
     * 连续插入多条数据
     * 验证：
     *  - SmartInsert 不依赖缓存
     *  - 不存在状态串扰
     */
    @Test
    void test_insertMultipleUsers() {

        userMapper.insertUser("UserA", 20, 1);
        userMapper.insertUser("UserB", 21, 0);
        userMapper.insertSimpleUser("UserC", 22);

        List<User> usersA = userMapper.findUsersByName("UserA");
        List<User> usersB = userMapper.findUsersByName("UserB");
        List<User> usersC = userMapper.findUsersByName("UserC");

        assertEquals(1, usersA.size());
        assertEquals(1, usersB.size());
        assertEquals(1, usersC.size());

        assertEquals(1, usersA.get(0).getStatus());
        assertEquals(0, usersB.get(0).getStatus());
        assertEquals(1, usersC.get(0).getStatus()); // DB 默认
    }

    // TODO: 批量insert
}
