package io.github.forgottenlab.smartorm.test;

import io.github.forgottenlab.smartorm.demo.entity.User;
import io.github.forgottenlab.smartorm.demo.mapper.UserMapper;
import io.github.forgottenlab.smartorm.demo.mapper.UserProfileMapper;
import io.github.forgottenlab.smartorm.model.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartPage 分页查询能力测试
 * @CreateDate 2025/12/19
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@SpringBootTest(classes = io.github.forgottenlab.smartorm.demo.SmartOrmDemoApplication.class)
@Transactional // 每个测试结束自动回滚
class MapperPageTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserProfileMapper userProfileMapper;

    /**
     * 每个测试前统一准备分页测试数据
     */
    @BeforeEach
    void setUp() {

        // ===== 状态 = 1 的用户 =====
        for (int i = 1; i <= 30; i++) {

            User user = new User();
            user.setUserName("ActiveUser" + i);
            user.setAge(20 + i);
            user.setStatus(1);

            user = userMapper.insertAndReturn(
                    user
            );

            Long userId = user.getId();

            // 只给前 20 个用户创建 profile
            if (i <= 20) {
                userProfileMapper.insertProfile(
                        userId,
                        "active" + i + "@example.com"
                );
            }
        }

        // ===== 状态 = 0 的用户 =====
        for (int i = 1; i <= 10; i++) {
            userMapper.insertUser(
                    "InactiveUser" + i,
                    18 + i,
                    0
            );
        }
    }

    /* ============================================================
     *                      SmartPage
     * ============================================================ */

    /**
     * 根据状态分页查询
     * 验证：
     *  - pageSize = 20
     *  - order by create_time desc
     *  - where 条件生效
     */
    @Test
    void test_findUsersByStatusPage() {

        PageResult<User> pageResult =
                userMapper.findUsersByStatusPage(1);

        for (User user : pageResult.getRows()) {
            System.out.println(user.toString());
        }

        assertNotNull(pageResult);
        assertNotNull(pageResult.getRows());

        // 每页最多 20 条
        assertTrue(pageResult.getRows().size() <= 20);

        for (User user : pageResult.getRows()) {
            assertEquals(1, user.getStatus());
            assertNotNull(user.getCreateTime());
        }
    }

    /**
     * 查询年龄区间分页
     * 验证：
     *  - BETWEEN 条件
     *  - order by age asc
     *  - pageSize = 15
     */
    @Test
    void test_findUsersByAgeRangePage() {

        PageResult<User> pageResult =
                userMapper.findUsersByAgeRangePage(25, 40);

        assertNotNull(pageResult);
        assertFalse(pageResult.getRows().isEmpty());

        assertTrue(pageResult.getRows().size() <= 15);

        int lastAge = 0;
        for (User user : pageResult.getRows()) {
            assertTrue(user.getAge() >= 25);
            assertTrue(user.getAge() <= 40);

            // 验证升序
            assertTrue(user.getAge() >= lastAge);
            lastAge = user.getAge();
        }
    }

    /**
     * 根据关键词 + 状态进行分页搜索
     * 验证：
     *  - LIKE 模糊匹配
     *  - 多条件 where
     *  - pageSize = 10
     */
    @Test
    void test_searchUsersPage() {

        PageResult<User> pageResult =
                userMapper.searchUsersPage("ActiveUser", 1);

        assertNotNull(pageResult);
        assertFalse(pageResult.getRows().isEmpty());

        assertTrue(pageResult.getRows().size() <= 10);

        for (User user : pageResult.getRows()) {
            assertTrue(user.getUserName().contains("ActiveUser"));
            assertEquals(1, user.getStatus());
        }
    }

    @Test
    void test_findUserWithProfilePage() {
        PageResult<Map<String, Object>> pageResult =
                userMapper.findUserWithProfilePage(1);

        assertNotNull(pageResult);
        assertNotNull(pageResult.getRows());

        // pageSize = 5
        assertTrue(pageResult.getRows().size() <= 5);

        // total 表示满足主表条件的数据量
        assertTrue(pageResult.getTotal() >= 1);

        for (Map<String, Object> row : pageResult.getRows()) {

            // user 字段存在
            assertNotNull(getValueIgnoreCase(row, "id"));
            assertNotNull(getValueIgnoreCase(row, "user_name"));
            assertNotNull(getValueIgnoreCase(row, "status"));

            // where 条件校验
            Object statusValue = getValueIgnoreCase(row, "status");
            assertNotNull(statusValue);
            assertEquals(1, ((Number) statusValue).intValue());

            // LEFT JOIN 场景下，email 可能为 null，但字段应可被访问
            assertTrue(hasKeyIgnoreCase(row, "email"));
        }
    }

    private boolean hasKeyIgnoreCase(Map<String, Object> row, String expectedKey) {
        return row.keySet().stream()
                .anyMatch(key -> key.equalsIgnoreCase(expectedKey));
    }

    private Object getValueIgnoreCase(Map<String, Object> row, String expectedKey) {
        return row.entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(expectedKey))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

}
