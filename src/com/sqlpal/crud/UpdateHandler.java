package com.sqlpal.crud;

import com.sqlpal.bean.FieldBean;
import com.sqlpal.exception.DataSupportException;
import com.sqlpal.manager.ConnectionManager;
import com.sqlpal.manager.ModelManager;
import com.sqlpal.util.DBUtils;
import com.sqlpal.util.ListUtils;
import com.sqlpal.util.SqlUtils;
import com.sun.istack.internal.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class UpdateHandler extends BaseUpdateHandler {

    int update(@NotNull DataSupport model) throws DataSupportException {
        return handle(model);
    }

    public int updateAll(@NotNull DataSupport model, @NotNull String... conditions) throws DataSupportException {
        if (conditions.length == 0) return 0;
        List<FieldBean> fields = ModelManager.getAllFields(model);
        if (ListUtils.isEmpty(fields)) return 0;

        Connection conn = null;
        MyStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            String sql = SqlUtils.update(model.getTableName(), conditions[0], fields);
            stmt = new MyStatement(conn, sql);
            stmt.addValues(fields);
            if (conditions.length > 1) {
                stmt.addValues(conditions, 1);
            }
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataSupportException("操作数据库出错！", e);
        } finally {
            DBUtils.close(stmt);
            ConnectionManager.freeConnection(conn);
        }
    }

    void updateAll(@NotNull List<? extends DataSupport> models) throws DataSupportException {
        handleAll(models);
    }

    @Override
    protected String onCreateSql(DataSupport model) {
        return SqlUtils.update(model.getTableName(), getFields(1), getFields(0));
    }

    @Override
    protected boolean onInitFieldLists(DataSupport model, List<List<FieldBean>> fieldLists) throws DataSupportException {
        ArrayList<FieldBean> primaryKeyFields = new ArrayList<>();
        ArrayList<FieldBean> updatedFields = new ArrayList<>();
        ModelManager.getFields(model, primaryKeyFields, updatedFields);
        if (primaryKeyFields.isEmpty() || updatedFields.isEmpty()) return false;

        fieldLists.add(updatedFields);
        fieldLists.add(primaryKeyFields);
        return true;
    }
}
