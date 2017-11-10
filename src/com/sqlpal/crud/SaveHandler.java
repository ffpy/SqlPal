package com.sqlpal.crud;

import com.sqlpal.bean.ModelField;
import com.sqlpal.manager.ModelManager;
import com.sqlpal.util.EmptyUtils;
import com.sqlpal.util.SqlUtils;
import com.sqlpal.util.StatementUtils;
import com.sun.istack.internal.NotNull;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 插入处理类
 */
class SaveHandler extends DefaultExecuteCallback<Integer> {
    private List<ModelField> allFields = new ArrayList<>();
    private int insertIndex;                                // 填充自增字段时的计数器
    private List<? extends DataSupport> mModels;

    /**
     * 插入记录
     * @return 返回插入结果，成功为1，失败为0
     * @throws SQLException 数据库错误
     */
    int save(@NotNull DataSupport model) throws SQLException {
        List<DataSupport> models = new ArrayList<>();
        models.add(model);
        mModels = models;

        return new DataHandler().execute(this, model);
    }

    /**
     * 插入多条记录
     * 要同时插入多条记录建议用这个方法，执行速度更快
     * @param models 要插入的Model类列表
     * @throws SQLException 数据库错误
     */
    void saveAll(@NotNull List<? extends DataSupport> models) throws SQLException {
        insertIndex = 0;
        mModels = models;
        new DataHandler().executeBatch(this, models);
    }

    @Override
    public PreparedStatement onCreateStatement(Connection connection, DataSupport model) throws SQLException {
        return connection.prepareStatement(SqlUtils.insert(model.getTableName(), allFields), Statement.RETURN_GENERATED_KEYS);
    }

    @Override
    public boolean onGetValues(DataSupport model) throws SQLException {
        ModelManager.getAllFields(model, allFields);
        return !EmptyUtils.isEmpty(allFields);
    }

    @Override
    public void onAddValues(PreparedStatement statement) throws SQLException {
        StatementUtils utils = new StatementUtils(statement);
        utils.addValues(allFields);
    }

    @Override
    public Integer onExecute(PreparedStatement statement) throws SQLException {
        int n = statement.executeUpdate();
        fillAutoIncrement(statement);
        return n;
    }

    @Override
    public void onExecuteBatch(PreparedStatement statement) throws SQLException {
        super.onExecuteBatch(statement);
        fillAutoIncrement(statement);
    }

    /**
     * 填充自增字段
     */
    private void fillAutoIncrement(@NotNull Statement stmt) throws SQLException {
        Class<? extends DataSupport> cls = mModels.get(0).getClass();
        String fieldName = ModelManager.getAutoIncrement(cls);
        if (fieldName == null) return;

        ResultSet rs = stmt.getGeneratedKeys();
        while (rs.next()) {
            int id = rs.getInt(1);
            try {
                Field field = cls.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(mModels.get(insertIndex++), id);
            } catch (IllegalAccessException | NoSuchFieldException ignored) {
            }
        }
    }
}
