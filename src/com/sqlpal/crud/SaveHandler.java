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

class SaveHandler {

    static void save(@NotNull DataSupport model) throws DataSupportException {
        List<FieldBean> fields = ModelManager.getAllFields(model);
        if (fields.isEmpty()) return;

        Connection conn = null;
        MyStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            String sql = SqlSentenceUtils.insert(model.getTableName(), fields);
            stmt = new MyStatement(conn, sql)
                    .addValues(fields);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataSupportException("操作数据库出错！", e);
        } finally {
            DBUtils.close(stmt);
            ConnectionManager.freeConnection(conn);
        }
    }

    static void saveAll(@NotNull List<? extends DataSupport> models) throws DataSupportException {
        if (ListUtils.isEmpty(models)) return;

        Connection conn = null;
        MyStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);

            int batchCount = 0;
            final int maxBatchCount = ConfigurationManager.getConfiguration().getMaxBatchCount();
            for (DataSupport model : models) {
                List<FieldBean> fields = ModelManager.getAllFields(model);
                if (fields.isEmpty()) continue;

                if (stmt == null) {
                    String tableName = models.get(0).getTableName();
                    String sql = SqlSentenceUtils.insert(tableName, fields);
                    stmt = new MyStatement(conn, sql);
                }

                stmt.addValues(fields);
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
