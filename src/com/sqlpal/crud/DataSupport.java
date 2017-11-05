package com.sqlpal.crud;

import com.sqlpal.exception.DataSupportException;
import com.sqlpal.manager.TableNameManager;

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

    public static void saveAll(List<? extends DataSupport> models) throws DataSupportException {
        new SaveHandler().saveAll(models);
    }

    public int update() throws DataSupportException {
        return new UpdateHandler().update(this);
    }

    public int updateAll(String... conditions) throws DataSupportException {
        return new UpdateHandler().updateAll(this, conditions);
    }

    public static void updateAll(List<? extends DataSupport> models) throws DataSupportException {
        new UpdateHandler().updateAll(models);
    }

    public int delete() throws DataSupportException {
        return new DeleteHandler().delete(this);
    }

    public static void deleteAll(List<? extends DataSupport> models) throws DataSupportException {
        new DeleteHandler().deleteAll(models);
    }

    public static int deleteAll(Class<? extends DataSupport> modelClass, String... conditions) throws DataSupportException {
        return new DeleteHandler().deleteAll(modelClass, conditions);
    }

    public static <T extends DataSupport> T findFirst(Class<? extends DataSupport> modelClass) throws DataSupportException {
        return new QueryHandler().findFirst(modelClass);
    }

    public static <T extends DataSupport> T findLast(Class<? extends DataSupport> modelClass) throws DataSupportException {
        return new QueryHandler().findLast(modelClass);
    }

    public static  <T extends DataSupport> List<T> findAll(Class<? extends DataSupport> modelClass) throws DataSupportException {
        return new QueryHandler().findAll(modelClass);
    }

    public static ClusterQuery select(String... columns) {
        return new ClusterQuery().select(columns);
    }

    public static ClusterQuery where(String... conditions) {
        return new ClusterQuery().where(conditions);
    }

    public static ClusterQuery order(String column) {
        return new ClusterQuery().order(column);
    }

    public static ClusterQuery limit(int value) {
        return new ClusterQuery().limit(value);
    }

    public static ClusterQuery offset(int value) {
        return new ClusterQuery().offset(value);
    }
}
