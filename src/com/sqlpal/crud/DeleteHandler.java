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
import java.util.List;

class DeleteHandler {

    static int delete(DataSupport model) throws DataSupportException {
        List<FieldBean> fields = ModelManager.getPrimaryKeyFields(model);

        Connection conn = null;
        MyStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            String sql = SqlSentenceUtils.delete(model.getTableName(), fields);
            stmt = new MyStatement(conn, sql)
                    .addValues(fields);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataSupportException("操作数据库出错！", e);
        } finally {
            DBUtils.close(stmt);
            ConnectionManager.freeConnection(conn);
        }
    }

    static void deleteAll(@NotNull List<? extends DataSupport> models) throws DataSupportException {
        if (ListUtils.isEmpty(models)) return;

        Connection conn = null;
        MyStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);

            int batchCount = 0;
            final int maxBatchCount = ConfigurationManager.getConfiguration().getMaxBatchCount();
            for (DataSupport model : models) {
                List<FieldBean> primaryKeyFields = ModelManager.getPrimaryKeyFields(model);

                if (stmt == null) {
                    String sql = SqlSentenceUtils.delete(model.getTableName(), primaryKeyFields);
                    stmt = new MyStatement(conn, sql);
                }

                stmt.addValues(primaryKeyFields);
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
