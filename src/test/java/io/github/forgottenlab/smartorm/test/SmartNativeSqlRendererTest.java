package io.github.forgottenlab.smartorm.test;

import io.github.forgottenlab.smartorm.annotations.JoinType;
import io.github.forgottenlab.smartorm.demo.entity.User;
import io.github.forgottenlab.smartorm.exception.SmartOrmException;
import io.github.forgottenlab.smartorm.resolver.meta.JoinMeta;
import io.github.forgottenlab.smartorm.resolver.meta.PageMeta;
import io.github.forgottenlab.smartorm.resolver.meta.SelectMeta;
import io.github.forgottenlab.smartorm.sql.model.RenderedPageSql;
import io.github.forgottenlab.smartorm.sql.model.RenderedSql;
import io.github.forgottenlab.smartorm.sql.renderer.SmartPageSqlRenderer;
import io.github.forgottenlab.smartorm.sql.renderer.SmartSelectSqlRenderer;
import io.github.forgottenlab.smartorm.util.SmartExpressionUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SmartNativeSqlRendererTest {

    @Test
    void rendersSequentialParametersInSqlOrder() {
        Object[] args = {"ACTIVE", 18};

        RenderedSql rendered = SmartSelectSqlRenderer.render(
                selectMeta("user.status = #{0} AND user.age > #{1}", List.of()),
                args
        );

        assertEquals(
                "SELECT user.id FROM user WHERE user.status = #{params[0]} AND user.age > #{params[1]}",
                rendered.getSql()
        );
        assertEquals(List.of("ACTIVE", 18), rendered.getParams());
        assertBindingCount(rendered.getSql(), rendered.getParams());
        assertArrayEquals(new Object[]{"ACTIVE", 18}, args);
    }

    @Test
    void compactsOutOfOrderParametersBySqlReferenceOrder() {
        Object[] args = {"ZERO", "UNUSED", "TWO"};

        RenderedSql rendered = SmartSelectSqlRenderer.render(
                selectMeta("user.status = #{2} AND user.user_name = #{0}", List.of()),
                args
        );

        assertEquals(
                "SELECT user.id FROM user WHERE user.status = #{params[0]} AND user.user_name = #{params[1]}",
                rendered.getSql()
        );
        assertEquals(List.of("TWO", "ZERO"), rendered.getParams());
        assertBindingCount(rendered.getSql(), rendered.getParams());
        assertArrayEquals(new Object[]{"ZERO", "UNUSED", "TWO"}, args);
    }

    @Test
    void compactsSparseParametersWithoutUnusedArguments() {
        Object[] args = {"ZERO", "UNUSED_1", "UNUSED_2", "THREE"};

        RenderedSql rendered = SmartSelectSqlRenderer.render(
                selectMeta("user.user_name = #{0} AND user.status = #{3}", List.of()),
                args
        );

        assertEquals(
                "SELECT user.id FROM user WHERE user.user_name = #{params[0]} AND user.status = #{params[1]}",
                rendered.getSql()
        );
        assertEquals(List.of("ZERO", "THREE"), rendered.getParams());
        assertBindingCount(rendered.getSql(), rendered.getParams());
        assertArrayEquals(new Object[]{"ZERO", "UNUSED_1", "UNUSED_2", "THREE"}, args);
    }

    @Test
    void preservesRepeatedParameterReferencesInPlaceholderOrder() {
        Object[] args = {42L};

        RenderedSql rendered = SmartSelectSqlRenderer.render(
                selectMeta("user.id = #{0} OR user.parent_id = #{0}", List.of()),
                args
        );

        assertEquals(
                "SELECT user.id FROM user WHERE user.id = #{params[0]} OR user.parent_id = #{params[1]}",
                rendered.getSql()
        );
        assertEquals(List.of(42L, 42L), rendered.getParams());
        assertBindingCount(rendered.getSql(), rendered.getParams());
        assertArrayEquals(new Object[]{42L}, args);
    }

    @Test
    void appendsToAnExistingCompactParameterList() {
        Object[] args = {"ZERO", "UNUSED", "TWO"};
        List<Object> params = new ArrayList<>(List.of("JOIN_VALUE"));

        String sql = SmartExpressionUtil.parseSqlWithParams(
                "user.status = #{2} AND user.user_name = #{0}",
                args,
                params
        );

        assertEquals(
                "user.status = #{params[1]} AND user.user_name = #{params[2]}",
                sql
        );
        assertEquals(List.of("JOIN_VALUE", "TWO", "ZERO"), params);
        assertArrayEquals(new Object[]{"ZERO", "UNUSED", "TWO"}, args);
    }

    @Test
    void rejectsOutOfRangeMethodParameterIndex() {
        SmartOrmException error = assertThrows(
                SmartOrmException.class,
                () -> SmartExpressionUtil.parseSqlWithParams(
                        "user.status = #{2}",
                        new Object[]{"ONLY"},
                        new ArrayList<>()
                )
        );

        assertTrue(error.getMessage().contains("参数索引越界"));
    }

    @Test
    void rejectsPlaceholderWhenMethodArgumentsAreMissing() {
        SmartOrmException error = assertThrows(
                SmartOrmException.class,
                () -> SmartExpressionUtil.parseSqlWithParams(
                        "user.status = #{0}",
                        null,
                        new ArrayList<>()
                )
        );

        assertTrue(error.getMessage().contains("可用参数数量: 0"));
    }

    @Test
    void rendersExplicitAndInferredJoinsWhileBindingOnlyRuntimeValues() {
        String explicitOn = "user.id = p.user_id AND p.state = #{1}";
        JoinMeta profileJoin = join("user_profile", "p", JoinType.LEFT, explicitOn, true);
        JoinMeta roleJoin = join("user_role", "r", JoinType.INNER, "", false);
        List<JoinMeta> joins = new ArrayList<>(List.of(profileJoin, roleJoin));
        Object[] args = {"ACTIVE", "VERIFIED"};

        SelectMeta meta = selectMeta("user.status = #{0}", joins);
        meta.fields = new String[]{"user.id", "p.email", "r.role_code"};

        RenderedSql rendered = SmartSelectSqlRenderer.render(meta, args);

        assertEquals(
                "SELECT user.id,p.email,r.role_code FROM user "
                        + "LEFT JOIN user_profile p ON user.id = p.user_id AND p.state = #{params[0]} "
                        + "INNER JOIN user_role r ON user.id = r.user_id "
                        + "WHERE user.status = #{params[1]}",
                rendered.getSql()
        );
        assertEquals(List.of("VERIFIED", "ACTIVE"), rendered.getParams());
        assertBindingCount(rendered.getSql(), rendered.getParams());
        assertEquals(explicitOn, profileJoin.on);
        assertEquals("p", profileJoin.alias);
        assertEquals("user_profile", profileJoin.joinTable);
        assertArrayEquals(new Object[]{"ACTIVE", "VERIFIED"}, args);
    }

    @Test
    void pageRendererUsesTheSameJoinParameterContract() {
        JoinMeta profileJoin = join(
                "user_profile",
                "p",
                JoinType.LEFT,
                "user.id = p.user_id AND p.kind = #{2}",
                true
        );
        Object[] args = {"ACTIVE", "UNUSED", "PRIMARY"};

        PageMeta meta = new PageMeta();
        meta.entityClass = User.class;
        meta.fields = new String[]{"user.id", "p.email"};
        meta.where = "user.status = #{0}";
        meta.orderBy = "";
        meta.joins = new ArrayList<>(List.of(profileJoin));
        meta.page = 2;
        meta.pageSize = 10;

        RenderedPageSql rendered = SmartPageSqlRenderer.render(meta, args);

        String baseSql = "SELECT user.id,p.email FROM user "
                + "LEFT JOIN user_profile p ON user.id = p.user_id AND p.kind = #{params[0]} "
                + "WHERE user.status = #{params[1]}";
        assertEquals(baseSql + " LIMIT 10 OFFSET 10", rendered.getPageSql());
        assertEquals("SELECT COUNT(*) FROM (" + baseSql + ") tmp", rendered.getCountSql());
        assertEquals(List.of("PRIMARY", "ACTIVE"), rendered.getParams());
        assertBindingCount(rendered.getPageSql(), rendered.getParams());
        assertBindingCount(rendered.getCountSql(), rendered.getParams());
        assertArrayEquals(new Object[]{"ACTIVE", "UNUSED", "PRIMARY"}, args);
    }

    @Test
    void rejectsStatementSeparatorInExplicitJoinPredicate() {
        JoinMeta join = join(
                "user_profile",
                "p",
                JoinType.LEFT,
                "user.id = p.user_id; DELETE FROM user",
                true
        );

        assertThrows(
                SmartOrmException.class,
                () -> SmartSelectSqlRenderer.render(selectMeta("", List.of(join)), new Object[]{})
        );
    }

    private static SelectMeta selectMeta(String where, List<JoinMeta> joins) {
        SelectMeta meta = new SelectMeta();
        meta.entityClass = User.class;
        meta.fields = new String[]{"user.id"};
        meta.where = where;
        meta.orderBy = "";
        meta.joins = joins;
        return meta;
    }

    private static JoinMeta join(
            String table,
            String alias,
            JoinType type,
            String on,
            boolean userDefinedOn
    ) {
        JoinMeta join = new JoinMeta();
        join.joinTable = table;
        join.alias = alias;
        join.joinType = type;
        join.on = on;
        join.userDefinedOn = userDefinedOn;
        return join;
    }

    private static void assertBindingCount(String sql, List<Object> params) {
        int count = 0;
        int offset = 0;
        while ((offset = sql.indexOf("#{params[", offset)) >= 0) {
            count++;
            offset += "#{params[".length();
        }
        assertEquals(params.size(), count, "SQL 占位符数量必须等于绑定参数数量");
    }
}
