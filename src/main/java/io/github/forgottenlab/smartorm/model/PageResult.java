package io.github.forgottenlab.smartorm.model;

import java.util.List;

/**
 * @author <a href="mailto:wangheran55@gmail.com">ForgottenLab</a>
 * @Details 分页结果模型
 * @CreateDate 2025/11/25
 * @LastModified 2026/04/22
 * @VersionHistory 详细请查看 CHANGELOG.md
 */
public class PageResult<T> {

    /** 当前页号（从 1 开始） */
    private long page;

    /** 每页条数 */
    private long pageSize;

    /** 总记录数 */
    private long total;

    /** 当前页数据 */
    private List<T> rows;

    public PageResult(
            long page,
            long pageSize,
            long total,
            List<T> rows
    ) {
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        this.rows = rows;
    }

    /**
     * 计算总页数
     */
    public long getTotalPages() {
        if (pageSize == 0) {
            return 0;
        }
        return (total + pageSize - 1) / pageSize;
    }

    /**
     * 是否存在下一页
     */
    public boolean hasNext() {
        return page < getTotalPages();
    }

    /**
     * 是否存在上一页
     */
    public boolean hasPrevious() {
        return page > 1;
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "page=" + page +
                ", pageSize=" + pageSize +
                ", total=" + total +
                ", rows=" + rows +
                '}';
    }
}