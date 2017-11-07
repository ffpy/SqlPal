package com.sqlpal;

import com.sqlpal.exception.ConnectionException;
import com.sqlpal.manager.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

public class SqlPal {

    /**
     * 初始化
     */
    public static void init() {
        try {
            ConfigurationManager.init();
            ConnectionManager.init();
            DataSupportClassManager.init();
            TableNameManager.init();
            ModelManager.init();

            DataSupportClassManager.destroy();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 销毁
     */
    public static void destroy() {
        try {
            ConfigurationManager.destroy();
            ConnectionManager.destroy();
            TableNameManager.destroy();
            ModelManager.destroy();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void begin() throws ConnectionException {
        ConnectionManager.requestConnection();
    }

    public static void end() {
        ConnectionManager.freeConnection();
    }

    public static Savepoint setSavepoint() throws SQLException {
        return ConnectionManager.getConnection().setSavepoint();
    }

    public static Savepoint setSavepoint(String name) throws SQLException {
        return ConnectionManager.getConnection().setSavepoint(name);
    }

    public static void rollback() throws SQLException {
        ConnectionManager.getConnection().rollback();
    }

    public static void rollback(Savepoint savepoint) throws SQLException {
        ConnectionManager.getConnection().rollback(savepoint);
    }

    public static void setAutoCommit(boolean autoCommit) throws SQLException {
        ConnectionManager.getConnection().setAutoCommit(autoCommit);
    }

    public static void getAutoCommit(boolean autoCommit) throws SQLException {
        ConnectionManager.getConnection().getAutoCommit();
    }

    public static void commit() throws SQLException {
        ConnectionManager.getConnection().commit();
    }

    public static Connection getConnection() {
        return ConnectionManager.getConnection();
    }
}
