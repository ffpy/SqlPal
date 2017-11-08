package com.sqlpal.crud;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public interface ExecuteCallback<T> {

    void onInitConnection(Connection connection) throws SQLException;

    PreparedStatement onCreateStatement(Connection connection, DataSupport model) throws SQLException;

    boolean onGetValues(DataSupport model) throws SQLException;

    void onAddValues(PreparedStatement statement) throws SQLException;

    T onExecute(PreparedStatement statement) throws SQLException;

    void onExecuteBatch(PreparedStatement statement) throws SQLException;

    void onClose(Connection connection, Statement statement, boolean isRequestConnection) throws SQLException;
}
