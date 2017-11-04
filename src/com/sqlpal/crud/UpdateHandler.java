package com.sqlpal.crud;

import com.sqlpal.bean.FieldBean;
import com.sqlpal.exception.DataSupportException;
import com.sqlpal.manager.ConfigurationManager;
import com.sqlpal.manager.ConnectionManager;
import com.sqlpal.manager.ModelManager;
import com.sqlpal.util.DBUtils;
import com.sqlpal.util.ListUtils;
import com.sqlpal.util.SqlSentenceUtils;
import com.sun.istack.internal.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class UpdateHandler {

    static int update(@NotNull DataSupport model) throws DataSupportException {
        ArrayList<FieldBean> primaryKeyFields = new ArrayList<>();
        ArrayList<FieldBean> updatedFields = new ArrayList<>();
        ModelManager.getFields(model, primaryKeyFields, updatedFields);
        if (updatedFields.isEmpty()) return 0;

        Connection conn = null;
        MyStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            String sql = SqlSentenceUtils.update(model.getTableName(), primaryKeyFields, updatedFields);
            stmt = new MyStatement(conn, sql)
                    .addValues(updatedFields)
                    .addValues(primaryKeyFields);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataSupportException("操作数据库出错！", e);
        } finally {
            DBUtils.close(stmt);
            ConnectionManager.freeConnection(conn);
        }
    }

    static void updateAll(@NotNull List<? extends DataSupport> models) throws DataSupportException {
        if (ListUtils.isEmpty(models)) return;

        Connection conn = null;
        MyStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);

            int batchCount = 0;
            final int maxBatchCount = ConfigurationManager.getConfiguration().getMaxBatchCount();
            for (DataSupport model : models) {
                ArrayList<FieldBean> primaryKeyFields = new ArrayList<>();
                ArrayList<FieldBean> updatedFields = new ArrayList<>();
                ModelManager.getFields(model, primaryKeyFields, updatedFields);
                if (updatedFields.isEmpty()) continue;

                if (stmt == null) {
                    String sql = SqlSentenceUtils.update(model.getTableName(), primaryKeyFields, updatedFields);
                    stmt = new MyStatement(conn, sql);
                }

                stmt.addValues(updatedFields).addValues(primaryKeyFields);
                stmt.addBatch();

                if (++batchCount % maxBatchCount == 0) {
                    stmt.executeBatch();
                }
            }

            if (stmt != null) {
                stmt.executeBatch();
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw new DataSupportException("操作数据库出错！", e);
        } finally {
            DBUtils.close(stmt);
            ConnectionManager.freeConnection(conn);
        }
    }
}
