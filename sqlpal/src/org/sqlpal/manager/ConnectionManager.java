package org.sqlpal.manager;

import org.sqlpal.MyConnection;
import org.sqlpal.StatementClosableConnection;
import org.sqlpal.bean.Config;
import org.sqlpal.exception.ConfigurationException;
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
    private static AllotThread mAllotThread;    // 分配线程

    /**
     * 初始化
     */
    public static void init() throws SQLException {
        Config config = ConfigurationManager.getConfig();
        // 加载驱动程序
        try {
            Class.forName(config.getDriverName());
        } catch (ClassNotFoundException e) {
            throw new ConfigurationException("找不到" + config.getDriverName());
        }

        // 创建并执行分配线程
        mAllotThread = new AllotThread();
        mAllotThread.setDaemon(true);
        mAllotThread.start();
    }

    /**
     * 关闭所有连接
     */
    public static void destroy() throws SQLException {
        // 停止分配线程
        mAllotThread.stopWork();
        mAllotThread = null;
    }

    /**
     * 获取当前线程的连接
     * @return 返回数据库连接
     */
    public static Connection getConnection() {
        return mAllotThread.getConnection(Thread.currentThread());
    }

    /**
     * 当前线程请求连接
     */
    public static void requestConnection() {
        mAllotThread.waitForConnection(Thread.currentThread());
    }

    /**
     * 释放当前线程的连接
     */
    public static void freeConnection() {
        mAllotThread.freeConnection(Thread.currentThread());
    }


    /**
     * 分配线程
     */
    private static class AllotThread extends Thread {
        private boolean working = true;                             // 运行状态
        private Thread mThread;                                     // 自身Thread实例
        private final Config config = ConfigurationManager.getConfig();
        private final ConcurrentLinkedQueue<StatementClosableConnection> freeConnections = new ConcurrentLinkedQueue<>();      // 空闲连接
        private final ConcurrentLinkedQueue<Thread> waitingThreads = new ConcurrentLinkedQueue<>();               // 等待分配连接的线程
        private final ConcurrentHashMap<Long, StatementClosableConnection> usedConnections = new ConcurrentHashMap<>();        // 正在使用的连接
        private final int initSize = config.getInitSize();          // 连接池初始化连接数
        private final int maxSize = config.getMaxSize();            // 连接池最大连接数
        private final int maxWait = config.getMaxWait();            // 等待连接分配的最长时间，毫秒
        private int curSize = 0;                                    // 连接池当前连接数

        @Override
        public void run() {
            mThread = Thread.currentThread();
            init();

            while (working) {
                if (waitingThreads.isEmpty()) {
                    LockSupport.park();
                    continue;
                }

                StatementClosableConnection conn = getFreeConnection();
                if (conn == null) {
                    LockSupport.park();
                    continue;
                }

                Thread thread = waitingThreads.poll();

                // 给线程分配连接
                usedConnections.put(thread.getId(), conn);
                LockSupport.unpark(thread);
            }

            freeResources();
        }

        /**
         * 初始化
         */
        private void init() {
            // 初始化连接池
            for (int i = 0; i < initSize; i++) {
                StatementClosableConnection conn = createConnection();
                freeConnections.offer(conn);
            }
        }

        /**
         * 获取空闲连接
         * @return 返回数据库连接
         */
        private StatementClosableConnection getFreeConnection() {
            StatementClosableConnection conn = freeConnections.poll();
            if (conn == null) conn = createConnection();
            return conn;
        }

        /**
         * 创建连接
         * @return 返回数据库连接
         */
        private StatementClosableConnection createConnection() {
            // 当前连接数不能超过最大连接数
            if (curSize >= maxSize) return null;
            try {
                Connection conn = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
                curSize++;
                return conn == null ? null : new MyConnection(conn);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * 关闭连接
         */
        private void closeConnection(@NotNull Connection conn) throws SQLException {
            curSize--;
            conn.close();
        }

        /**
         * 释放资源
         */
        private void freeResources() {
            // 释放等待线程
            for (Thread thread : waitingThreads) {
                LockSupport.unpark(thread);
            }
            waitingThreads.clear();

            // 释放正在使用的线程
            for (Connection conn : usedConnections.values()) {
                try {
                    closeConnection(conn);
                } catch (SQLException ignored) {
                }
            }
            usedConnections.clear();

            // 释放空闲线程
            for (Connection conn : freeConnections) {
                try {
                    closeConnection(conn);
                } catch (SQLException ignored) {
                }
            }
            freeConnections.clear();
        }

        /**
         * 等待连接
         */
        public void waitForConnection(@NotNull Thread thread) {
            waitingThreads.offer(thread);
            LockSupport.unpark(mThread);
            LockSupport.parkUntil(Calendar.getInstance().getTimeInMillis() + maxWait);
        }

        /**
         * 获取连接
         * @return 返回数据库连接
         */
        public Connection getConnection(Thread thread) {
            return usedConnections.get(thread.getId());
        }

        /**
         * 释放连接
         */
        public void freeConnection(Thread thread) {
            // 获取当前线程的连接
            StatementClosableConnection conn = usedConnections.get(thread.getId());
            if (conn == null) return;

            // 重置连接
            try {
                conn.closeStatements();
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {
            }

            // 移动到空闲队列
            usedConnections.remove(thread.getId());
            freeConnections.offer(conn);

            // 唤醒分配线程
            LockSupport.unpark(mThread);
        }

        /**
         * 停止
         */
        public void stopWork() {
            working = false;
            LockSupport.unpark(mThread);
        }
    }
}
