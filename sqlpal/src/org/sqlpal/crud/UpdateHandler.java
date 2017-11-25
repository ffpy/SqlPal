package org.sqlpal.crud;

import org.sqlpal.common.ModelField;
import org.sqlpal.manager.ModelManager;
import org.sqlpal.util.EmptyUtils;
import org.sqlpal.util.SqlUtils;
import org.sqlpal.util.StatementUtils;
import com.sun.istack.internal.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 更新处理类
 */
class UpdateHandler extends DefaultExecuteCallback<Integer> {
    private List<ModelField> primaryKeyFields = new ArrayList<>();
    private List<ModelField> updatedFields = new ArrayList<>();

    /**
     * 更新记录
     * @return 返回更新结果，成功为1，失败为0
     * @throws SQLException 数据库错误
     */
    int update(@NotNull DataSupport model) throws SQLException {
        Integer row = new DataHandler().execute(this, model);
        return row == null ? 0 : row;
    }

    /**
     * 更新多条记录
     * 要同时更新多条记录建议用这个方法，执行速度更快
     * @param models 要更新的Model类列表
     * @throws SQLException 数据库错误
     */
    void updateAll(@NotNull List<? extends DataSupport> models) throws SQLException {
        new DataHandler().executeBatch(this, models);
    }

    /**
     * 更新符合条件的记录
     * @param conditions 查询条件
     * @return 返回更新的行数
     * @throws SQLException 数据库错误
     */
    int updateAll(@NotNull DataSupport model, @NotNull final String... conditions) throws SQLException {
        if (EmptyUtils.isEmpty(conditions)) return 0;
        final List<ModelField> allFields = new ArrayList<>();
        ModelManager.getAllFields(model, allFields);
        if (EmptyUtils.isEmpty(allFields)) return 0;

        Integer row = new DataHandler().execute(new DefaultExecuteCallback<Integer>() {

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
        return row == null ? 0 : row;
    }

    @Override
    public PreparedStatement onCreateStatement(Connection connection, DataSupport model) throws SQLException {
        return connection.prepareStatement(SqlUtils.update(model.getTableName(), primaryKeyFields, updatedFields));
    }

    @Override
    public boolean onGetValues(DataSupport model) throws SQLException {
        ModelManager.getFields(model, primaryKeyFields, updatedFields);
        if (EmptyUtils.isEmpty(primaryKeyFields)) throw new RuntimeException("主键没有值，请设置主键值");
        return !EmptyUtils.isEmpty(updatedFields);
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
