package com.sqlpal.crud;

import com.sqlpal.bean.ModelField;
import com.sqlpal.manager.ModelManager;
import com.sqlpal.util.EmptyUtlis;
import com.sqlpal.util.SqlUtils;
import com.sqlpal.util.StatementUtils;
import com.sun.istack.internal.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class UpdateHandler extends DefaultExecuteCallback<Integer> {
    private List<ModelField> primaryKeyFields = new ArrayList<>();
    private List<ModelField> updatedFields = new ArrayList<>();

    int update(@NotNull DataSupport model) throws SQLException {
        return new DataHandler().execute(this, model);
    }

    void updateAll(@NotNull List<? extends DataSupport> models) throws SQLException {
        new DataHandler().executeBatch(this, models);
    }

    int updateAll(@NotNull DataSupport model, @NotNull String... conditions) throws SQLException {
        if (EmptyUtlis.isEmpty(conditions)) return 0;
        List<ModelField> allFields = new ArrayList<>();
        ModelManager.getAllFields(model, allFields);
        if (EmptyUtlis.isEmpty(allFields)) return 0;

        return new DataHandler().execute(new DefaultExecuteCallback<Integer>() {

            @Override
            public PreparedStatement onCreateStatement(Connection connection, DataSupport model) throws SQLException {
                return connection.prepareStatement(SqlUtils.update(model.getTableName(), conditions[0], allFields));
            }

            @Override
            public void onAddValues(PreparedStatement statement) throws SQLException {
                StatementUtils utils = new StatementUtils(statement);
                utils.addValues(allFields);
                if (conditions.length > 1) {
                    utils.addValues(conditions, 1);
                }
            }

            @Override
            public Integer onExecute(PreparedStatement statement) throws SQLException {
                return statement.executeUpdate();
            }
        }, model);
    }

    @Override
    public PreparedStatement onCreateStatement(Connection connection, DataSupport model) throws SQLException {
        return connection.prepareStatement(SqlUtils.update(model.getTableName(), primaryKeyFields, updatedFields));
    }

    @Override
    public boolean onGetValues(DataSupport model) throws SQLException {
        ModelManager.getFields(model, primaryKeyFields, updatedFields);
        return !EmptyUtlis.isEmpty(primaryKeyFields) && !EmptyUtlis.isEmpty(updatedFields);
    }

    @Override
    public void onAddValues(PreparedStatement statement) throws SQLException {
        StatementUtils utils = new StatementUtils(statement);
        utils.addValues(updatedFields);
        utils.addValues(primaryKeyFields);
    }

    @Override
    public Integer onExecute(PreparedStatement statement) throws SQLException {
        return statement.executeUpdate();
    }
}
