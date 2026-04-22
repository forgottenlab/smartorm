package io.github.forgottenlab.smartorm.test;

import io.github.forgottenlab.smartorm.demo.dto.UserProfileDTO;
import io.github.forgottenlab.smartorm.demo.entity.User;
import io.github.forgottenlab.smartorm.demo.entity.UserProfile;
import io.github.forgottenlab.smartorm.demo.entity.UserRole;
import io.github.forgottenlab.smartorm.demo.mapper.UserMapper;
import io.github.forgottenlab.smartorm.demo.mapper.UserProfileMapper;
import io.github.forgottenlab.smartorm.demo.mapper.UserRoleMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartJoin 与原生关联查询能力测试
 * @CreateDate 2025/12/19
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@SpringBootTest(classes = io.github.forgottenlab.smartorm.demo.SmartOrmDemoApplication.class)
@Transactional // 所有测试完成后自动回滚
class MapperJoinTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    /* ============================================================
     * JOIN 基础准备
     * ============================================================ */

    /**
     * 初始化一组用户 + profile 数据
     *
     * user1：有 profile，status=1
     * user2：有 profile，status=0
     * user3：无 profile，status=1（用于 LEFT JOIN）
     */
    private void prepareJoinData() {
        // user1
        User u1 = new User();
        u1.setUserName("join_user_1");
        u1.setAge(20);
        u1.setStatus(1);
        userMapper.insert(u1);

        UserProfile p1 = new UserProfile();
        p1.setUserId(u1.getId());
        p1.setEmail("u1@test.com");
        p1.setPhone("13800000001");
        userProfileMapper.insert(p1);

        // user2
        User u2 = new User();
        u2.setUserName("join_user_2");
        u2.setAge(22);
        u2.setStatus(0);
        userMapper.insert(u2);

        UserProfile p2 = new UserProfile();
        p2.setUserId(u2.getId());
        p2.setEmail("u2@test.com");
        p2.setPhone("13800000002");
        userProfileMapper.insert(p2);

        // user3（无 profile）
        User u3 = new User();
        u3.setUserName("join_user_3");
        u3.setAge(25);
        u3.setStatus(1);
        userMapper.insert(u3);
    }

    private void prepareUserRoleData() {
        // 假设 prepareJoinData() 已经执行

        // 查询刚插入的用户
        List<User> users = userMapper.selectList(null);

        for (User user : users) {
            // 所有用户都有一个 BASIC 角色
            UserRole role = new UserRole();
            role.setUserId(user.getId());
            role.setRoleCode("BASIC");
            role.setRoleName("基础用户");
            userRoleMapper.insert(role);
        }

        // 给第一个用户再加一个 ADMIN 角色
        User first = users.get(0);
        UserRole admin = new UserRole();
        admin.setUserId(first.getId());
        admin.setRoleCode("ADMIN");
        admin.setRoleName("管理员");
        userRoleMapper.insert(admin);
    }

    /* ============================================================
     * LEFT JOIN 测试
     * ============================================================ */

    /**
     * 测试 LEFT JOIN：
     * - 有 profile 的用户应返回 email
     * - 无 profile 的用户也应被返回（email 为 null）
     */
    @Test
    void test_selectUserWithProfileLeft() {
        prepareJoinData();

        List<Map<String, Object>> result =
                userMapper.selectUserWithProfileLeft();

        assertNotNull(result);
        assertEquals(3, result.size(), "LEFT JOIN 应返回全部用户");

        boolean hasNullEmail = result.stream()
                .anyMatch(m -> m.get("email") == null);

        assertTrue(hasNullEmail, "LEFT JOIN 应包含无 profile 的用户");
    }

    /* ============================================================
     * INNER JOIN 测试
     * ============================================================ */

    /**
     * 测试 INNER JOIN：
     * - 只返回存在 profile 的用户
     * - 不应包含 email 为 null 的记录
     */
    @Test
    void test_selectUserWithProfileInner() {
        prepareJoinData();

        List<Map<String, Object>> result =
                userMapper.selectUserWithProfileInner();

        assertNotNull(result);
        assertEquals(2, result.size(), "INNER JOIN 只应返回有 profile 的用户");

        result.forEach(row ->
                assertNotNull(row.get("email"), "INNER JOIN email 不应为空")
        );
    }

    /* ============================================================
     * JOIN + WHERE 测试
     * ============================================================ */

    /**
     * 测试 JOIN + WHERE：
     * - 只返回 status=1 且存在 profile 的用户
     */
    @Test
    void test_selectActiveUsersWithEmail() {
        prepareJoinData();

        List<Map<String, Object>> result =
                userMapper.selectActiveUsersWithEmail(1);

        assertNotNull(result);
        assertEquals(1, result.size(), "应只返回一个活跃且有 profile 的用户");

        Map<String, Object> row = result.get(0);
        assertEquals("join_user_1", row.get("user_name"));
        assertNotNull(row.get("email"));
    }

    /* ============================================================
     * JOIN + DTO 测试
     * ============================================================ */

    /**
     * 测试 JOIN + DTO 映射：
     * - 返回类型应为 DTO
     * - 字段应正确映射
     */
    @Test
    void test_selectUserProfileDTO() {
        prepareJoinData();

        List<UserProfileDTO> list =
                userMapper.selectUserProfileDTO();

        assertNotNull(list);
        assertFalse(list.isEmpty());

        UserProfileDTO dto = list.get(0);
        assertNotNull(dto.getId());
        assertNotNull(dto.getUserName());
        // LEFT JOIN 场景下 email 允许为 null
    }

    @Test
    void test_selectUserWithProfile_native_sql() {
        prepareJoinData();

        List<Map<String, Object>> result =
                userMapper.selectUserWithProfileNative();

        assertNotNull(result);
        assertEquals(3, result.size());

        boolean hasNullEmail = result.stream()
                .anyMatch(m -> m.get("email") == null);

        assertTrue(hasNullEmail);
    }

    @Test
    void test_join_where_param_order() {
        prepareJoinData();

        List<Map<String, Object>> result =
                userMapper.selectByStatusAndEmailLike(
                        1,
                        "%@test.com"
                );

        assertEquals(1, result.size());
        assertEquals("join_user_1", result.get(0).get("user_name"));
    }

    @Test
    void test_user_defined_on_condition() {
        prepareJoinData();

        List<Map<String, Object>> result =
                userMapper.selectUserWithCustomOn();

        assertEquals(3, result.size());

        boolean hasNullEmail = result.stream()
                .anyMatch(r -> r.get("email") == null);

        assertTrue(hasNullEmail);
    }

    @Test
    void test_auto_infer_on_condition() {
        prepareJoinData();

        List<Map<String, Object>> result =
                userMapper.selectUserWithAutoOn();

        assertEquals(2, result.size());
    }

    @Test
    void test_join_order_by() {
        prepareJoinData();

        List<Map<String, Object>> result =
                userMapper.selectUserOrderByAge();

        assertEquals(3, result.size());
        assertEquals("join_user_1", result.get(0).get("user_name"));
    }

    @Test
    void test_join_limit() {
        prepareJoinData();

        List<Map<String, Object>> result =
                userMapper.selectUserLimit1();

        assertEquals(1, result.size());
    }

    /* ============================================================
     * THREE TABLE JOIN TEST
     * ============================================================ */

    @Test
    void test_three_table_left_join() {
        prepareJoinData();
        prepareUserRoleData();

        List<Map<String, Object>> result =
                userMapper.selectUserWithProfileAndRole();

        assertNotNull(result);
        assertFalse(result.isEmpty());

        // 至少有一条 ADMIN
        boolean hasAdmin = result.stream()
                .anyMatch(r -> "ADMIN".equals(r.get("role_code")));

        assertTrue(hasAdmin);
    }

    @Test
    void test_three_table_inner_join() {
        prepareJoinData();
        prepareUserRoleData();

        List<Map<String, Object>> result =
                userMapper.selectUserWithProfileAndRoleInner();

        // 所有用户都有 profile + role
        assertFalse(result.isEmpty());

        result.forEach(r -> {
            assertNotNull(r.get("email"));
            assertNotNull(r.get("role_code"));
        });
    }

    @Test
    void test_three_table_join_with_where() {
        prepareJoinData();
        prepareUserRoleData();

        List<Map<String, Object>> result =
                userMapper.selectActiveUserByRole(1, "ADMIN");

        assertEquals(1, result.size());

        Map<String, Object> row = result.get(0);
        assertEquals("join_user_1", row.get("user_name"));
    }

}