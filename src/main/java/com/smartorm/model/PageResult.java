package com.smartorm.model;

import java.util.List;

/**
 * @author <a href="wangheran55@gmail.com">Forgotten.</a>
 * @Details 简单分页实体类
 * @CreateDate 2025/11/25
 * @LastModified 2025/11/25
 * @VersionHistory [版本历史]
 */
// 为确保工具的纯净，没有引入lambok
public class PageResult<T> {

    public PageResult(Long total, List<T> rows) {
        this.total = total; // 总记录数
        this.rows = rows; // 分页数据
    }

    private Long total; // 总记录数
    private List<T> rows; // 分页数据


    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}