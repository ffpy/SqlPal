package org.sqlpal;

import org.sqlpal.connection.ConnectionFactory;
import org.sqlpal.manager.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

public class SqlPal {

    /**
     * 初始化
     */
    public static void init(String configFilename, ConnectionFactory connectionFactory) {
        ConfigManager.init(configFilename);
        ConnectionManager.init(connectionFactory);
        ClassManager.init();
        TableNameManager.init();
        ModelManager.init();

        ClassManager.destroy();
    }

    /**
     * 销毁
     */
    public static void destroy() {
        try {
            ConfigManager.destroy();
            ConnectionManager.destroy();
            TableNameManager.destroy();
            ModelManager.destroy();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始事务
     */
    public static void begin() throws SQLException {
        ConnectionManager.requestConnection();
    }

    /**
     * 结束事务
     * @throws SQLException 数据库错误
     */
    public static void end() throws SQLException {
        ConnectionManager.freeConnection();
    }

    /**
     * 设置保存点
     * @return 返回保存点
     * @throws SQLException 数据库错误
     */
    public static Savepoint setSavepoint() throws SQLException {
        return ConnectionManager.getConnection().setSavepoint();
    }

    /**
     * 设置保存点
     * @param name 保存点名字
     * @return 返回保存点
     * @throws SQLException 数据库错误
     */
    public static Savepoint setSavepoint(String name) throws SQLException {
        return ConnectionManager.getConnection().setSavepoint(name);
    }

    /**
     * 回滚事务
     * @throws SQLException 数据库错误
     */
    public static void rollback() throws SQLException {
        ConnectionManager.getConnection().rollback();
    }

    /**
     * 回滚到保存点
     * @param savepoint 保存点
     * @throws SQLException 数据库错误
     */
    public static void rollback(Savepoint savepoint) throws SQLException {
        ConnectionManager.getConnection().rollback(savepoint);
    }

    /**
     * 设置是否自动体积
     * @param autoCommit 是否自动提交
     * @throws SQLException 数据库错误
     */
    public static void setAutoCommit(boolean autoCommit) throws SQLException {
        ConnectionManager.getConnection().setAutoCommit(autoCommit);
    }

    /**
     * 获取是否自动提交
     * @return 返回是否自动提交
     * @throws SQLException 数据库错误
     */
    public static boolean getAutoCommit() throws SQLException {
        return ConnectionManager.getConnection().getAutoCommit();
    }

    /**
     * 提交事务
     * @throws SQLException 数据库错误
     */
    public static void commit() throws SQLException {
        ConnectionManager.getConnection().commit();
    }

    /**
     * 获取当前连接
     * @return 返回数据库连接
     */
    public static Connection getConnection() {
        return ConnectionManager.getConnection();
    }
}
