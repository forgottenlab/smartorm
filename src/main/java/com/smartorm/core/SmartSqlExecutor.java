package com.smartorm.core;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartorm.model.PageResult;

import java.util.List;

/**
 * @author <a href="#">Forgotten.</a>
 * @Details 智能语句生成器
 * @CreateDate 2025/11/25
 * @LastModified 2025/11/25
 * @VersionHistory [版本历史]
 */
public class SmartSqlExecutor {

    /**
     * 执行数据查询：列表
     */
    public static <T> List<T> executeList(BaseMapper<T> mapper, QueryWrapper<T> wrapper) {
        return mapper.selectList(wrapper);
    }

    /**
     * 执行数据查询：单条
     */
    public static <T> T executeOne(BaseMapper<T> mapper, QueryWrapper<T> wrapper) {
        return mapper.selectOne(wrapper);
    }

    /**
     * 执行分页查询：基础<br><br>
     * 对应老代码中的 PageHelper.startPage(page, pageSize);升级为使用 MybatisPlus的分页插件
     */
    public static <T> PageResult<T> executePage(BaseMapper<T> mapper, QueryWrapper<T> wrapper, long page, long pageSize) {
        Page<T> mpPage = new Page<>(page, pageSize);
        Page<T> resultPage = mapper.selectPage(mpPage, wrapper);
        return new PageResult<>(resultPage.getTotal(), resultPage.getRecords());
    }

    // TODO：待补充批量插入、更新、删除方法等特殊条件下的语句执行

}
