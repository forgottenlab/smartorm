package io.github.forgottenlab.smartorm.support.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.forgottenlab.smartorm.annotations.SmartSelect;
import io.github.forgottenlab.smartorm.handler.SmartSelectHandler;
import io.github.forgottenlab.smartorm.mapper.SmartMapper;
import io.github.forgottenlab.smartorm.model.SmartContext;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SmartQuerySupportBindingTest {

    @Test
    void bindsIntegerWithoutEmbeddingItInSqlStructure() {
        QueryWrapper<Object> wrapper = new QueryWrapper<>();
        Object[] args = {42};

        SmartQuerySupport.applyWhere(wrapper, "id = #{0}", args);

        assertEquals("(id = #{ew.paramNameValuePairs.MPGENVAL1})", wrapper.getSqlSegment());
        assertFalse(wrapper.getSqlSegment().contains("42"));
        assertEquals(Map.of("MPGENVAL1", 42), wrapper.getParamNameValuePairs());
        assertArrayEquals(new Object[]{42}, args);
    }

    @Test
    void bindsStringWithoutEmbeddingItInSqlStructure() {
        QueryWrapper<Object> wrapper = new QueryWrapper<>();
        String runtimeValue = "O'Reilly";

        SmartQuerySupport.applyWhere(wrapper, "name = #{0}", new Object[]{runtimeValue});

        assertEquals("(name = #{ew.paramNameValuePairs.MPGENVAL1})", wrapper.getSqlSegment());
        assertFalse(wrapper.getSqlSegment().contains(runtimeValue));
        assertEquals(Map.of("MPGENVAL1", runtimeValue), wrapper.getParamNameValuePairs());
    }

    @Test
    void keepsMultipleArgumentsLogicallyAssociated() {
        QueryWrapper<Object> wrapper = new QueryWrapper<>();

        SmartQuerySupport.applyWhere(
                wrapper,
                "status = #{0} AND type = #{1}",
                new Object[]{1, "ADMIN"}
        );

        assertEquals(
                "(status = #{ew.paramNameValuePairs.MPGENVAL1} "
                        + "AND type = #{ew.paramNameValuePairs.MPGENVAL2})",
                wrapper.getSqlSegment()
        );
        assertEquals(
                Map.of("MPGENVAL1", 1, "MPGENVAL2", "ADMIN"),
                wrapper.getParamNameValuePairs()
        );
    }

    @Test
    void reusesOneBindingForRepeatedMethodArgument() {
        QueryWrapper<Object> wrapper = new QueryWrapper<>();

        SmartQuerySupport.applyWhere(
                wrapper,
                "id = #{0} OR parent_id = #{0}",
                new Object[]{42L}
        );

        assertEquals(
                "(id = #{ew.paramNameValuePairs.MPGENVAL1} "
                        + "OR parent_id = #{ew.paramNameValuePairs.MPGENVAL1})",
                wrapper.getSqlSegment()
        );
        assertEquals(Map.of("MPGENVAL1", 42L), wrapper.getParamNameValuePairs());
    }

    @Test
    void compactsSparseArgumentsInSqlReferenceOrder() {
        QueryWrapper<Object> wrapper = new QueryWrapper<>();
        Object[] args = {"ACTIVE", "UNUSED", "ADMIN"};

        SmartQuerySupport.applyWhere(
                wrapper,
                "type = #{2} AND status = #{0}",
                args
        );

        assertEquals(
                "(type = #{ew.paramNameValuePairs.MPGENVAL1} "
                        + "AND status = #{ew.paramNameValuePairs.MPGENVAL2})",
                wrapper.getSqlSegment()
        );
        assertEquals(
                Map.of("MPGENVAL1", "ADMIN", "MPGENVAL2", "ACTIVE"),
                wrapper.getParamNameValuePairs()
        );
        assertFalse(wrapper.getParamNameValuePairs().containsValue("UNUSED"));
        assertArrayEquals(new Object[]{"ACTIVE", "UNUSED", "ADMIN"}, args);
    }

    @Test
    void preservesNullAsABoundValueWithoutInventingIsNullSemantics() {
        QueryWrapper<Object> wrapper = new QueryWrapper<>();

        SmartQuerySupport.applyWhere(wrapper, "deleted_at = #{0}", new Object[]{null});

        assertEquals(
                "(deleted_at = #{ew.paramNameValuePairs.MPGENVAL1})",
                wrapper.getSqlSegment()
        );
        assertFalse(wrapper.getSqlSegment().contains("NULL"));
        assertTrue(wrapper.getParamNameValuePairs().containsKey("MPGENVAL1"));
        assertNull(wrapper.getParamNameValuePairs().get("MPGENVAL1"));
    }

    @Test
    void nonJoinSmartSelectHandlerUsesBoundWhereParameters() throws Exception {
        Method method = BindingMapper.class.getDeclaredMethod("findById", Integer.class);
        SmartSelect annotation = method.getAnnotation(SmartSelect.class);
        AtomicReference<QueryWrapper<?>> capturedWrapper = new AtomicReference<>();
        BindingMapper mapper = (BindingMapper) Proxy.newProxyInstance(
                BindingMapper.class.getClassLoader(),
                new Class<?>[]{BindingMapper.class},
                (proxy, invokedMethod, invokedArgs) -> {
                    if (invokedMethod.getName().equals("selectOne")) {
                        capturedWrapper.set((QueryWrapper<?>) invokedArgs[0]);
                        return null;
                    }
                    throw new AssertionError("Unexpected mapper call: " + invokedMethod);
                }
        );
        Object[] args = {42};

        Object result = new SmartSelectHandler(null).handle(
                annotation,
                new SmartContext(mapper, method, BindingMapper.class, args)
        );

        assertNull(result);
        QueryWrapper<?> wrapper = capturedWrapper.get();
        assertEquals(
                "(id = #{ew.paramNameValuePairs.MPGENVAL1}) LIMIT 1",
                wrapper.getSqlSegment()
        );
        assertEquals(Map.of("MPGENVAL1", 42), wrapper.getParamNameValuePairs());
        assertArrayEquals(new Object[]{42}, args);
    }

    private interface BindingMapper extends SmartMapper<BindingEntity> {

        @SmartSelect(where = "id = #{0}", limit = 1)
        BindingEntity findById(Integer id);
    }

    private static final class BindingEntity {
    }
}
