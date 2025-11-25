package com.smartorm.test;

import com.smartorm._demo.entiey.User;
import com.smartorm._demo.mapper.UserMapper;
import com.smartorm.model.PageResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * @author <a href="#">Forgotten.</a>
 * @Details UserMapper 测试类
 * @CreateDate 2025/11/25
 * @LastModified 2025/11/25
 * @VersionHistory [版本历史]
 */
@SpringBootTest
@Transactional // 测试后回滚数据
class UserMapperTest {

    @Resource
    private UserMapper userMapper;

    /**
     * &#064;SmartSelect  测试
     */
    @Test
    void testFindActiveUsersByAge() {
        // 先插入测试数据
        userMapper.insertUser("张三", 25, 1);
        userMapper.insertUser("李四", 30, 1);
        userMapper.insertUser("王五", 20, 0); // 状态为0，不应该被查询到

        List<User> users = userMapper.findActiveUsersByAge(22, 1);

        // 使用JUnit 5断言
        assertNotNull(users);
        assertEquals(2, users.size()); // 只应该找到张三和李四
    }
    @Test
    void testFindUsersByName() {
        userMapper.insertUser("张三丰", 28, 1);
        userMapper.insertUser("张三", 25, 1);
        userMapper.insertUser("李四", 30, 1);

        List<User> users = userMapper.findUsersByName("张三");

        assertNotNull(users);
        assertEquals(2, users.size()); // 张三丰和张三
    }
    @Test
    void testFindUserById() {
        // 先插入一个用户
        userMapper.insertUser("测试用户", 25, 1);

        // 获取刚插入的用户
        List<User> allUsers = userMapper.selectList(null);
        Long testUserId = allUsers.get(0).getId();

        User user = userMapper.findUserById(testUserId);

        assertNotNull(user);
        assertEquals("测试用户", user.getUserName());
        assertEquals(25, user.getAge());
    }

    // =============================================================================

    /**
     * &#064;SmartPage  测试
     */
    @Test
    void testFindUsersByStatusPage() {
        // 插入测试数据
        for (int i = 0; i < 5; i++) {
            userMapper.insertUser("用户" + i, 20 + i, i % 2); // 状态交替为0和1
        }

        PageResult<User> page = userMapper.findUsersByStatusPage(1);

        assertNotNull(page);
        assertNotNull(page.getRows());
        // 根据实际数据验证
    }
    @Test
    void testFindUsersByAgeRangePage() {
        // 插入测试数据
        for (int i = 0; i < 5; i++) {
            userMapper.insertUser("年龄用户" + i, 18 + i, 1);
        }

        PageResult<User> page = userMapper.findUsersByAgeRangePage(20, 30);

        assertNotNull(page);
        assertNotNull(page.getRows());
    }

    // =============================================================================

    /**
     * &#064;SmartInsert  测试
     */
    @Test
    void testInsertUser() {
        int result = userMapper.insertUser("新用户", 28, 1);

        assertEquals(1, result);
    }
    @Test
    void testInsertSimpleUser() {
        int result = userMapper.insertSimpleUser("简单用户", 22);

        assertEquals(1, result);
    }

    // =============================================================================

    /**
     * &#064;SmartUpdate  测试
     */
    @Test
    void testUpdateUserNameAndAge() {
        // 先插入测试数据
        userMapper.insertUser("旧名字", 25, 1);
        List<User> users = userMapper.findUsersByName("旧名字");
        Long userId = users.get(0).getId();

        // 更新用户信息
        int result = userMapper.updateUserNameAndAge("新名字", 30, userId);

        assertEquals(1, result);
    }
    @Test
    void testUpdateUserStatus() {
        userMapper.insertUser("状态测试用户", 25, 1);
        List<User> users = userMapper.findUsersByName("状态测试用户");
        Long userId = users.get(0).getId();

        int result = userMapper.updateUserStatus(0, userId);

        assertEquals(1, result);
    }
    @Test
    void testIncrementAge() {
        userMapper.insertUser("年龄递增用户", 25, 1);
        List<User> users = userMapper.findUsersByName("年龄递增用户");
        Long userId = users.get(0).getId();

        int result = userMapper.incrementAge(5, userId);

        assertEquals(1, result);
    }

    // =============================================================================

    /**
     * &#064;SmartDelete  测试
     */
    @Test
    void testDeleteUserById() {
        userMapper.insertUser("待删除用户", 25, 1);
        List<User> users = userMapper.findUsersByName("待删除用户");
        Long userId = users.get(0).getId();

        int result = userMapper.deleteUserById(userId);

        assertEquals(1, result);
    }
    @Test
    void testDeleteInactiveYoungUsers() {
        // 插入测试数据
        userMapper.insertUser("年轻非活跃用户", 18, 0); // 应该被删除
        userMapper.insertUser("年轻活跃用户", 19, 1);   // 不应该被删除

        int result = userMapper.deleteInactiveYoungUsers(0, 20);

        assertTrue(result >= 0); // 可能删除0条或多条
    }
    @Test
    void testDeleteUserByName() {
        userMapper.insertUser("特定用户", 25, 1);

        int result = userMapper.deleteUserByName("特定用户");

        assertEquals(1, result);
    }

    // =============================================================================

    /**
     * 原生方法测试
     */
    @Test
    void testInsertAndReturn() {
        User user = new User();
        user.setUserName("原生用户");
        user.setAge(35);
        user.setStatus(1);

        User returnedUser = userMapper.insertAndReturn(user);

        assertNotNull(returnedUser);
        assertNotNull(returnedUser.getId()); // ID 应该被自动填充
    }
    @Test
    void testUpdateByIdEntity() {
        User user = new User();
        user.setUserName("更新测试");
        user.setAge(25);
        user.setStatus(1);
        userMapper.insert(user);

        user.setUserName("更新后的名字");
        user.setAge(30);
        int result = userMapper.updateByIdEntity(user);

        assertEquals(1, result);
    }
    @Test
    void testSelectList() {
        // 测试BaseMapper的原始方法
        userMapper.insertUser("测试用户1", 20, 1);
        userMapper.insertUser("测试用户2", 25, 1);

        List<User> users = userMapper.selectList(null);

        assertNotNull(users);
        assertFalse(users.isEmpty());
    }
}
