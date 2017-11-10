package com.sqlpal.crud;

import com.sun.istack.internal.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * 连缀查询类
 */
public class ClusterQuery {
    private String[] columns;       // select的列
    private String[] conditions;    // where的参数
    private String[] orderBy;       // order by
    private int limit;              // limit
    private int offset;             // offset

    ClusterQuery() {
    }

    /**
     * 设置选择的列
     * @param columns 选择的列
     * @return 返回自身实例
     */
    public ClusterQuery select(@NotNull String... columns) {
        this.columns = columns;
        return this;
    }

    /**
     * 设置查询条件
     * @param conditions 查询条件
     * @return 返回自身实例
     */
    public ClusterQuery where(@NotNull String... conditions) {
        this.conditions = conditions;
        return this;
    }

    /**
     * 设置排序
     * @param columns 排序的列，例如 username 或 username asc 代表从小到大排序，username desc 代表从大到小排序
     * @return 返回自身实例
     */
    public ClusterQuery order(@NotNull String... columns) {
        this.orderBy = columns;
        return this;
    }

    /**
     * 设置行数限制
     * @param value 必须大于0
     * @return 返回自身实例
     */
    public ClusterQuery limit(int value) {
        this.limit = value;
        return this;
    }

    /**
     * 设置偏移量
     * @param value 必须大于0
     * @return 返回自身实例
     */
    public ClusterQuery offset(int value) {
        this.offset = value;
        return this;
    }

    /**
     * 查找记录
     * @param modelClass 要查找的Model类的Class
     * @return 返回查询结果
     */
    public <T extends DataSupport> List<T> find(@NotNull Class<? extends DataSupport> modelClass) throws SQLException {
        return new QueryHandler().find(modelClass, columns, conditions, orderBy, limit, offset);
    }

    /**
     * 查找第一条记录
     * @param modelClass 要查找的Model类的Class
     * @return 返回查找结果
     * @throws SQLException 数据库错误
     */
    public <T extends DataSupport> T findFirst(@NotNull Class<? extends DataSupport> modelClass) throws SQLException {
        return new QueryHandler().findFirst(modelClass, columns, conditions);
    }

    /**
     * 查找最后一条记录
     * @param modelClass 要查找的Model类的Class
     * @return 返回查找结果
     * @throws SQLException 数据库错误
     */
    public <T extends DataSupport> T findLast(@NotNull Class<? extends DataSupport> modelClass) throws SQLException {
        return new QueryHandler().findLast(modelClass, columns, conditions);
    }

    /**
     * 统计行数
     * @param modelClass 要统计的Model类的Class
     * @return 返回统计结果
     * @throws SQLException 数据库错误
     */
    public int count(@NotNull Class<? extends DataSupport> modelClass) throws SQLException {
        return new QueryHandler().aggregate(modelClass, int.class, new String[]{"COUNT(*)"}, conditions, null, 0, 0);
    }

    /**
     * 统计某列之和
     * @param modelClass 要统计的Model类的Class
     * @param column 统计的列名
     * @param columnType 统计结果类型，可用有short.class, int.class, long.class, float.class, double.class
     * @return 返回统计结果
     * @throws SQLException 数据库错误
     */
    public <T extends Number> T sum(@NotNull Class<? extends DataSupport> modelClass, @NotNull String column, Class<T> columnType) throws SQLException {
        column = "SUM(" + column + ")";
        return new QueryHandler().aggregate(modelClass, columnType, new String[]{column}, conditions, null, 0, 0);
    }

    /**
     * 统计某列的平均值
     * @param modelClass 要统计的Model类的Class
     * @param column 统计的列名
     * @return 返回统计结果
     * @throws SQLException 数据库错误
     */
    public double average(@NotNull Class<? extends DataSupport> modelClass, @NotNull String column) throws SQLException {
        column = "AVG(" + column + ")";
        return new QueryHandler().aggregate(modelClass, double.class, new String[]{column}, conditions, null, 0, 0);
    }

    /**
     * 返回某列的最大值
     * @param modelClass 要统计的Model类的Class
     * @param column 统计的列名
     * @param columnType
     * @return 返回统计结果
     * @throws SQLException 数据库错误
     */
    public <T extends Number> T max(@NotNull Class<? extends DataSupport> modelClass, @NotNull String column, Class<T> columnType) throws SQLException {
        column = "MAX(" + column + ")";
        return new QueryHandler().aggregate(modelClass, columnType, new String[]{column}, conditions, null, 0, 0);
    }

    /**
     * 返回某列的最小值
     * @param modelClass 要统计的Model类的Class
     * @param column 统计的列名
     * @param columnType
     * @return 返回统计结果
     * @throws SQLException 数据库错误
     */
    public <T extends Number> T min(@NotNull Class<? extends DataSupport> modelClass, @NotNull String column, Class<T> columnType) throws SQLException {
        column = "MIN(" + column + ")";
        return new QueryHandler().aggregate(modelClass, columnType, new String[]{column}, conditions, null, 0, 0);
    }
}
