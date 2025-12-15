package com.smartorm.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.smartorm._demo.entiey.User;
import com.smartorm._demo.mapper.UserMapper;
import com.smartorm.model.PageResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author <a href="mailto:1349801439@qq.com">Forgotten.</a>
 * @Details UserMapper 测试类 (使用 Junit5 并利用断言, 还使用了 SpringBootTest)
 * @CreateDate 2025/11/25
 * @LastModified 2025/12/13
 */
@SpringBootTest
@Transactional // 测试后回滚
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    /* ============================================================
     * 简单封装 测试
     * ============================================================ */
    /** 测试根据ID列表查询用户 */
    @Test
    void testInsertAndReturn() {
        User user = new User();
        user.setUserName("smart_insert");
        user.setAge(20);
        user.setStatus(1);

        User saved = userMapper.insertAndReturn(user);

        assertNotNull(saved.getId(), "插入后 ID 不应为空");
    }

    /** 测试根据ID删除用户 */
    @Test
    void testExistsById() {
        User user = new User();
        user.setUserName("exists_id");
        user.setAge(22);
        user.setStatus(1);

        userMapper.insert(user);

        boolean exists = userMapper.existsById(user.getId());

        assertTrue(exists);
    }

    /** 测试根据用户名查询用户 */
    @Test
    void testExistsByField() {
        User user = new User();
        user.setUserName("exists_field");
        user.setAge(23);
        user.setStatus(1);

        userMapper.insert(user);

        boolean exists = userMapper.existsBy("user_name", "exists_field");

        assertTrue(exists);
    }

    /** 测试根据ID更新用户名 */
    @Test
    void testFindOneBy() {
        User user = new User();
        user.setUserName("find_one");
        user.setAge(24);
        user.setStatus(1);

        userMapper.insert(user);

        User found = userMapper.findOneBy("user_name", "find_one");

        assertNotNull(found);
        assertEquals("find_one", found.getUserName());
    }

    /** 测试根据ID更新用户名 */
    @Test
    void testFindOneWithWrapper() {
        User user = new User();
        user.setUserName("wrapper_test");
        user.setAge(25);
        user.setStatus(1);

        userMapper.insert(user);

        QueryWrapper<User> wrapper = new QueryWrapper<User>()
                .eq("user_name", "wrapper_test");

        User found = userMapper.findOne(wrapper);

        assertNotNull(found);
    }


    /* ============================================================
     * SmartSelect 测试
     * ============================================================ */

    /** 测试根据年龄筛选活跃用户 */
    @Test
    void testFindActiveUsersByAge() {
        userMapper.insertUser("张三", 25, 1);
        userMapper.insertUser("李四", 30, 1);
        userMapper.insertUser("王五", 20, 0); // 这条不该被查出

        List<User> users = userMapper.findActiveUsersByAge(22, 1);

        assertNotNull(users);
        assertEquals(2, users.size());
    }

    /** 测试根据用户名模糊查询用户 */
    @Test
    void testFindUsersByName() {
        userMapper.insertUser("张三丰", 28, 1);
        userMapper.insertUser("张三", 25, 1);
        userMapper.insertUser("李四", 30, 1);

        List<User> users = userMapper.findUsersByName("张三");

        assertNotNull(users);
        assertEquals(2, users.size());
    }

    /** 测试根据ID查询单个用户 */
    @Test
    void testFindUserById() {
        userMapper.insertUser("测试用户", 25, 1);

        List<User> allUsers = userMapper.selectList(null);
        Long id = allUsers.get(0).getId();

        User user = userMapper.findUserById(id);

        assertNotNull(user);
        assertEquals("测试用户", user.getUserName());
        assertEquals(25, user.getAge());
    }

    /** 测试根据年龄倒序查询用户（带LIMIT 1限制） */
    @Test
    void testFindUserOrderByAgeLimit1() {
        userMapper.insertUser("A", 25, 1);
        userMapper.insertUser("B", 24, 1);

        User result = userMapper.findUserOrderByAgeLimit1();
        assertNotNull(result);
    }

    /** 测试多条件复杂查询用户 */
    @Test
    void testFindComplexUsers() {
        userMapper.insertUser("Jack", 30, 1);
        userMapper.insertUser("Jacky", 35, 1);
        userMapper.insertUser("Lucy", 28, 0);

        List<User> users = userMapper.findComplexUsers(29, 1, "J");

        assertNotNull(users);
        assertEquals(2, users.size());
    }

    /** 测试查询近期活跃用户 */
    @Test
    void testFindRecentActiveUsers() {
        userMapper.insertUser("Active1", 22, 1);
        userMapper.insertUser("Deactive", 23, 0);

        List<User> users = userMapper.findRecentActiveUsers(1);
        assertNotNull(users);
        assertEquals(1, users.size());
    }

    /* ============================================================
     * SmartPage 测试
     * ============================================================ */

    /** 测试根据状态分页查询用户 */
    @Test
    void testFindUsersByStatusPage() {
        for (int i = 0; i < 5; i++) {
            userMapper.insertUser("测试" + i, 20 + i, i % 2);
        }

        PageResult<User> page = userMapper.findUsersByStatusPage(1);

        assertNotNull(page);
        assertNotNull(page.getRows());
    }

    /** 测试根据年龄范围分页查询用户 */
    @Test
    void testFindUsersByAgeRangePage() {
        for (int i = 0; i < 7; i++) {
            userMapper.insertUser("范围用户" + i, 18 + i, 1);
        }

        PageResult<User> page = userMapper.findUsersByAgeRangePage(20, 30);

        assertNotNull(page);
        assertFalse(page.getRows().isEmpty());
    }

    /** 测试带搜索条件的分页查询 */
    @Test
    void testSearchUsersPage() {
        userMapper.insertUser("张力", 23, 1);
        userMapper.insertUser("张天", 26, 1);
        userMapper.insertUser("李三", 27, 1);

        PageResult<User> page = userMapper.searchUsersPage("张", 1);

        assertNotNull(page);
        assertEquals(2, page.getRows().size());
    }

    /* ============================================================
     * SmartInsert 测试
     * ============================================================ */

    /** 测试插入用户（带状态参数） */
    @Test
    void testInsertUser() {
        assertEquals(1, userMapper.insertUser("新用户", 28, 1));
    }

    /** 测试插入简单用户（只有用户名和年龄） */
    @Test
    void testInsertSimpleUser() {
        assertEquals(1, userMapper.insertSimpleUser("简单用户", 22));
    }

    /** 测试插入用户（使用默认状态） */
    @Test
    void testInsertUserWithDefaultStatus() {
        userMapper.insertUserWithDefaultStatus("默认状态用户", 26);

        List<User> users = userMapper.findUsersByName("默认状态用户");

        assertNotNull(users);
//        users.forEach(System.out::println); // 需要 User 重写 toString 方法才能输出
        for (User user : users) {
            System.out.println("ID: " + user.getId() +
                    ", 姓名: " + user.getUserName() +
                    ", 年龄: " + user.getAge());
        }
        assertEquals(1, users.size());
        assertEquals(1, users.get(0).getStatus());
    }

    /* ============================================================
     * SmartUpdate 测试
     * ============================================================ */

    /** 测试更新用户名和年龄 */
    @Test
    void testUpdateUserNameAndAge() {
        userMapper.insertUser("旧名字", 25, 1);
        Long id = userMapper.findUsersByName("旧名字").get(0).getId();

        assertEquals(1, userMapper.updateUserNameAndAge("新名字", 30, id));
    }

    /** 测试更新用户状态 */
    @Test
    void testUpdateUserStatus() {
        userMapper.insertUser("状态测试用户", 25, 1);
        Long id = userMapper.findUsersByName("状态测试用户").get(0).getId();

        assertEquals(1, userMapper.updateUserStatus(0, id));
    }

    /** 测试递增用户年龄 */
    @Test
    void testIncrementAge() {
        userMapper.insertUser("年龄递增用户", 25, 1);
        Long id = userMapper.findUsersByName("年龄递增用户").get(0).getId();

        assertEquals(1, userMapper.incrementAge(5, id));
    }

    /** 测试批量更新用户状态 */
    @Test
    void testUpdateStatusForOldUsers() {
        userMapper.insertUser("A", 45, 1);
        userMapper.insertUser("B", 46, 1);

        int result = userMapper.updateStatusForOldUsers(0, 40);

        assertTrue(result >= 1);
    }

    /* ============================================================
     * SmartDelete 测试
     * ============================================================ */

    /** 测试根据ID删除用户 */
    @Test
    void testDeleteUserById() {
        userMapper.insertUser("待删用户", 25, 1);
        Long id = userMapper.findUsersByName("待删用户").get(0).getId();

        assertEquals(1, userMapper.deleteUserById(id));
    }

    /** 测试删除非活跃年轻用户 */
    @Test
    void testDeleteInactiveYoungUsers() {
        userMapper.insertUser("young0", 18, 0);
        userMapper.insertUser("young1", 19, 1);

        int result = userMapper.deleteInactiveYoungUsers(0, 20);
        assertTrue(result >= 0);
    }

    /** 测试根据用户名删除用户 */
    @Test
    void testDeleteUserByName() {
        userMapper.insertUser("删除测试", 25, 1);

        assertEquals(1, userMapper.deleteUserByName("删除测试"));
    }

    /* ============================================================
     * Debug & Trace（可作为开发调试日志输出）
     * ============================================================ */

    /** 测试SQL执行跟踪（用于调试） */
    @Test
    void testTraceSql() {
        User u = new User();
        u.setId(1L);
        u.setUserName("Test");
        try {
            userMapper.updateById(u);
        } catch (Exception ignored) {}
    }

    /** 调试Mapper方法签名（用于分析） */
    @Test
    void debugMapperMethodSignature() {
        for (Method method : UserMapper.class.getMethods()) {
            if (method.getName().startsWith("update")) {
                System.out.println("方法：" + method);
            }
        }
    }
}

