package com.sqlpal.crud;

import com.sqlpal.MyStatement;
import com.sqlpal.bean.ContentValue;
import com.sqlpal.manager.ConnectionManager;
import com.sqlpal.manager.ModelManager;
import com.sqlpal.util.DBUtils;
import com.sqlpal.util.EmptyUtlis;
import com.sqlpal.util.SqlUtils;
import com.sun.istack.internal.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class UpdateHandler extends BaseUpdateHandler {

    int update(@NotNull DataSupport model) throws SQLException {
        return handle(model, false);
    }

    int updateAll(@NotNull DataSupport model, @NotNull String... conditions) throws SQLException {
        if (conditions.length == 0) return 0;
        List<ContentValue> fields = ModelManager.getAllFields(model);
        if (EmptyUtlis.isEmpty(fields)) return 0;

        boolean isRequestConnection = false;
        MyStatement stmt = null;
        try {
            Connection conn = ConnectionManager.getConnection();
            // 自动请求连接
            if (conn == null) {
                isRequestConnection = true;
                ConnectionManager.requestConnection();
                conn = ConnectionManager.getConnection();
            }
            String sql = SqlUtils.update(model.getTableName(), conditions[0], fields);
            stmt = new MyStatement(conn, sql);
            stmt.addValues(fields);
            if (conditions.length > 1) {
                stmt.addValues(conditions, 1);
            }
            return stmt.executeUpdate();
        } finally {
            DBUtils.close(stmt);
            // 释放自动请求的连接
            if (isRequestConnection) {
                ConnectionManager.freeConnection();
            }
        }
    }

    void updateAll(@NotNull List<? extends DataSupport> models) throws SQLException {
        handleAll(models, false);
    }

    int executeUpdate(@NotNull String[] conditions) throws SQLException {
        if (EmptyUtlis.isEmpty(conditions)) throw new RuntimeException("SQL语句不能为空");

        MyStatement stmt = null;
        try {
            Connection conn = ConnectionManager.getConnection();
            if (conn == null) {
                throw new RuntimeException("请先执行Sql.begin()以获取连接");
            }
            stmt = new MyStatement(conn, conditions[0]);
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
        return SqlUtils.update(model.getTableName(), getFields(1), getFields(0));
    }

    @Override
    protected boolean onInitFieldLists(DataSupport model, List<List<ContentValue>> fieldLists) {
        ArrayList<ContentValue> primaryKeyFields = new ArrayList<>();
        ArrayList<ContentValue> updatedFields = new ArrayList<>();
        ModelManager.getFields(model, primaryKeyFields, updatedFields);
        if (primaryKeyFields.isEmpty() || updatedFields.isEmpty()) return false;

        fieldLists.add(updatedFields);
        fieldLists.add(primaryKeyFields);
        return true;
    }
}
