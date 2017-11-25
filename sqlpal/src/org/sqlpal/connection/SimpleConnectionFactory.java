package org.sqlpal.connection;

import org.sqlpal.config.Config;
import org.sqlpal.exception.ConfigurationException;
import org.sqlpal.manager.ConfigManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 简单的连接工厂
 */
public class SimpleConnectionFactory implements ConnectionFactory {
    private Config mConfig;

    @Override
    public void init() {
        mConfig = ConfigManager.getConfig();
        try {
            Class.forName(mConfig.getDriverName());
        } catch (ClassNotFoundException e) {
            throw new ConfigurationException("找不到" + mConfig.getDriverName());
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(mConfig.getUrl(), mConfig.getUsername(), mConfig.getPassword());
        return ConnectionProxy.instance(connection);
    }

    @Override
    public void destroy() throws SQLException {
        mConfig = null;
    }
}
