package io.github.forgottenlab.smartorm.executor;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.forgottenlab.smartorm.model.PageResult;
import io.github.forgottenlab.smartorm.support.mapping.SmartBeanMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 基于 MyBatis-Plus 的执行器工具类
 * @CreateDate 2025/11/25
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class SmartMapperExecutor {

    private SmartMapperExecutor() {
    }

    /**
     * 统一获取 BaseMapper（泛型在此处降级处理）
     */
    private static BaseMapper base(Object mapper) {
        return (BaseMapper) mapper;
    }

    // ================= 读操作 =================

    /**
     * 查询单条
     */
    public static Object selectOne(Object mapper, Wrapper<?> wrapper) {
        return base(mapper).selectOne(wrapper);
    }

    /**
     * 查询列表
     */
    public static List<?> selectList(Object mapper, Wrapper<?> wrapper) {
        return base(mapper).selectList(wrapper);
    }

    /**
     * 查询列表（返回 Map）
     */
    public static List<Map<String, Object>> selectMaps(Object mapper, Wrapper<?> wrapper) {
        return base(mapper).selectMaps(wrapper);
    }

    /**
     * 查询并映射为 DTO / VO 列表
     */
    public static <T> List<T> selectDtoList(
            Object mapper,
            Wrapper<?> wrapper,
            Class<T> dtoClass
    ) {
        List<Map<String, Object>> maps = selectMaps(mapper, wrapper);

        return maps.stream()
                .map(row -> SmartBeanMapper.map(row, dtoClass))
                .collect(Collectors.toList());
    }

    /**
     * 分页查询
     */
    public static PageResult<?> selectPage(
            Object mapper,
            Wrapper<?> wrapper,
            long page,
            long size
    ) {
        IPage<?> iPage = base(mapper)
                .selectPage(new Page<>(page, size), wrapper);

        return new PageResult<>(
                page,
                size,
                iPage.getTotal(),
                iPage.getRecords()
        );
    }

    // ================= 写操作 =================

    /**
     * 新增
     */
    public static int insert(Object mapper, Object entity) {
        return base(mapper).insert(entity);
    }

    /**
     * 修改
     */
    public static int update(Object mapper, Wrapper<?> wrapper) {
        return base(mapper).update(null, wrapper);
    }

    /**
     * 删除
     */
    public static int delete(Object mapper, Wrapper<?> wrapper) {
        return base(mapper).delete(wrapper);
    }
}