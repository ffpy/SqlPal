package com.sqlpal.crud;

import com.sqlpal.MyStatement;
import com.sqlpal.bean.ContentValue;
import com.sqlpal.manager.ConnectionManager;
import com.sqlpal.manager.ModelManager;
import com.sqlpal.manager.TableNameManager;
import com.sqlpal.util.DBUtils;
import com.sqlpal.util.SqlUtils;
import com.sun.istack.internal.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

class DeleteHandler extends BaseUpdateHandler {

    int delete(DataSupport model) throws SQLException {
        return handle(model, false);
    }

    void deleteAll(@NotNull List<? extends DataSupport> models) throws SQLException {
        handleAll(models, false);
    }

    int deleteAll(@NotNull Class<? extends DataSupport> modelClass, String... conditions) throws SQLException {
        MyStatement stmt = null;
        try {
            Connection conn = ConnectionManager.getConnection();
            String sql = SqlUtils.delete(TableNameManager.getTableName(modelClass), conditions.length > 0 ? conditions[0] : null);
            stmt = new MyStatement(conn, sql);
            if (conditions.length > 1) {
                stmt.addValues(conditions, 1);
            }
            return stmt.executeUpdate();
        } finally {
            DBUtils.close(stmt);
        }
    }

    @Override
    protected String onCreateSql(DataSupport model) {
        return SqlUtils.delete(model.getTableName(), getFields(0));
    }

    @Override
    protected boolean onInitFieldLists(DataSupport model, List<List<ContentValue>> fieldLists) {
        List<ContentValue> primaryKeyFields = ModelManager.getPrimaryKeyFields(model);
        if (primaryKeyFields.isEmpty()) return false;
        fieldLists.add(primaryKeyFields);
        return true;
    }
}
