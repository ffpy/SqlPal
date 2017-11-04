package com.sqlpal.manager;

import com.sqlpal.bean.Configuration;
import com.sqlpal.exception.ConfigurationException;
import com.sqlpal.exception.ConnectionException;
import com.sun.istack.internal.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.concurrent.locks.LockSupport;

/**
 * 数据库连接管理器
 */
public class ConnectionManager {
    private static int initSize;        // 连接池初始化连接数
    private static int maxSize;         // 连接池最大连接数
    private static int curSize;         // 连接池当前连接数
    private static int maxWait;         // 等待连接分配的最长时间，秒
    private static LinkedList<Connection> connectionPool;       // 连接池
    private static LinkedList<Thread> waitingThreads;           // 等待分配连接的线程
    private static final Object poolLockObj = new Object();     // 连接池锁

    /**
     * 初始化
     */
    public static void init() throws ConnectionException {
        connectionPool = new LinkedList<>();
        waitingThreads = new LinkedList<>();
        Configuration configuration = ConfigurationManager.getConfiguration();
        try {
            Class.forName(configuration.getDriverName());

            ConnectionManager.initSize = configuration.getInitSize();
            ConnectionManager.maxSize = configuration.getMaxSize();
            ConnectionManager.maxWait = configuration.getMaxWait();

            initConnectionPool();
        } catch (ClassNotFoundException e) {
            throw new ConfigurationException("找不到" + configuration.getDriverName());
        }
    }

    /**
     * 关闭所有连接
     */
    public static void destroy() throws ConnectionException {
        if (connectionPool != null) {
            for (Connection conn : connectionPool) {
                closeConnection(conn);
            }
            connectionPool.clear();
            connectionPool = null;
        }
        if (waitingThreads != null) {
            for (Thread thread : waitingThreads) {
                LockSupport.unpark(thread);
            }
            waitingThreads.clear();
            waitingThreads = null;
        }
    }

    /**
     * 获取连接
     */
    public static Connection getConnection() throws ConnectionException {
        Connection conn = getConnection_1();
        if (conn != null) return conn;
        addWaitThread(Thread.currentThread());
        LockSupport.parkUntil(maxWait * 1000);
        conn = getConnection_1();
        if (conn == null) {
            if (Thread.currentThread().isInterrupted()) {
                throw new ConnectionException("获取连接失败");
            } else {
                throw new ConnectionException("等待连接分配超时");
            }
        }
        return conn;
    }

    /**
     * 获取连接
     */
    private static Connection getConnection_1() throws ConnectionException {
        synchronized (poolLockObj) {
            if (!connectionPool.isEmpty()) return connectionPool.removeFirst();
            if (curSize < maxSize) return createConnection();
        }
        return null;
    }

    /**
     * 释放连接
     */
    public static void freeConnection(Connection conn) {
        if (conn != null) {
            synchronized (poolLockObj) {
                connectionPool.addLast(conn);
            }

            Thread thread = getWaitThread();
            if (thread != null) {
                LockSupport.unpark(thread);
            }
        }
    }

    /**
     * 初始化连接池
     */
    private static void initConnectionPool() throws ConnectionException {
        for (int i = 0; i < initSize; i++) {
            Connection conn = createConnection();
            connectionPool.push(conn);
        }
    }

    /**
     * 创建连接
     */
    private static Connection createConnection() throws ConnectionException {
        Configuration configuration = ConfigurationManager.getConfiguration();
        try {
            Connection conn =  DriverManager.getConnection(configuration.getUrl(), configuration.getUsername(), configuration.getPassword());
            curSize++;
            return conn;
        } catch (SQLException e) {
            throw new ConnectionException("获取数据库连接失败！", e);
        }
    }

    /**
     * 关闭连接
     */
    private static void closeConnection(Connection conn) throws ConnectionException {
        if (conn == null) return;
        try {
            curSize--;
            conn.close();
        } catch (SQLException e) {
            throw new ConnectionException("关闭数据库连接出错！", e);
        }
    }

    /**
     * 获取最久的等待线程
     */
    private synchronized static Thread getWaitThread() {
        if (!waitingThreads.isEmpty()) {
            return waitingThreads.removeFirst();
        }
        return null;
    }

    /**
     * 添加等待线程
     */
    private synchronized static void addWaitThread(@NotNull Thread thread) {
        waitingThreads.addLast(thread);
    }
}
