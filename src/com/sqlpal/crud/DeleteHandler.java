package com.sqlpal.crud;

import com.sqlpal.bean.ModelField;
import com.sqlpal.manager.ModelManager;
import com.sqlpal.manager.TableNameManager;
import com.sqlpal.util.EmptyUtils;
import com.sqlpal.util.SqlUtils;
import com.sqlpal.util.StatementUtils;
import com.sun.istack.internal.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 删除处理类
 */
class DeleteHandler extends DefaultExecuteCallback<Integer> {
    private List<ModelField> primaryKeyFields = new ArrayList<>();

    /**
     * 删除记录
     * @return 返回删除结果，成功为1，失败为0
     * @throws SQLException 数据从错误
     */
    int delete(DataSupport model) throws SQLException {
        return new DataHandler().execute(this, model);
    }

    /**
     * 删除多条记录
     * 要同时删除多条记录建议用这个方法，执行速度更快
     * @param models 要删除的Model类列表
     * @throws SQLException 数据库错误
     */
    void deleteAll(@NotNull List<? extends DataSupport> models) throws SQLException {
        new DataHandler().executeBatch(this, models);
    }

    /**
     * 删除符合条件的记录
     * @param modelClass 要执行删除的Model类的Class
     * @param conditions 查询条件
     * @return 返回删除的行数
     * @throws SQLException
     */
    int deleteAll(@NotNull Class<? extends DataSupport> modelClass, @NotNull String... conditions) throws SQLException {
        return new DataHandler().execute(new DefaultExecuteCallback<Integer>() {
            @Override
            public PreparedStatement onCreateStatement(Connection connection, DataSupport model) throws SQLException {
                return connection.prepareStatement(SqlUtils.delete(
                        TableNameManager.getTableName(modelClass), conditions.length > 0 ? conditions[0] : null));
            }

            @Override
            public void onAddValues(PreparedStatement statement) throws SQLException {
                if (conditions.length > 1) {
                    StatementUtils utils = new StatementUtils(statement);
                    utils.addValues(conditions, 1);
                }
            }

            @Override
            public Integer onExecute(PreparedStatement statement) throws SQLException {
                return statement.executeUpdate();
            }
        });
    }

    @Override
    public PreparedStatement onCreateStatement(Connection connection, DataSupport model) throws SQLException {
        return connection.prepareStatement(SqlUtils.delete(model.getTableName(), primaryKeyFields));
    }

    @Override
    public boolean onGetValues(DataSupport model) throws SQLException {
        ModelManager.getPrimaryKeyFields(model, primaryKeyFields);
        return !EmptyUtils.isEmpty(primaryKeyFields);
    }

    @Override
    public void onAddValues(PreparedStatement statement) throws SQLException {
        StatementUtils utils = new StatementUtils(statement);
        utils.addValues(primaryKeyFields);
    }

    @Override
    public Integer onExecute(PreparedStatement statement) throws SQLException {
        return statement.executeUpdate();
    }
}
