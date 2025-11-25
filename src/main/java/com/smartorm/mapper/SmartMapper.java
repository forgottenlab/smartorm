package com.smartorm.mapper;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.smartorm.resover.*;
import com.smartorm.core.SmartWrapperBuilder;
import com.smartorm.core.SmartSqlExecutor;
import com.smartorm.annotation.*;
import com.smartorm.model.PageResult;
import com.smartorm.util.AnnotationCacheUtil;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author <a href="#">Forgotten.</a>
 * @Details 智能 Mapper（基于MybatisPlus的BaseMapper）
 * @CreateDate 2025/11/25
 * @LastModified 2025/11/25
 * @VersionHistory [版本历史]
 */
public interface SmartMapper<T> extends BaseMapper<T> {

    /**
     * 查询 -- 列表 @SmartSelect
     * @param methodName    方法名
     * @param args          参数
     * @return List<T>
     */
    default List<T> findByAnnotation(String methodName, Object[] args) {

        // 获取注解
        SmartSelect select = AnnotationCacheUtil.getSmartSelect(
                getMapperInterfaceClass(), methodName, getParameterTypes(args)
        );

        // 解析注解为 meta
        SmartSelectResolver.SelectMeta meta = SmartSelectResolver.resolve(select);

        // 构建 QueryWrapper
        QueryWrapper<T> wrapper = SmartWrapperBuilder.buildSelectWrapper(meta, args);

        return SmartSqlExecutor.executeList(this, wrapper);

    }

    /**
     * 查询 -- 单条 @SmartSelect
     * @param methodName    方法名
     * @param args          参数
     * @return T 实体
     */
    default T findOneByAnnotation(String methodName, Object[] args) {
        SmartSelect select = AnnotationCacheUtil.getSmartSelect(
                getMapperInterfaceClass(), methodName, getParameterTypes(args)
        );
        SmartSelectResolver.SelectMeta meta = SmartSelectResolver.resolve(select);
        QueryWrapper<T> wrapper = SmartWrapperBuilder.buildSelectWrapper(meta, args);
        return SmartSqlExecutor.executeOne(this, wrapper);
    }

    /**
     * 查询 -- 分页 @SmartPage
     * @param methodName    方法名
     * @param args          参数
     * @return PageResult<T>
     */
    default PageResult<T> findByPageAnnotation(String methodName, Object[] args) {
        SmartPage ann = AnnotationCacheUtil.getSmartPage(
                getMapperInterfaceClass(), methodName, getParameterTypes(args)
        );
        SmartPageResolver.PageMeta meta = SmartPageResolver.resolve(ann);
        QueryWrapper<T> wrapper = SmartWrapperBuilder.buildPageWrapper(meta, args);
        return SmartSqlExecutor.executePage(this, wrapper, meta.page, meta.pageSize);
    }

    /**
     * 新增 --（原生）
     * @param entity    类型
     * @return rows 影响行数
     */
    default int insertEntity(T entity) {
        return this.insert(entity);
    }

    /**
     * 新增 --（原生）
     * @param entity    类型
     * @return T 实体
     */
    default T insertAndReturn(T entity) {
        this.insert(entity);
        return entity;
    }

    /**
     * 新增 -- @SmartInsert
     * @param methodName    方法名
     * @param args          参数
     * @return rows 影响行数
     */
    default int insertByAnnotation(String methodName, Object[] args) {
        SmartInsert ann = AnnotationCacheUtil.getSmartInsert(
                getMapperInterfaceClass(), methodName, getParameterTypes(args)
        );

        // 获取实体类型，例如 User.class
        Class<T> entityClass = getEntityClass();
        SmartInsertResolver.InsertMeta meta = SmartInsertResolver.resolve(ann, entityClass);

        // 构建实体对象（关键：字段映射数据库 -> Java）
        T entity = buildEntityFromInsertMeta(meta, args);
        return this.insert(entity);
    }

    /**
     * 更新 --（原生）
     * @param entity    实体
     * @return rows 影响行数
     */
    default int updateByIdEntity(T entity) {
        return this.updateById(entity);
    }

    /**
     * 更新 --（原生）
     * @param entity    实体
     * @return rows 影响行数
     */
    default int updateByCondition(T entity, QueryWrapper<T> wrapper) {
        return this.update(entity, wrapper);
    }

    /**
     * 更新 -- @SmartUpdate
     * @param methodName 方法名
     * @param entity     实体
     * @param args       参数
     * @return rows 影响行数
     */
    default int updateByAnnotation(String methodName, T entity, Object[] args) {
        SmartUpdate ann = AnnotationCacheUtil.getSmartUpdate(
                getMapperInterfaceClass(), methodName, getParameterTypes(args)
        );
        SmartUpdateResolver.UpdateMeta meta = SmartUpdateResolver.resolve(ann);

        // 构建 UpdateWrapper
        UpdateWrapper<T> wrapper = SmartWrapperBuilder.buildUpdateWrapper(meta, args);
        return this.update(entity, wrapper);
    }

    /**
     * 删除 -- 原生
     * @param id    待删id
     * @return rows 影响行数
     */
    default int deleteByIdEntity(Serializable id) {
        return this.deleteById(id);
    }

    /**
     * 删除 -- 原生
     * @param wrapper
     * @return rows 影响行数
     */
    default int deleteByCondition(QueryWrapper<T> wrapper) {
        return this.delete(wrapper);
    }

    /**
     * 删除 -- @SmartDelete
     * @param methodName    方法名
     * @param args          参数
     * @return rows 影响行数
     */
    default int deleteByAnnotation(String methodName, Object[] args) {
        SmartDelete ann = AnnotationCacheUtil.getSmartDelete(
                getMapperInterfaceClass(), methodName, getParameterTypes(args)
        );
        SmartDeleteResolver.DeleteMeta meta = SmartDeleteResolver.resolve(ann);
        QueryWrapper<T> wrapper = SmartWrapperBuilder.buildDeleteWrapper(meta, args);
        return this.delete(wrapper);
    }

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

    /**
     * 私有工具方法 -- 根据 SmartInsert 的 meta 构建实体<br><br>
     *
     * 步骤：<br>
     * 1. 根据 InsertMeta.fields/values 获取值<br>
     * 2. 支持数据库字段名映射到 Java 字段名（user_name -> userName）<br>
     * 3. 使用反射设置字段值<br>
     */
    @SuppressWarnings("unchecked")
    default T buildEntityFromInsertMeta(SmartInsertResolver.InsertMeta meta, Object[] args) {
        try {
            Class<T> entityClass = (Class<T>) meta.entityClass;
            T entity = entityClass.getDeclaredConstructor().newInstance();

            for (int i = 0; i < meta.fields.length; i++) {

                String field = meta.fields[i];
                String expression = meta.values[i];

                Object value = SmartWrapperBuilder.parseValueExpression(expression, args);

                // 处理数据库字段名到Java字段名的映射
                Field javaField = findJavaField(entityClass, field);
                if (javaField != null) {
                    javaField.setAccessible(true);
                    javaField.set(entity, value);
                } else {
                    throw new NoSuchFieldException("在实体类 " + entityClass.getSimpleName() +
                            " 中找不到字段: " + field + " (尝试映射: " + mapDbFieldToJavaField(field) + ")");
                }
            }

            return entity;

        } catch (Exception e) {
            throw new RuntimeException("构建实体失败（SmartInsert）", e);
        }
    }

    /**
     * 私有工具方法 -- 解析形参类型
     */
    default Class<?>[] getParameterTypes(Object[] args) {
        if (args == null) return new Class[0];
        Class<?>[] types = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = args[i] != null ? args[i].getClass() : Object.class;
        }
        return types;
    }

    /**
     * 私有工具方法 -- 查找Java字段<br>
     * 支持数据库字段名到Java字段名的映射
     */
    default Field findJavaField(Class<?> entityClass, String fieldName) {
        try {
            // 先直接查找
            return entityClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            // 如果直接查找失败，尝试转换数据库字段名到Java字段名
            String javaFieldName = mapDbFieldToJavaField(fieldName);
            try {
                return entityClass.getDeclaredField(javaFieldName);
            } catch (NoSuchFieldException e2) {
                return null;
            }
        }
    }

    /**
     * 将数据库字段名映射为Java字段名<br>
     * 例如：user_name -> userName, create_time -> createTime
     */
    default String mapDbFieldToJavaField(String dbField) {
        if (dbField == null || dbField.isEmpty()) {
            return dbField;
        }

        StringBuilder javaField = new StringBuilder();
        boolean nextUpper = false;

        for (int i = 0; i < dbField.length(); i++) {
            char c = dbField.charAt(i);
            if (c == '_') {
                nextUpper = true;
            } else {
                if (nextUpper) {
                    javaField.append(Character.toUpperCase(c));
                    nextUpper = false;
                } else {
                    javaField.append(c);
                }
            }
        }

        return javaField.toString();
    }
}

