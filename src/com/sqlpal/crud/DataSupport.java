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
     * 查询所有
     */
    public static  <T extends DataSupport> List<T> findAll(Class<? extends DataSupport> modelClass) throws DataSupportException {
        return QueryHandler.findAll(modelClass);
    }
}
