package com.sqlpal.crud;

import com.sqlpal.manager.ConnectionManager;
import com.sqlpal.util.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 实现了一些默认的方法
 */
public abstract class DefaultExecuteCallback<T> implements ExecuteCallback<T> {

    @Override
    public void onInitConnection(Connection connection) throws SQLException {

    }

    @Override
    public boolean onGetValues(DataSupport model) throws SQLException {
        return true;
    }

    @Override
    public void onExecuteBatch(PreparedStatement statement) throws SQLException {
        statement.executeBatch();
    }

    @Override
    public void onClose(Connection connection, Statement statement, boolean isRequestConnection) throws SQLException {
        DBUtils.close(statement);
        if (isRequestConnection) {
            ConnectionManager.freeConnection();
        }
    }
}
