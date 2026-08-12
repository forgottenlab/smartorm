package io.github.forgottenlab.smartorm.test;

import io.github.forgottenlab.smartorm.demo.dto.UserSimpleDTO;
import io.github.forgottenlab.smartorm.demo.entity.User;
import io.github.forgottenlab.smartorm.demo.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SelectResultType 返回类型推断与显式指定测试
 * @CreateDate 2025/12/19
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@SpringBootTest(classes = io.github.forgottenlab.smartorm.demo.SmartOrmDemoApplication.class)
@ActiveProfiles("test")
@Transactional
class MapperSelectResultTypeTest {

    @Autowired
    private UserMapper userMapper;

    /*
     * 测试目标：
     * 显式 ENTITY_LIST 行为
     */
    @Test
    void test_findAllUsers_entityList() {
        userMapper.insertUser("C", 22, 1);

        List<User> users = userMapper.findAllUsersAsEntity();

        assertEquals(1, users.size());
        assertInstanceOf(User.class, users.get(0));
    }

    /*
     * 测试目标：
     * MAP_LIST 行为
     */
    @Test
    void test_findAllUsers_mapList() {
        userMapper.insertUser("D", 23, 1);

        List<Map<String, Object>> maps =
                userMapper.findAllUsersAsMap();

        assertEquals(1, maps.size());
        assertTrue(maps.get(0).containsKey("user_name"));
    }

    /*
     * 测试目标：
     * DTO_LIST 行为
     * 不需要用户指定 DTO.class
     */
    @Test
    void test_findAllUsers_dtoList() {
        userMapper.insertUser("E", 24, 1);

        List<UserSimpleDTO> dtos =
                userMapper.findAllUsersAsDTO();

        assertEquals(1, dtos.size());
        assertNotNull(dtos.get(0).getUserName());
    }
}
