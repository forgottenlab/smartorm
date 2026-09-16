package io.github.forgottenlab.smartorm.builder;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.github.forgottenlab.smartorm.annotations.SmartDelete;
import io.github.forgottenlab.smartorm.annotations.SmartUpdate;
import io.github.forgottenlab.smartorm.exception.SmartOrmException;
import io.github.forgottenlab.smartorm.handler.SmartDeleteHandler;
import io.github.forgottenlab.smartorm.handler.SmartUpdateHandler;
import io.github.forgottenlab.smartorm.mapper.SmartMapper;
import io.github.forgottenlab.smartorm.model.SmartContext;
import io.github.forgottenlab.smartorm.resolver.SmartDeleteResolver;
import io.github.forgottenlab.smartorm.resolver.SmartUpdateResolver;
import io.github.forgottenlab.smartorm.resolver.meta.DeleteMeta;
import io.github.forgottenlab.smartorm.resolver.meta.UpdateMeta;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SmartMutationWhereSafetyTest {

    @Test
    void blankSmartUpdateIsRejectedByDefault() throws Exception {
        UpdateMeta meta = updateMeta("", method("blankUpdate", Integer.class));

        SmartOrmException exception = assertThrows(
                SmartOrmException.class,
                () -> SmartWrapperBuilder.buildUpdateWrapper(meta, new Object[]{1})
        );

        assertRejectedMessage(exception, "@SmartUpdate", "UPDATE", "blankUpdate");
    }

    @Test
    void whitespaceSmartUpdateIsRejectedByDefault() throws Exception {
        UpdateMeta meta = updateMeta(" \r\n\t ", method("blankUpdate", Integer.class));

        SmartOrmException exception = assertThrows(
                SmartOrmException.class,
                () -> SmartWrapperBuilder.buildUpdateWrapper(meta, new Object[]{1})
        );

        assertRejectedMessage(exception, "@SmartUpdate", "UPDATE", "blankUpdate");
    }

    @Test
    void whereKeywordWithoutUpdatePredicateIsRejected() throws Exception {
        UpdateMeta meta = updateMeta("WHERE", method("blankUpdate", Integer.class));

        SmartOrmException exception = assertThrows(
                SmartOrmException.class,
                () -> SmartWrapperBuilder.buildUpdateWrapper(meta, new Object[]{1})
        );

        assertRejectedMessage(exception, "@SmartUpdate", "UPDATE", "blankUpdate");
    }

    @Test
    void blankSmartDeleteIsRejectedByDefault() throws Exception {
        DeleteMeta meta = deleteMeta(null, method("blankDelete"));

        SmartOrmException exception = assertThrows(
                SmartOrmException.class,
                () -> SmartWrapperBuilder.buildDeleteWrapper(meta, new Object[0])
        );

        assertRejectedMessage(exception, "@SmartDelete", "DELETE", "blankDelete");
    }

    @Test
    void whitespaceSmartDeleteIsRejectedByDefault() throws Exception {
        DeleteMeta meta = deleteMeta(" \r\n\t ", method("blankDelete"));

        SmartOrmException exception = assertThrows(
                SmartOrmException.class,
                () -> SmartWrapperBuilder.buildDeleteWrapper(meta, new Object[0])
        );

        assertRejectedMessage(exception, "@SmartDelete", "DELETE", "blankDelete");
    }

    @Test
    void whereKeywordWithoutDeletePredicateIsRejected() throws Exception {
        DeleteMeta meta = deleteMeta("WHERE", method("blankDelete"));

        SmartOrmException exception = assertThrows(
                SmartOrmException.class,
                () -> SmartWrapperBuilder.buildDeleteWrapper(meta, new Object[0])
        );

        assertRejectedMessage(exception, "@SmartDelete", "DELETE", "blankDelete");
    }

    @Test
    void smartUpdateWithEffectiveWhereKeepsSqlBindingAndInputOrder() throws Exception {
        UpdateMeta meta = updateMeta("id = #{1}", method("updateById", Integer.class, Long.class));
        Object[] args = {0, 7L};

        UpdateWrapper<Object> wrapper = SmartWrapperBuilder.buildUpdateWrapper(meta, args);

        assertEquals("(id = 7)", wrapper.getSqlSegment());
        assertEquals("status=#{ew.paramNameValuePairs.MPGENVAL1}", wrapper.getSqlSet());
        assertEquals(Map.of("MPGENVAL1", 0), wrapper.getParamNameValuePairs());
        assertEquals(countPlaceholders(wrapper.getSqlSet()), wrapper.getParamNameValuePairs().size());
        assertArrayEquals(new Object[]{0, 7L}, args);
    }

    @Test
    void smartDeleteWithEffectiveWhereKeepsSqlAndInputOrder() throws Exception {
        DeleteMeta meta = deleteMeta("status = #{0}", method("deleteByStatus", Integer.class));
        Object[] args = {0};

        QueryWrapper<Object> wrapper = SmartWrapperBuilder.buildDeleteWrapper(meta, args);

        String sql = wrapper.getSqlSegment();
        assertEquals(1, countPlaceholders(sql));
        assertFalse(sql.contains("status = 0"));
        String parameterPrefix = "#{ew.paramNameValuePairs.";
        assertTrue(sql.contains(parameterPrefix));
        int parameterStart = sql.indexOf(parameterPrefix) + parameterPrefix.length();
        int parameterEnd = sql.indexOf('}', parameterStart);
        String referencedParameter = sql.substring(parameterStart, parameterEnd);
        assertEquals(0, wrapper.getParamNameValuePairs().get(referencedParameter));
        assertArrayEquals(new Object[]{0}, args);
    }

    @Test
    void annotationOptInDefaultsToFalse() throws Exception {
        SmartUpdate update = annotatedMethod("defaultUpdate", Integer.class).getAnnotation(SmartUpdate.class);
        SmartDelete delete = annotatedMethod("defaultDelete").getAnnotation(SmartDelete.class);

        assertFalse(update.allowFullTable());
        assertFalse(delete.allowFullTable());
    }

    @Test
    void smartUpdateHandlerRejectsBeforeMapperExecutionAndKeepsPreciseMessage() throws Exception {
        Method method = annotatedMethod("defaultUpdate", Integer.class);
        SmartUpdate annotation = method.getAnnotation(SmartUpdate.class);
        SmartContext context = new SmartContext(new Object(), method, AnnotatedMapper.class, new Object[]{1});

        SmartOrmException exception = assertThrows(
                SmartOrmException.class,
                () -> new SmartUpdateHandler().handle(annotation, context)
        );

        assertRejectedMessage(exception, "@SmartUpdate", "UPDATE", "defaultUpdate");
    }

    @Test
    void smartDeleteHandlerRejectsBeforeMapperExecutionAndKeepsPreciseMessage() throws Exception {
        Method method = annotatedMethod("defaultDelete");
        SmartDelete annotation = method.getAnnotation(SmartDelete.class);
        SmartContext context = new SmartContext(new Object(), method, AnnotatedMapper.class, new Object[0]);

        SmartOrmException exception = assertThrows(
                SmartOrmException.class,
                () -> new SmartDeleteHandler().handle(annotation, context)
        );

        assertRejectedMessage(exception, "@SmartDelete", "DELETE", "defaultDelete");
    }

    @Test
    void explicitSmartUpdateOptInAllowsDeliberateFullTablePlan() throws Exception {
        Method method = annotatedMethod("fullTableUpdate", Integer.class);
        SmartUpdate annotation = method.getAnnotation(SmartUpdate.class);
        UpdateMeta meta = SmartUpdateResolver.resolve(method, annotation, AnnotatedMapper.class);
        Object[] args = {2};

        UpdateWrapper<Object> wrapper = SmartWrapperBuilder.buildUpdateWrapper(meta, args);

        assertTrue(meta.allowFullTable);
        assertEquals("", wrapper.getSqlSegment());
        assertEquals("status=#{ew.paramNameValuePairs.MPGENVAL1}", wrapper.getSqlSet());
        assertEquals(Map.of("MPGENVAL1", 2), wrapper.getParamNameValuePairs());
        assertEquals(countPlaceholders(wrapper.getSqlSet()), wrapper.getParamNameValuePairs().size());
        assertArrayEquals(new Object[]{2}, args);
    }

    @Test
    void explicitSmartDeleteOptInAllowsDeliberateFullTablePlan() throws Exception {
        Method method = annotatedMethod("fullTableDelete");
        SmartDelete annotation = method.getAnnotation(SmartDelete.class);
        DeleteMeta meta = SmartDeleteResolver.resolve(method, annotation, AnnotatedMapper.class);

        QueryWrapper<Object> wrapper = SmartWrapperBuilder.buildDeleteWrapper(meta, new Object[0]);

        assertTrue(meta.allowFullTable);
        assertEquals("", wrapper.getSqlSegment());
        assertTrue(wrapper.getParamNameValuePairs().isEmpty());
    }

    @Test
    void optInDoesNotChangeEffectiveWhereRenderingOrBinding() throws Exception {
        Method method = annotatedMethod("optInUpdateById", Integer.class, Long.class);
        SmartUpdate annotation = method.getAnnotation(SmartUpdate.class);
        UpdateMeta meta = SmartUpdateResolver.resolve(method, annotation, AnnotatedMapper.class);
        Object[] args = {3, 11L};

        UpdateWrapper<Object> wrapper = SmartWrapperBuilder.buildUpdateWrapper(meta, args);

        assertEquals("(id = 11)", wrapper.getSqlSegment());
        assertEquals("status=#{ew.paramNameValuePairs.MPGENVAL1}", wrapper.getSqlSet());
        assertEquals(Map.of("MPGENVAL1", 3), wrapper.getParamNameValuePairs());
        assertEquals(countPlaceholders(wrapper.getSqlSet()), wrapper.getParamNameValuePairs().size());
        assertArrayEquals(new Object[]{3, 11L}, args);
    }

    @Test
    void runtimeDisabledUpdateConditionsAreRejectedAndSetParametersDoNotCountAsWhere() throws Exception {
        UpdateMeta meta = updateMeta("unused", method("blankUpdate", Integer.class));
        UpdateWrapper<Object> wrapper = new UpdateWrapper<>();
        wrapper.set("status", 1);
        wrapper.eq(false, "id", null);

        SmartOrmException exception = assertThrows(
                SmartOrmException.class,
                () -> SmartMutationSafetyGuard.verifyUpdate(meta, wrapper)
        );

        assertRejectedMessage(exception, "@SmartUpdate", "UPDATE", "blankUpdate");
    }

    @Test
    void runtimeDisabledDeleteConditionsAreRejected() throws Exception {
        DeleteMeta meta = deleteMeta("unused", method("blankDelete"));
        QueryWrapper<Object> wrapper = new QueryWrapper<>();
        wrapper.eq(false, "id", null);

        SmartOrmException exception = assertThrows(
                SmartOrmException.class,
                () -> SmartMutationSafetyGuard.verifyDelete(meta, wrapper)
        );

        assertRejectedMessage(exception, "@SmartDelete", "DELETE", "blankDelete");
    }

    @Test
    void remainingRuntimeUpdateConditionIsAccepted() throws Exception {
        UpdateMeta meta = updateMeta("unused", method("blankUpdate", Integer.class));
        UpdateWrapper<Object> wrapper = new UpdateWrapper<>();
        wrapper.eq(false, "id", null);
        wrapper.eq(true, "tenant_id", 9L);

        SmartMutationSafetyGuard.verifyUpdate(meta, wrapper);

        assertEquals("(tenant_id = #{ew.paramNameValuePairs.MPGENVAL1})", wrapper.getSqlSegment());
        assertEquals(Map.of("MPGENVAL1", 9L), wrapper.getParamNameValuePairs());
    }

    @Test
    void remainingRuntimeDeleteConditionIsAccepted() throws Exception {
        DeleteMeta meta = deleteMeta("unused", method("blankDelete"));
        QueryWrapper<Object> wrapper = new QueryWrapper<>();
        wrapper.eq(false, "id", null);
        wrapper.eq(true, "tenant_id", 9L);

        SmartMutationSafetyGuard.verifyDelete(meta, wrapper);

        assertEquals("(tenant_id = #{ew.paramNameValuePairs.MPGENVAL1})", wrapper.getSqlSegment());
        assertEquals(Map.of("MPGENVAL1", 9L), wrapper.getParamNameValuePairs());
    }

    private static UpdateMeta updateMeta(String where, Method method) {
        UpdateMeta meta = new UpdateMeta();
        meta.method = method;
        meta.fields = new String[]{"status"};
        meta.values = new String[]{"#{0}"};
        meta.where = where;
        return meta;
    }

    private static DeleteMeta deleteMeta(String where, Method method) {
        DeleteMeta meta = new DeleteMeta();
        meta.method = method;
        meta.where = where;
        return meta;
    }

    private static Method method(String name, Class<?>... parameterTypes) throws Exception {
        return SafetyContract.class.getDeclaredMethod(name, parameterTypes);
    }

    private static Method annotatedMethod(String name, Class<?>... parameterTypes) throws Exception {
        return AnnotatedMapper.class.getDeclaredMethod(name, parameterTypes);
    }

    private static int countPlaceholders(String sql) {
        int count = 0;
        int index = 0;
        while ((index = sql.indexOf("#{", index)) >= 0) {
            count++;
            index += 2;
        }
        return count;
    }

    private static void assertRejectedMessage(
            SmartOrmException exception,
            String annotation,
            String operation,
            String methodName
    ) {
        assertTrue(exception.getMessage().contains(annotation));
        assertTrue(exception.getMessage().contains(operation));
        assertTrue(exception.getMessage().contains("no effective WHERE"));
        assertTrue(exception.getMessage().contains("denied by default"));
        assertTrue(exception.getMessage().contains("allowFullTable = true"));
        assertTrue(exception.getMessage().contains(methodName));
    }

    private interface SafetyContract {

        int blankUpdate(Integer status);

        int blankDelete();

        int updateById(Integer status, Long id);

        int deleteByStatus(Integer status);
    }

    private interface AnnotatedMapper extends SmartMapper<TestEntity> {

        @SmartUpdate(fields = "status", values = "#{0}")
        int defaultUpdate(Integer status);

        @SmartDelete
        int defaultDelete();

        @SmartUpdate(fields = "status", values = "#{0}", allowFullTable = true)
        int fullTableUpdate(Integer status);

        @SmartDelete(allowFullTable = true)
        int fullTableDelete();

        @SmartUpdate(
                fields = "status",
                values = "#{0}",
                where = "id = #{1}",
                allowFullTable = true
        )
        int optInUpdateById(Integer status, Long id);
    }

    private static final class TestEntity {
    }
}
