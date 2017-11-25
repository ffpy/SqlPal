package org.sqlpal.sample;

import org.apache.commons.dbcp2.BasicDataSource;
import org.sqlpal.config.Config;
import org.sqlpal.connection.ConnectionFactory;
import org.sqlpal.manager.ConfigManager;

import java.sql.Connection;
import java.sql.SQLException;

public class DbcpConnectionFactory implements ConnectionFactory {
    private BasicDataSource mDataSource;

    @Override
    public void init() {
        Config config = ConfigManager.getConfig();
        mDataSource = new BasicDataSource();
        mDataSource.setDriverClassName(config.getDriverName());
        mDataSource.setUsername(config.getUsername());
        mDataSource.setPassword(config.getPassword());
        mDataSource.setUrl(config.getUrl());
        mDataSource.setInitialSize(1); // 初始的连接数；
        mDataSource.setMaxTotal(10);  //最大连接数
        mDataSource.setMaxIdle(5);  // 设置最大空闲连接
        mDataSource.setMaxWaitMillis(1000);  // 设置最大等待时间
        mDataSource.setMinIdle(3);  // 设置最小空闲连接
    }

    @Override
    public Connection getConnection() throws SQLException {
        return mDataSource.getConnection();
    }

    @Override
    public void destroy() throws SQLException {
        mDataSource.close();
        mDataSource = null;
    }
}
