package io.github.forgottenlab.smartorm.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.forgottenlab.smartorm.demo.entity.User;
import io.github.forgottenlab.smartorm.demo.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartMapper 核心快捷方法测试
 * @CreateDate 2025/12/19
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@SpringBootTest(classes = io.github.forgottenlab.smartorm.demo.SmartOrmDemoApplication.class)
@Transactional // 每个测试结束自动回滚
public class SmartMapperCoreTest {

    @Autowired
    private UserMapper userMapper;

    /**
     * insertAndReturn
     * 验证点：
     *  - insert 后主键回填
     *  - 返回实体非 null
     */
    @Test
    void test_insertAndReturn_success() {

        User user = new User();
        user.setUserName("SmartORM");
        user.setAge(22);
        user.setStatus(1);

        User saved = userMapper.insertAndReturn(user);

        assertNotNull(saved);
        assertNotNull(saved.getId());
    }

    /**
     * existsById
     * 验证点：
     *  - 已存在 id 返回 true
     *  - 不存在 id 返回 false
     */
    @Test
    void test_existsById() {

        User user = new User();
        user.setUserName("ExistsUser");
        user.setAge(20);
        user.setStatus(1);

        user = userMapper.insertAndReturn(
                user
        );

        assertTrue(userMapper.existsById(user.getId()));
        assertFalse(userMapper.existsById(-1L));
    }

    /**
     * existsBy
     * 验证点：
     *  - 指定字段存在判断
     */
    @Test
    void test_existsBy_field() {

        userMapper.insertUser("UniqueUser", 18, 1);

        assertTrue(userMapper.existsBy("user_name", "UniqueUser"));
        assertFalse(userMapper.existsBy("user_name", "NotExist"));
    }

    /**
     * findOneBy
     * 验证点：
     *  - 精确匹配返回实体
     */
    @Test
    void test_findOneBy() {

        userMapper.insertUser("FindMe", 23, 1);

        User user = userMapper.findOneBy("user_name", "FindMe");

        assertNotNull(user);
        assertEquals("FindMe", user.getUserName());
    }

    /**
     * findOne
     * 验证点：
     *  - 多条结果抛异常
     */
    @Test
    void test_findOne_throwIfMultiple() {

        userMapper.insertUser("Dup", 20, 1);
        userMapper.insertUser("Dup", 21, 1);

        assertThrows(
                RuntimeException.class,
                () -> userMapper.findOne(
                        new QueryWrapper<User>().eq("user_name", "Dup")
                )
        );
    }

}
