package com.sqlpal.crud;

import com.sqlpal.exception.DataSupportException;
import com.sqlpal.manager.TableNameManager;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

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

    public int save() throws DataSupportException {
        return new SaveHandler().save(this);
    }

    public static void saveAll(@NotNull List<? extends DataSupport> models) throws DataSupportException {
        new SaveHandler().saveAll(models);
    }

    public int update() throws DataSupportException {
        return new UpdateHandler().update(this);
    }

    public int updateAll(@NotNull String... conditions) throws DataSupportException {
        return new UpdateHandler().updateAll(this, conditions);
    }

    public static void updateAll(@NotNull List<? extends DataSupport> models) throws DataSupportException {
        new UpdateHandler().updateAll(models);
    }

    public int delete() throws DataSupportException {
        return new DeleteHandler().delete(this);
    }

    public static void deleteAll(@NotNull List<? extends DataSupport> models) throws DataSupportException {
        new DeleteHandler().deleteAll(models);
    }

    public static int deleteAll(@NotNull Class<? extends DataSupport> modelClass, @NotNull String... conditions) throws DataSupportException {
        return new DeleteHandler().deleteAll(modelClass, conditions);
    }

    public static <T extends DataSupport> T findFirst(@NotNull Class<? extends DataSupport> modelClass) throws DataSupportException {
        return new QueryHandler().findFirst(modelClass);
    }

    public static <T extends DataSupport> T findLast(@NotNull Class<? extends DataSupport> modelClass) throws DataSupportException {
        return new QueryHandler().findLast(modelClass);
    }

    public static  <T extends DataSupport> List<T> findAll(@NotNull Class<? extends DataSupport> modelClass) throws DataSupportException {
        return new QueryHandler().findAll(modelClass);
    }

    public static ClusterQuery select(@NotNull String... columns) {
        return new ClusterQuery().select(columns);
    }

    public static ClusterQuery where(@NotNull String... conditions) {
        return new ClusterQuery().where(conditions);
    }

    public static ClusterQuery order(@NotNull String column) {
        return new ClusterQuery().order(column);
    }

    public static ClusterQuery limit(int value) {
        return new ClusterQuery().limit(value);
    }

    public static ClusterQuery offset(int value) {
        return new ClusterQuery().offset(value);
    }

    public static int count(@NotNull Class<? extends DataSupport> modelClass) throws DataSupportException {
        return new ClusterQuery().count(modelClass);
    }

    public static <T extends Number> T sum(@NotNull Class<? extends DataSupport> modelClass, @NotNull String column, Class<T> columnType) throws DataSupportException {
        return new ClusterQuery().sum(modelClass, column, columnType);
    }

    public static double average(@NotNull Class<? extends DataSupport> modelClass, @NotNull String column) throws DataSupportException {
        return new ClusterQuery().average(modelClass, column);
    }

    public static <T extends Number> T max(@NotNull Class<? extends DataSupport> modelClass, @NotNull String column, Class<T> columnType) throws DataSupportException {
        return new ClusterQuery().max(modelClass, column, columnType);
    }

    public static <T extends Number> T min(@NotNull Class<? extends DataSupport> modelClass, @NotNull String column, Class<T> columnType) throws DataSupportException {
        return new ClusterQuery().min(modelClass, column, columnType);
    }
}
