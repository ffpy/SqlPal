package com.sqlpal.manager;

import com.sqlpal.AutoConnection;
import com.sqlpal.bean.Configuration;
import com.sqlpal.exception.ConfigurationException;
import com.sqlpal.exception.ConnectionException;
import com.sun.istack.internal.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.LockSupport;

/**
 * 数据库连接管理器
 */
public class ConnectionManager {
    private static int initSize;        // 连接池初始化连接数
    private static int maxSize;         // 连接池最大连接数
    private static int curSize;         // 连接池当前连接数
    private static int maxWait;         // 等待连接分配的最长时间，秒
    private static ConcurrentLinkedQueue<AutoConnection> freeConnections;       // 空闲连接
    private static ConcurrentLinkedQueue<Thread> waitingThreads;                // 等待分配连接的线程
    private static ConcurrentHashMap<Long, AutoConnection> usedConnections;     // 正在使用的连接
    private static final Object sizeObj = new Object();
    private static final Object waitThreadObj = new Object();

    /**
     * 初始化
     */
    public static void init() throws SQLException {
        freeConnections = new ConcurrentLinkedQueue<>();
        waitingThreads = new ConcurrentLinkedQueue<>();
        usedConnections = new ConcurrentHashMap<>();

        Configuration configuration = ConfigurationManager.getConfiguration();
        try {
            // 加载驱动程序
            Class.forName(configuration.getDriverName());

            // 初始化参数
            ConnectionManager.initSize = configuration.getInitSize();
            ConnectionManager.maxSize = configuration.getMaxSize();
            ConnectionManager.maxWait = configuration.getMaxWait();

            // 初始化连接池
            initConnectionPool();
        } catch (ClassNotFoundException e) {
            throw new ConfigurationException("找不到" + configuration.getDriverName());
        }
    }

    /**
     * 关闭所有连接
     */
    public static void destroy() throws SQLException {
        // 释放等待线程
        if (waitingThreads != null) {
            for (Thread thread : waitingThreads) {
                LockSupport.unpark(thread);
            }
            waitingThreads.clear();
            waitingThreads = null;
        }
        // 释放正在使用的线程
        if (usedConnections != null) {
            for (Connection conn : freeConnections) {
                closeConnection(conn);
            }
            usedConnections.clear();
            usedConnections = null;
        }
        // 释放空闲线程
        if (freeConnections != null) {
            for (Connection conn : freeConnections) {
                closeConnection(conn);
            }
            freeConnections.clear();
            freeConnections = null;
        }
    }

    /**
     * 获取当前线程的连接
     */
    public static Connection getConnection() {
        return usedConnections.get(Thread.currentThread().getId());
    }

    /**
     * 为当前线程请求连接
     */
    public static void requestConnection() throws ConnectionException {
        System.out.println(Thread.currentThread().getName() + " request connection");
        // 当前线程是否持有可用连接
        AutoConnection conn = usedConnections.get(Thread.currentThread().getId());
        if (conn == null) {
            // 获取空闲连接
            try {
                conn = getFreeConnection();
            } catch (SQLException e) {
                throw new ConnectionException("请求连接出错", e);
            }
            if (conn == null) {
                // 进入等待队列
                addWaitThread(Thread.currentThread());
//                LockSupport.parkUntil(Calendar.getInstance().getTimeInMillis() + maxWait);
                LockSupport.park();
                // 再次尝试获取连接
                try {
                    conn = getFreeConnection();
                } catch (SQLException e) {
                    throw new ConnectionException("请求连接出错", e);
                }
                if (conn == null) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new ConnectionException("请求连接失败");
                    } else {
                        throw new ConnectionException("等待连接分配超时");
                    }
                }
            }
        }

        usedConnections.put(Thread.currentThread().getId(), conn);
    }

    /**
     * 释放当前线程的连接
     */
    public static void freeConnection() {
        System.out.println(Thread.currentThread().getName() + " free connection");
        // 获取当前线程的连接
        AutoConnection conn = usedConnections.get(Thread.currentThread().getId());
        if (conn != null) {
            // 关闭事务

            // 移除并添加到空闲队列
            usedConnections.remove(Thread.currentThread().getId());
            freeConnections.offer(conn);

            // 唤醒等待连接的线程
            synchronized (waitThreadObj) {
                Thread thread = getWaitThread();
                if (thread != null) {
                    LockSupport.unpark(thread);
                }
            }
        }
    }

    /**
     * 获取空闲连接
     */
    private synchronized static AutoConnection getFreeConnection() throws SQLException {
        AutoConnection conn = freeConnections.poll();
        if (conn == null) {
            conn = createConnection();
        }
        return conn;
    }

    /**
     * 初始化连接池
     */
    private static void initConnectionPool() throws SQLException {
        for (int i = 0; i < initSize; i++) {
            AutoConnection conn = createConnection();
            freeConnections.offer(conn);
        }
    }

    /**
     * 创建连接
     */
    private static AutoConnection createConnection() throws SQLException {
        Configuration configuration = ConfigurationManager.getConfiguration();
        synchronized (sizeObj) {
            // 当前连接数不能超过最大连接数
            if (curSize >= maxSize) return null;
        }
        Connection conn =  DriverManager.getConnection(configuration.getUrl(), configuration.getUsername(), configuration.getPassword());
        synchronized (sizeObj) {
            curSize++;
        }
        System.out.println("curSize: " + curSize);
        return new com.sqlpal.manager.MyConnection(conn);
    }

    /**
     * 关闭连接
     */
    private static void closeConnection(Connection conn) throws SQLException {
        if (conn == null) return;
        synchronized (sizeObj) {
            curSize--;
        }
        conn.close();
    }

    /**
     * 获取最久的等待线程
     */
    private static Thread getWaitThread() {
        Thread thread = waitingThreads.poll();
        if (thread != null) {
            System.out.println(thread.getName() + " is ready");
        }
        return thread;
    }

    /**
     * 添加等待线程
     */
    private static void addWaitThread(@NotNull Thread thread) {
        System.out.println(thread.getName() + " is waiting...");
        waitingThreads.offer(thread);
    }
}
