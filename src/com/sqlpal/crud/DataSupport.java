package com.sqlpal.crud;

import com.sqlpal.manager.TableNameManager;
import com.sun.istack.internal.NotNull;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


/**
 * 对象关系映射类
 */
public abstract class DataSupport {

    /**
     * 获取表名
     */
    public String getTableName() {
        return TableNameManager.getTableName(getClass());
    }

    /**
     * 插入记录
     * @return 返回插入结果，成功为1，失败为0
     * @throws SQLException 数据库错误
     */
    public int save() throws SQLException {
        return new SaveHandler().save(this);
    }

    /**
     * 插入多条记录
     * 要同时插入多条记录建议用这个方法，执行速度更快
     * @param models 要插入的Model类列表
     * @throws SQLException 数据库错误
     */
    public static void saveAll(@NotNull List<? extends DataSupport> models) throws SQLException {
        new SaveHandler().saveAll(models);
    }

    /**
     * 更新记录
     * @return 返回更新结果，成功为1，失败为0
     * @throws SQLException 数据库错误
     */
    public int update() throws SQLException {
        return new UpdateHandler().update(this);
    }

    /**
     * 更新符合条件的记录
     * @param conditions 查询条件
     * @return 返回更新的行数
     * @throws SQLException 数据库错误
     */
    public int updateAll(@NotNull String... conditions) throws SQLException {
        return new UpdateHandler().updateAll(this, conditions);
    }

    /**
     * 更新多条记录
     * 要同时更新多条记录建议用这个方法，执行速度更快
     * @param models 要更新的Model类列表
     * @throws SQLException 数据库错误
     */
    public static void updateAll(@NotNull List<? extends DataSupport> models) throws SQLException {
        new UpdateHandler().updateAll(models);
    }

    /**
     * 删除记录
     * @return 返回删除结果，成功为1，失败为0
     * @throws SQLException 数据从错误
     */
    public int delete() throws SQLException {
        return new DeleteHandler().delete(this);
    }

    /**
     * 删除多条记录
     * 要同时删除多条记录建议用这个方法，执行速度更快
     * @param models 要删除的Model类列表
     * @throws SQLException 数据库错误
     */
    public static void deleteAll(@NotNull List<? extends DataSupport> models) throws SQLException {
        new DeleteHandler().deleteAll(models);
    }

    /**
     * 删除符合条件的记录
     * @param modelClass 要执行删除的Model类的Class
     * @param conditions 查询条件
     * @return 返回删除的行数
     * @throws SQLException
     */
    public static int deleteAll(@NotNull Class<? extends DataSupport> modelClass, @NotNull String... conditions) throws SQLException {
        return new DeleteHandler().deleteAll(modelClass, conditions);
    }

    /**
     * 查找第一条记录
     * @param modelClass 要查找的Model类的Class
     * @return 返回查找结果
     * @throws SQLException 数据库错误
     */
    public static <T extends DataSupport> T findFirst(@NotNull Class<? extends DataSupport> modelClass) throws SQLException {
        return new QueryHandler().findFirst(modelClass, null, null);
    }

    /**
     * 查找最后一条记录
     * @param modelClass 要查找的Model类的Class
     * @return 返回查找结果
     * @throws SQLException 数据库错误
     */
    public static <T extends DataSupport> T findLast(@NotNull Class<? extends DataSupport> modelClass) throws SQLException {
        return new QueryHandler().findLast(modelClass, null, null);
    }

    /**
     * 查询所有记录
     * @param modelClass 要查找的Model类的Class
     * @return 返回查找结果
     * @throws SQLException 数据库错误
     */
    public static  <T extends DataSupport> List<T> findAll(@NotNull Class<? extends DataSupport> modelClass) throws SQLException {
        return new QueryHandler().findAll(modelClass);
    }

    /**
     * 设置选择的列
     * @param columns 选择的列
     * @return 返回连缀查询实例
     */
    public static ClusterQuery select(@NotNull String... columns) {
        return new ClusterQuery().select(columns);
    }

    /**
     * 设置查询条件
     * @param conditions 查询条件
     * @return 返回连缀查询实例
     */
    public static ClusterQuery where(@NotNull String... conditions) {
        return new ClusterQuery().where(conditions);
    }

    /**
     * 设置排序
     * @param columns 排序的列，例如 username 或 username asc 代表从小到大排序，username desc 代表从大到小排序
     * @return 返回连缀查询实例
     */
    public static ClusterQuery order(@NotNull String... columns) {
        return new ClusterQuery().order(columns);
    }

    /**
     * 设置行数限制
     * @param value 必须大于0
     * @return 返回连缀查询实例
     */
    public static ClusterQuery limit(int value) {
        return new ClusterQuery().limit(value);
    }

    /**
     * 设置偏移量
     * @param value 必须大于0
     * @return 返回连缀查询实例
     */
    public static ClusterQuery offset(int value) {
        return new ClusterQuery().offset(value);
    }

    /**
     * 统计行数
     * @param modelClass 要统计的Model类的Class
     * @return 返回统计结果
     * @throws SQLException 数据库错误
     */
    public static int count(@NotNull Class<? extends DataSupport> modelClass) throws SQLException {
        return new ClusterQuery().count(modelClass);
    }

    /**
     * 统计某列之和
     * @param modelClass 要统计的Model类的Class
     * @param column 统计的列名
     * @param columnType 统计结果类型，可用有
     * @return 返回统计结果
     * @throws SQLException 数据库错误
     */
    public static <T extends Number> T sum(@NotNull Class<? extends DataSupport> modelClass, @NotNull String column, Class<T> columnType) throws SQLException {
        return new ClusterQuery().sum(modelClass, column, columnType);
    }

    /**
     * 统计某列的平均值
     * @param modelClass 要统计的Model类的Class
     * @param column 统计的列名
     * @return 返回统计结果
     * @throws SQLException 数据库错误
     */
    public static double average(@NotNull Class<? extends DataSupport> modelClass, @NotNull String column) throws SQLException {
        return new ClusterQuery().average(modelClass, column);
    }

    /**
     * 返回某列的最大值
     * @param modelClass 要统计的Model类的Class
     * @param column 统计的列名
     * @param columnType
     * @return 返回统计结果
     * @throws SQLException 数据库错误
     */
    public static <T extends Number> T max(@NotNull Class<? extends DataSupport> modelClass, @NotNull String column, Class<T> columnType) throws SQLException {
        return new ClusterQuery().max(modelClass, column, columnType);
    }

    /**
     * 返回某列的最小值
     * @param modelClass 要统计的Model类的Class
     * @param column 统计的列名
     * @param columnType
     * @return 返回统计结果
     * @throws SQLException 数据库错误
     */
    public static <T extends Number> T min(@NotNull Class<? extends DataSupport> modelClass, @NotNull String column, Class<T> columnType) throws SQLException {
        return new ClusterQuery().min(modelClass, column, columnType);
    }

    /**
     * 执行原生查询
     * @param condition 查询语句
     * @return 返回查询结果
     * @throws SQLException 数据库错误
     */
    public static Statement executeQuery(@NotNull String... condition) throws SQLException {
        return new DataHandler().executeQuery(condition);
    }

    /**
     * 执行原生更新
     * @param condition SQL语句
     * @return 返回更新结果
     * @throws SQLException 数据库错误
     */
    public static int executeUpdate(@NotNull String... condition) throws SQLException {
        return new DataHandler().executeUpdate(condition);
    }
}
