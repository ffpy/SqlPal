package com.sqlpal.crud;

import com.sqlpal.bean.ContentValue;
import com.sqlpal.manager.ConfigurationManager;
import com.sqlpal.manager.ConnectionManager;
import com.sqlpal.manager.ModelManager;
import com.sqlpal.util.DBUtils;
import com.sqlpal.util.EmptyUtlis;
import com.sun.istack.internal.NotNull;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseUpdateHandler {
    private List<List<ContentValue>> fieldLists = new ArrayList<>();
    private int insertIndex = 0;

    protected abstract String onCreateSql(DataSupport model);

    protected abstract boolean onInitFieldLists(DataSupport model, List<List<ContentValue>> fieldLists);

    protected List<ContentValue> getFields(int index) {
        return fieldLists.get(index);
    }

    private boolean initFieldLists(DataSupport model) {
        fieldLists.clear();
        return onInitFieldLists(model, fieldLists);
    }

    protected final int handle(@NotNull DataSupport model, boolean isInsert) throws SQLException {
        if (!initFieldLists(model)) return 0;

        MyStatement stmt = null;
        try {
            Connection conn = ConnectionManager.getConnection();
            stmt = new MyStatement(conn, onCreateSql(model));
            for (List<ContentValue> fields : fieldLists) {
                stmt.addValues(fields);
            }
            int res = stmt.executeUpdate();

            // 填充自增字段
            if (isInsert) {
                fillAutoIncrement(model, stmt.getStatement());
            }

            return res;
        } finally {
            DBUtils.close(stmt);
        }
    }

    protected final void handleAll(@NotNull List<? extends DataSupport> models, boolean isInsert) throws SQLException {
        if (EmptyUtlis.isEmpty(models)) return;

        MyStatement stmt = null;
        try {
            Connection conn = ConnectionManager.getConnection();
            boolean autoCommit = conn.getAutoCommit();
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

                    fillAutoIncrement(models, stmt.getStatement());
                }
            }

            if (stmt != null) {
                stmt.executeBatch();
                // 填充自增字段
                if (isInsert) {
                    fillAutoIncrement(models, stmt.getStatement());
                }
            }
            conn.setAutoCommit(autoCommit);
        } finally {
            DBUtils.close(stmt);
        }
    }

    private void fillAutoIncrement(@NotNull DataSupport model, Statement stmt) throws SQLException {
        List<DataSupport> models = new ArrayList<>();
        models.add(model);
        fillAutoIncrement(models, stmt);
    }

    /**
     * 填充自增字段
     */
    private void fillAutoIncrement(@NotNull List<? extends DataSupport> models, Statement stmt) throws SQLException {
        Class<? extends DataSupport> cls = models.get(0).getClass();
        String fieldName = ModelManager.getAutoIncrement(cls);
        if (fieldName == null) return;

        ResultSet rs = stmt.getGeneratedKeys();
        while (rs.next()) {
            int id = rs.getInt(1);
            try {
                Field field = cls.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(models.get(insertIndex++), id);
            } catch (IllegalAccessException | NoSuchFieldException ignored) {
            }
        }
    }
}
