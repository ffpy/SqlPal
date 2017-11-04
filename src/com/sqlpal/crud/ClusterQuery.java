package com.sqlpal.crud;

import com.sqlpal.exception.DataSupportException;

import java.util.List;

/**
 * 查询管理器
 */
public class ClusterQuery {
    private String[] columns;
    private String[] conditions;
    private String orderBy;
    private int limit;
    private int offset;

    ClusterQuery() {
    }

    /**
     * 设置列选择
     * @param columns 选择的列
     * @return 返回自身
     */
    public ClusterQuery select(String... columns) {
        this.columns = columns;
        return this;
    }

    /**
     * 设置查询条件
     * @param conditions 查询条件
     * @return 返回自身
     */
    public ClusterQuery where(String... conditions) {
        this.conditions = conditions;
        return this;
    }

    /**
     * 设置排序
     * @param column 排序的列，例如 username 或 username asc 代表从小到大排序，username desc 代表从大到小排序
     * @return 返回自身
     */
    public ClusterQuery order(String column) {
        this.orderBy = column;
        return this;
    }

    /**
     * 设置行数限制
     * @param value 必须大于0
     * @return 返回自身
     */
    public ClusterQuery limit(int value) {
        this.limit = value;
        return this;
    }

    /**
     * 设置偏移量
     * @param value 必须大于0
     * @return 返回自身
     */
    public ClusterQuery offset(int value) {
        this.offset = value;
        return this;
    }

    /**
     * 执行查找操作
     * @param modelClass Model类的Class
     * @return 返回查询结果
     */
    public <T extends DataSupport> List<T> find(Class<? extends DataSupport> modelClass) throws DataSupportException {
        return QueryHandler.find(modelClass, columns, conditions, orderBy, limit, offset);
    }
}
