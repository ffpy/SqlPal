package com.sqlpal.crud;

import com.sqlpal.exception.DataSupportException;
import com.sun.istack.internal.NotNull;

import java.util.List;

/**
 * 查询管理器
 */
public class ClusterQuery {
    private String[] columns;
    private String[] conditions;
    private String[] orderBy;
    private int limit;
    private int offset;

    ClusterQuery() {
    }

    /**
     * 设置列选择
     * @param columns 选择的列
     * @return 返回自身
     */
    public ClusterQuery select(@NotNull String... columns) {
        this.columns = columns;
        return this;
    }

    /**
     * 设置查询条件
     * @param conditions 查询条件
     * @return 返回自身
     */
    public ClusterQuery where(@NotNull String... conditions) {
        this.conditions = conditions;
        return this;
    }

    /**
     * 设置排序
     * @param columns 排序的列，例如 username 或 username asc 代表从小到大排序，username desc 代表从大到小排序
     * @return 返回自身
     */
    public ClusterQuery order(@NotNull String... columns) {
        this.orderBy = columns;
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
    public <T extends DataSupport> List<T> find(@NotNull Class<? extends DataSupport> modelClass) throws DataSupportException {
        return new QueryHandler().find(modelClass, columns, conditions, orderBy, limit, offset);
    }

    public <T extends DataSupport> T findFirst(@NotNull Class<? extends DataSupport> modelClass) throws DataSupportException {
        return new QueryHandler().findFirst(modelClass, columns, conditions);
    }

    public <T extends DataSupport> T findLast(@NotNull Class<? extends DataSupport> modelClass) throws DataSupportException {
        return new QueryHandler().findLast(modelClass, columns, conditions);
    }

    public int count(@NotNull Class<? extends DataSupport> modelClass) throws DataSupportException {
        return new QueryHandler().aggregate(modelClass, int.class, new String[]{"COUNT(*)"}, conditions, null, 0, 0);
    }

    public <T extends Number> T sum(@NotNull Class<? extends DataSupport> modelClass, @NotNull String column, Class<T> columnType) throws DataSupportException {
        column = "SUM(" + column + ")";
        return new QueryHandler().aggregate(modelClass, columnType, new String[]{column}, conditions, null, 0, 0);
    }

    public double average(@NotNull Class<? extends DataSupport> modelClass, @NotNull String column) throws DataSupportException {
        column = "AVG(" + column + ")";
        return new QueryHandler().aggregate(modelClass, double.class, new String[]{column}, conditions, null, 0, 0);
    }

    public <T extends Number> T max(@NotNull Class<? extends DataSupport> modelClass, @NotNull String column, Class<T> columnType) throws DataSupportException {
        column = "MAX(" + column + ")";
        return new QueryHandler().aggregate(modelClass, columnType, new String[]{column}, conditions, null, 0, 0);
    }

    public <T extends Number> T min(@NotNull Class<? extends DataSupport> modelClass, @NotNull String column, Class<T> columnType) throws DataSupportException {
        column = "MIN(" + column + ")";
        return new QueryHandler().aggregate(modelClass, columnType, new String[]{column}, conditions, null, 0, 0);
    }
}
