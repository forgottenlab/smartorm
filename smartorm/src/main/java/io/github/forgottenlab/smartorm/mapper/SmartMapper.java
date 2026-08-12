package io.github.forgottenlab.smartorm.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.yulichang.base.MPJBaseMapper;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details SmartORM 增强 Mapper 接口
 * @CreateDate 2025/11/25
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public interface SmartMapper<T> extends BaseMapper<T>, MPJBaseMapper<T> {

    /**
     * 新增实体并返回实体本身
     */
    default T insertAndReturn(T entity) {
        this.insert(entity);
        return entity;
    }

    /**
     * 根据主键判断是否存在
     */
    default boolean existsById(Serializable id) {
        return this.selectById(id) != null;
    }

    /**
     * 根据字段和值判断是否存在
     *
     * @param field 数据库字段名（非 Java 属性名）
     * @param value 字段值
     */
    default boolean existsBy(String field, Object value) {
        return exists(new QueryWrapper<T>().eq(field, value));
    }

    /**
     * 根据字段和值查询单条记录
     *
     * @param field 数据库字段名（非 Java 属性名）
     * @param value 字段值
     */
    default T findOneBy(String field, Object value) {
        return findOne(new QueryWrapper<T>().eq(field, value));
    }

    /**
     * 根据条件查询单条记录
     */
    default T findOne(Wrapper<T> wrapper) {
        List<T> list = this.selectList(wrapper);
        if (list.size() > 1) {
            throw new RuntimeException("结果超过 1 条");
        }
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 生命周期 Hook：执行前
     */
    default void beforeSmartOperation(String methodName, Object[] args) {
    }

    /**
     * 生命周期 Hook：执行后
     */
    default void afterSmartOperation(String methodName, Object result) {
    }

    /**
     * 生命周期 Hook：异常时
     */
    default void onSmartException(String methodName, Throwable e) {
    }

    /* ======================================================================
     *                            内部辅助方法
     * ====================================================================== */

    /**
     * 获取当前 Mapper 接口类型
     */
    @SuppressWarnings("unchecked")
    default Class<T> getMapperInterfaceClass() {
        for (Class<?> itf : this.getClass().getInterfaces()) {
            if (SmartMapper.class.isAssignableFrom(itf)) {
                return (Class<T>) itf;
            }
        }
        throw new IllegalStateException("无法找到 Mapper 接口类");
    }

    /**
     * 获取当前 Mapper 对应的实体类型
     */
    @SuppressWarnings("unchecked")
    default Class<T> getEntityClass() {
        Class<?> mapperInterface = getMapperInterfaceClass();

        Type generic = mapperInterface.getGenericInterfaces()[0];
        ParameterizedType p = (ParameterizedType) generic;

        return (Class<T>) p.getActualTypeArguments()[0];
    }
}