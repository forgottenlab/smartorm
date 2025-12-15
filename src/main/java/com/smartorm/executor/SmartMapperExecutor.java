package com.smartorm.executor;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartorm.model.PageResult;

import java.util.List;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details 智能 wrapper构建器
 * @CreateDate 2025/11/25
 * @LastModified 2025/12/15
 * @VersionHistory
 * - v1.0.0 2025/11/25 提供泛型版本的查询方法<br>
 * - v1.0.1 2025/12/9 新增静态快捷方法与实例化方法共存<br>
 * - v1.1.0 2025/12/15 新增分页查询方法并且废弃老版本中的实例化方法<br>
 */

@SuppressWarnings({"rawtypes", "unchecked"})
public final class SmartMapperExecutor {

    private SmartMapperExecutor() {}

    /** 统一获取 BaseMapper（泛型在此处彻底降级） */
    private static BaseMapper base(Object mapper) {
        return (BaseMapper) mapper; // 可以避免后续反复的类型转换
    }

    // ================= 读操作 =================

    /** 查询单条 */
    public static Object selectOne(Object mapper, Wrapper<?> wrapper) {
        return base(mapper).selectOne(wrapper);
    }

    /** 查询列表 */
    public static List<?> selectList(Object mapper, Wrapper<?> wrapper) {
        return base(mapper).selectList(wrapper);
    }

    /** 分页查询 */
    public static PageResult<?> selectPage(
            Object mapper,
            Wrapper<?> wrapper,
            long page,
            long size
    ) {
        IPage<?> iPage = base(mapper)
                .selectPage(new Page<>(page, size), wrapper);

        return new PageResult<>(
                iPage.getTotal(),
                iPage.getRecords()
        );
    }
    // TODO：临时方法后续会进行删除，请使用上面的分页查询方法，因为用户就算不设置注解内我设置了默认值
    public static PageResult<?> selectPage(
            Object mapper,
            Wrapper<?> wrapper
    ) {
        return selectPage(mapper,wrapper,1,10);
    }

    // ================= 写操作 =================

    /** 新增 */
    public static int insert(Object mapper, Object entity) {
        return base(mapper).insert(entity);
    }

    /** 修改 */
    public static int update(Object mapper, Wrapper<?> wrapper) {
        return base(mapper).update(null, wrapper);
    }

    /** 删除 */
    public static int delete(Object mapper, Wrapper<?> wrapper) {
        return base(mapper).delete(wrapper);
    }

}

