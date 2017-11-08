package com.sqlpal.crud;

import com.sqlpal.manager.ConfigurationManager;
import com.sqlpal.manager.ConnectionManager;
import com.sqlpal.util.DBUtils;
import com.sqlpal.util.EmptyUtlis;
import com.sqlpal.util.StatementUtils;
import com.sun.istack.internal.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DataHandler {
    private Boolean isAutoCommit = null;

    <T> T execute(@NotNull ExecuteCallback<T> callback) throws SQLException {
        return execute(callback, null);
    }

    <T> T execute(@NotNull ExecuteCallback<T> callback, DataSupport model) throws SQLException {
        if (!callback.onGetValues(model)) return null;

        boolean isRequestConnection = false;
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            // 获取连接
            conn = ConnectionManager.getConnection();
            // 自动请求连接
            if (conn == null) {
                isRequestConnection = true;
                ConnectionManager.requestConnection();
                conn = ConnectionManager.getConnection();
            }
            callback.onInitConnection(conn);

            // 创建Statement
            stmt = callback.onCreateStatement(conn, model);
            if (stmt == null) return null;
            callback.onAddValues(stmt);

            // 执行Statement
            return callback.onExecute(stmt);
        } finally {
            callback.onClose(conn, stmt, isRequestConnection);
        }
    }

    void executeBatch(@NotNull ExecuteCallback callback, @NotNull List<? extends DataSupport> models) throws SQLException {
        if (EmptyUtlis.isEmpty(models)) return;

        execute(new DefaultExecuteCallback<Void>() {
            @Override
            public void onInitConnection(Connection connection) throws SQLException {
                isAutoCommit = connection.getAutoCommit();
                connection.setAutoCommit(false);
            }

            @Override
            public PreparedStatement onCreateStatement(Connection connection, DataSupport modelIgnore) throws SQLException {
                PreparedStatement stmt = null;
                int batchCount = 0;
                final int maxBatchCount = ConfigurationManager.getConfiguration().getMaxBatchCount();
                for (DataSupport model : models) {
                    if (!callback.onGetValues(model)) continue;

                    if (stmt == null) {
                        stmt = callback.onCreateStatement(connection, model);
                        if (stmt == null) return null;
                    }

                    callback.onAddValues(stmt);
                    stmt.addBatch();

                    if (++batchCount % maxBatchCount == 0) {
                        callback.onExecuteBatch(stmt);
                    }
                }

                return stmt;
            }

            @Override
            public void onAddValues(PreparedStatement statement) throws SQLException {

            }

            @Override
            public Void onExecute(PreparedStatement statement) throws SQLException {
                callback.onExecuteBatch(statement);
                return null;
            }

            @Override
            public void onClose(Connection connection, Statement statement, boolean isRequestConnection) throws SQLException {
                if (isAutoCommit != null && connection != null) {
                    connection.setAutoCommit(isAutoCommit);
                }
                callback.onClose(connection, statement, isRequestConnection);
            }
        });
    }

    int executeUpdate(@NotNull String[] conditions) throws SQLException {
        if (EmptyUtlis.isEmpty(conditions)) throw new RuntimeException("SQL语句不能为空");

        PreparedStatement stmt = null;
        try {
            Connection conn = ConnectionManager.getConnection();
            if (conn == null) {
                throw new RuntimeException("请先执行Sql.begin()以获取连接");
            }
            stmt = conn.prepareStatement(conditions[0]);
            StatementUtils utils = new StatementUtils(stmt);
            if (conditions.length > 1) {
                utils.addValues(conditions, 1);
            }
            return stmt.executeUpdate();
        } finally {
            DBUtils.close(stmt);
        }
    }

    Statement executeQuery(@NotNull String[] conditions) throws SQLException {
        if (EmptyUtlis.isEmpty(conditions)) throw new RuntimeException("SQL语句不能为空");

        Connection conn = ConnectionManager.getConnection();
        if (conn == null) {
            throw new RuntimeException("请先执行Sql.begin()以获取连接");
        }
        PreparedStatement stmt = conn.prepareStatement(conditions[0]);
        StatementUtils utils = new StatementUtils(stmt);
        if (conditions.length > 1) {
            utils.addValues(conditions, 1);
        }
        stmt.executeQuery();
        return stmt;
    }
}
