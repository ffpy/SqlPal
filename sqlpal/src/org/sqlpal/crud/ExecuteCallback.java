package org.sqlpal.crud;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 数据库查询回调
 */
public interface ExecuteCallback<T> {

    /**
     * 初始化连接
     */
    void onInitConnection(Connection connection) throws SQLException;

    /**
     * 创建Statement
     */
    PreparedStatement onCreateStatement(Connection connection, DataSupport model) throws SQLException;

    /**
     * 获取值
     */
    boolean onGetValues(DataSupport model) throws SQLException;

    /**
     * 添加值
     */
    void onAddValues(PreparedStatement statement) throws SQLException;

    /**
     * 执行事务
     */
    T onExecute(PreparedStatement statement) throws SQLException;

    /**
     * 执行批处理
     */
    void onExecuteBatch(PreparedStatement statement) throws SQLException;

    /**
     * 关闭连接
     */
    void onClose(Connection connection, Statement statement, boolean isRequestConnection) throws SQLException;
}
