package org.sqlpal.manager;

import com.sun.istack.internal.NotNull;
import org.sqlpal.connection.ConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据库连接管理器
 */
public class ConnectionManager {
    private static ConnectionFactory mConnectionFactory;
    private static final ConcurrentHashMap<Long, Connection> usedConnections = new ConcurrentHashMap<>();  // 正在使用的连接

    /**
     * 初始化
     */
    public static void init(@NotNull ConnectionFactory connectionFactory) {
        mConnectionFactory = connectionFactory;
        mConnectionFactory.init();
    }

    /**
     * 关闭所有连接
     */
    public static void destroy() throws SQLException {
        mConnectionFactory.destroy();
        mConnectionFactory = null;
    }

    /**
     * 获取当前线程的连接
     * @return 返回数据库连接
     */
    public static Connection getConnection() {
        return usedConnections.get(Thread.currentThread().getId());
    }

    /**
     * 当前线程请求连接
     */
    public static void requestConnection() throws SQLException {
        if (!usedConnections.containsKey(Thread.currentThread().getId())) {
            usedConnections.put(Thread.currentThread().getId(), mConnectionFactory.getConnection());
        }
    }

    /**
     * 释放当前线程的连接
     */
    public static void freeConnection() throws SQLException {
        Connection connection = usedConnections.remove(Thread.currentThread().getId());
        if (connection != null) connection.close();
    }
}
