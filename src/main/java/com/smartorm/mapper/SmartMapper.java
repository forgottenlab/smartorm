package com.smartorm.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartorm.resover.*;
import com.smartorm.annotation.*;
import com.smartorm.resover.meta.*;
import com.smartorm.util.*;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details 智能 Mapper（基于MybatisPlus的BaseMapper）
 * @CreateDate 2025/11/25
 * @LastModified 2025/11/25
 * @VersionHistory [版本历史]
 */
public interface SmartMapper<T> extends BaseMapper<T> {

    /**
     * 新增且返回实体
     * @param entity 类型
     * @return T 实体
     */
    default T insertAndReturn(T entity) {
        this.insert(entity);
        return entity;
    }

    /**
     * 根据id判断是否存在
     * @param id 主键
     * @return boolean 是否存在
     */
    default boolean existsById(Serializable id) {
        return this.selectById(id) != null;
    }

    /**
     * 根据条件判断是否存在
     * @param field 数据库字段名（非 Java 属性名）
     * @param value 值
     * @return boolean 是否存在
     */
    default boolean existsBy(String field, Object value) {
        return exists(new QueryWrapper<T>().eq(field, value));
    }

    /**
     * 根据条件的单条查询
     * @param field 字段名（非 Java 属性名）
     * @param value 值
     * @return
     */
    default T findOneBy(String field, Object value) {
        return findOne(new QueryWrapper<T>().eq(field, value));
    }

    /**
     * 单条查询
     * @param wrapper 条件
     * @return 实体
     */
    default T findOne(Wrapper<T> wrapper) {
        List<T> list = this.selectList(wrapper);
        if (list.size() > 1) throw new RuntimeException("结果超过1条");
        return list.isEmpty() ? null : list.get(0);
    }

    // TODO：拓展用户的自定义方法
    /**
     * 生命周期 Hook
     */
    default void beforeSmartOperation(String methodName, Object[] args) {}

    default void afterSmartOperation(String methodName, Object result) {}

    default void onSmartException(String methodName, Throwable e) {}


    /* ==========================================================================
     *                          实用工具
     * ========================================================================== */

    /**
     * 私有工具方法 -- 获取 mapper 接口类
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
     * 私有工具方法 -- 获取实体类
     */
    @SuppressWarnings("unchecked")
    default Class<T> getEntityClass() {
        // this: 代理类  ->  mapper 接口 ->  SmartMapper<User>
        Class<?> mapperInterface = getMapperInterfaceClass();

        Type generic = mapperInterface.getGenericInterfaces()[0];
        ParameterizedType p = (ParameterizedType) generic;

        return (Class<T>) p.getActualTypeArguments()[0];
    }
}