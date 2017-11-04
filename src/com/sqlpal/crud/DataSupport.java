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
    public String getTableName() throws DataSupportException {
        return TableNameManager.getTableName(getClass());
    }

    /**
     * 保存
     */
    public void save() throws DataSupportException {
        SaveHandler.save(this);
    }

    /**
     * 删除
     */
    public int delete() throws DataSupportException {
        return DeleteHandler.delete(this);
    }

    /**
     * 更新
     */
    public int update() throws DataSupportException {
        return UpdateHandler.update(this);
    }

    /**
     * 保存所有
     */
    public static void saveAll(List<? extends DataSupport> models) throws DataSupportException {
        SaveHandler.saveAll(models);
    }

    /**
     * 查询所有
     */
    public static  <T extends DataSupport> List<T> findAll(Class<? extends DataSupport> modelClass) throws DataSupportException {
        return QueryHandler.findAll(modelClass);
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
