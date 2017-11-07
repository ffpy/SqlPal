package com.sqlpal.crud;

import com.sqlpal.bean.ContentValue;
import com.sqlpal.exception.DataSupportException;
import com.sqlpal.manager.ConfigurationManager;
import com.sqlpal.manager.ConnectionManager;
import com.sqlpal.util.DBUtils;
import com.sqlpal.util.EmptyUtlis;
import com.sun.istack.internal.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
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

    protected final int handle(@NotNull DataSupport model) throws DataSupportException {
        if (!initFieldLists(model)) return 0;

        MyStatement stmt = null;
        try {
            Connection conn = ConnectionManager.getConnection();
            stmt = new MyStatement(conn, onCreateSql(model));
            for (List<ContentValue> fields : fieldLists) {
                stmt.addValues(fields);
            }
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataSupportException("操作数据库出错！", e);
        } finally {
            DBUtils.close(stmt);
        }
    }

    protected final void handleAll(@NotNull List<? extends DataSupport> models) throws DataSupportException {
        if (EmptyUtlis.isEmpty(models)) return;

        MyStatement stmt = null;
        try {
            Connection conn = ConnectionManager.getConnection();
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
        }
    }

    public Cursor executeQuery(@NotNull String[] conditions) throws DataSupportException {
        if (EmptyUtlis.isEmpty(conditions)) throw new RuntimeException("SQL语句不能为空");

        try {
            Connection conn = ConnectionManager.getConnection();
            MyStatement stmt = new MyStatement(conn, conditions[0]);
            if (conditions.length > 1) {
                stmt.addValues(conditions, 1);
            }
            ResultSet rs = stmt.executeQuery();
            return new Cursor(stmt, rs);
        } catch (SQLException e) {
            throw new DataSupportException("操作数据库出错！", e);
        }
    }

    public int executeUpdate(@NotNull String[] conditions) throws DataSupportException {
        if (EmptyUtlis.isEmpty(conditions)) throw new RuntimeException("SQL语句不能为空");

        MyStatement stmt = null;
        try {
            Connection conn = ConnectionManager.getConnection();
            stmt = new MyStatement(conn, conditions[0]);
            if (conditions.length > 1) {
                stmt.addValues(conditions, 1);
            }
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataSupportException("操作数据库出错！", e);
        } finally {
            DBUtils.close(stmt);
        }
    }
}
