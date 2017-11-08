package com.sqlpal.crud;

import com.sqlpal.bean.ModelField;
import com.sqlpal.manager.ModelManager;
import com.sqlpal.manager.TableNameManager;
import com.sqlpal.util.EmptyUtlis;
import com.sqlpal.util.SqlUtils;
import com.sqlpal.util.StatementUtils;
import com.sun.istack.internal.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class DeleteHandler extends DefaultExecuteCallback<Integer> {
    private List<ModelField> primaryKeyFields = new ArrayList<>();

    int delete(DataSupport model) throws SQLException {
        return new DataHandler().execute(this, model);
    }

    void deleteAll(@NotNull List<? extends DataSupport> models) throws SQLException {
        new DataHandler().executeBatch(this, models);
    }

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
        return !EmptyUtlis.isEmpty(primaryKeyFields);
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
