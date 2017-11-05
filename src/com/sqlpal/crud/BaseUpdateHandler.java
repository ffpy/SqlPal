package com.sqlpal.crud;

import com.sqlpal.bean.ContentValue;
import com.sqlpal.exception.DataSupportException;
import com.sqlpal.manager.ConfigurationManager;
import com.sqlpal.manager.ConnectionManager;
import com.sqlpal.util.DBUtils;
import com.sqlpal.util.ListUtils;
import com.sun.istack.internal.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseUpdateHandler {
    private List<List<ContentValue>> fieldLists = new ArrayList<>();

    protected abstract String onCreateSql(DataSupport model) throws DataSupportException;

    protected abstract boolean onInitFieldLists(DataSupport model, List<List<ContentValue>> fieldLists) throws DataSupportException;

    protected List<ContentValue> getFields(int index) {
        return fieldLists.get(index);
    }

    private boolean initFieldLists(DataSupport model) throws DataSupportException {
        fieldLists.clear();
        return onInitFieldLists(model, fieldLists);
    }

    protected int handle(@NotNull DataSupport model) throws DataSupportException {
        if (!initFieldLists(model)) return 0;

        Connection conn = null;
        MyStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = new MyStatement(conn, onCreateSql(model));
            for (List<ContentValue> fields : fieldLists) {
                stmt.addValues(fields);
            }
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataSupportException("操作数据库出错！", e);
        } finally {
            DBUtils.close(stmt);
            ConnectionManager.freeConnection(conn);
        }
    }

    protected void handleAll(@NotNull List<? extends DataSupport> models) throws DataSupportException {
        if (ListUtils.isEmpty(models)) return;

        Connection conn = null;
        MyStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);

            int batchCount = 0;
            final int maxBatchCount = ConfigurationManager.getConfiguration().getMaxBatchCount();
            for (DataSupport model : models) {
                if (!initFieldLists(model)) continue;

                if (stmt == null) {
                    stmt = new MyStatement(conn, onCreateSql(model));
                }

                for (List<ContentValue> fields : fieldLists) {
                    stmt.addValues(fields);
                }
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
