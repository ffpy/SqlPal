package org.sqlpal.connection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 连接工厂
 */
public interface ConnectionFactory {
    void init();

    Connection getConnection() throws SQLException;

    void destroy() throws SQLException;
}
