package io.github.forgottenlab.smartorm.test;

import io.github.forgottenlab.smartorm.demo.entity.User;
import io.github.forgottenlab.smartorm.demo.mapper.UserMapper;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartSelect 注解能力测试
 * @CreateDate 2025/12/19
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@SpringBootTest(classes = io.github.forgottenlab.smartorm.demo.SmartOrmDemoApplication.class)
@ActiveProfiles("test")
@Transactional // 每个测试结束自动回滚
@Import(MapperSelectTest.BindingCaptureConfiguration.class)
class MapperSelectTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BoundSqlCaptureInterceptor boundSqlCapture;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    /**
     * 每个测试前统一准备基础数据
     * 避免测试之间互相依赖
     */
    @BeforeEach
    void setUp() {
        if (!sqlSessionFactory.getConfiguration().getInterceptors().contains(boundSqlCapture)) {
            sqlSessionFactory.getConfiguration().addInterceptor(boundSqlCapture);
        }
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
        assertEquals(2, users.size());

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
        assertEquals(3, users.size());

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

        List<User> matches = userMapper.findUsersByName("Target");
        assertEquals(1, matches.size());
        User inserted = matches.get(0);

        User result = userMapper.findUserById(inserted.getId());

        assertNotNull(result);
        assertEquals("Target", result.getUserName());
        assertEquals(28, result.getAge());
    }

    @Test
    void test_nonJoinWhereUsesRealMyBatisParameterBindings() {
        String runtimeName = "Binding O'Reilly";
        userMapper.insertUser(runtimeName, 41, 1);
        User inserted = userMapper.findUsersByName(runtimeName).get(0);

        boundSqlCapture.clear();
        User existing = userMapper.findUserById(inserted.getId());
        CapturedBoundSql existingSql = boundSqlCapture.requireLastSelect();

        assertNotNull(existing);
        assertTrue(existingSql.sql().contains("id = ?"), existingSql.sql());
        assertFalse(existingSql.sql().contains("id = " + inserted.getId()), existingSql.sql());
        assertEquals(1, existingSql.parameterProperties().size());
        assertEquals(List.of(inserted.getId()), existingSql.parameterValues());

        boundSqlCapture.clear();
        User missing = userMapper.findUserById(Long.MAX_VALUE);
        CapturedBoundSql missingSql = boundSqlCapture.requireLastSelect();

        assertNull(missing);
        assertTrue(missingSql.sql().contains("id = ?"), missingSql.sql());
        assertEquals(List.of(Long.MAX_VALUE), missingSql.parameterValues());

        boundSqlCapture.clear();
        List<User> byName = userMapper.findUsersByName(runtimeName);
        CapturedBoundSql stringSql = boundSqlCapture.requireLastSelect();

        assertEquals(1, byName.size());
        assertTrue(stringSql.sql().contains("LIKE CONCAT('%', ?, '%')"), stringSql.sql());
        assertFalse(stringSql.sql().contains(runtimeName), stringSql.sql());
        assertEquals(1, stringSql.parameterProperties().size());
        assertEquals(List.of(runtimeName), stringSql.parameterValues());
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
        assertEquals(1, users.size());

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
        assertEquals(3, users.size());

        for (User user : users) {
            assertEquals(1, user.getStatus());
        }
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class BindingCaptureConfiguration {

        @Bean
        BoundSqlCaptureInterceptor boundSqlCaptureInterceptor() {
            return new BoundSqlCaptureInterceptor();
        }
    }

    @Intercepts({
            @Signature(
                    type = Executor.class,
                    method = "query",
                    args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
            ),
            @Signature(
                    type = Executor.class,
                    method = "query",
                    args = {
                            MappedStatement.class,
                            Object.class,
                            RowBounds.class,
                            ResultHandler.class,
                            CacheKey.class,
                            BoundSql.class
                    }
            )
    })
    static class BoundSqlCaptureInterceptor implements Interceptor {

        private final AtomicReference<CapturedBoundSql> lastSelect = new AtomicReference<>();

        @Override
        public Object intercept(Invocation invocation) throws Throwable {
            Object[] invocationArgs = invocation.getArgs();
            MappedStatement statement = (MappedStatement) invocationArgs[0];
            BoundSql boundSql = invocationArgs.length == 6
                    ? (BoundSql) invocationArgs[5]
                    : statement.getBoundSql(invocationArgs[1]);
            String sql = boundSql.getSql().replaceAll("\\s+", " ").trim();
            if (statement.getSqlCommandType() == SqlCommandType.SELECT) {
                lastSelect.set(capture(boundSql, sql));
            }
            return invocation.proceed();
        }

        void clear() {
            lastSelect.set(null);
        }

        CapturedBoundSql requireLastSelect() {
            CapturedBoundSql captured = lastSelect.get();
            assertNotNull(captured, "Expected a SELECT BoundSql snapshot");
            return captured;
        }

        private static CapturedBoundSql capture(BoundSql boundSql, String sql) {
            List<String> properties = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            Object parameterObject = boundSql.getParameterObject();
            MetaObject metaObject = parameterObject == null
                    ? null
                    : SystemMetaObject.forObject(parameterObject);

            for (ParameterMapping mapping : boundSql.getParameterMappings()) {
                if (mapping.getMode() == ParameterMode.OUT) {
                    continue;
                }
                String property = mapping.getProperty();
                properties.add(property);
                if (boundSql.hasAdditionalParameter(property)) {
                    values.add(boundSql.getAdditionalParameter(property));
                } else if (metaObject == null) {
                    values.add(null);
                } else {
                    values.add(metaObject.getValue(property));
                }
            }

            return new CapturedBoundSql(
                    sql,
                    List.copyOf(properties),
                    Collections.unmodifiableList(new ArrayList<>(values))
            );
        }
    }

    private record CapturedBoundSql(
            String sql,
            List<String> parameterProperties,
            List<Object> parameterValues
    ) {
    }
}
