package com.sqlpal.crud;

import com.sqlpal.util.DBUtils;

import java.sql.*;

public class Cursor {
    private Statement statement;
    private ResultSet resultSet;

    public Cursor(Statement statement, ResultSet resultSet) {
        this.statement = statement;
        this.resultSet = resultSet;
    }

    public Cursor(MyStatement statement, ResultSet resultSet) {
        this.statement = statement.getStatement();
        this.resultSet = resultSet;
    }

    public Statement getStatement() {
        return statement;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public void close() {
        DBUtils.close(statement, resultSet);
    }
}
