package com.sqlpal;

import com.sqlpal.bean.ContentValue;

import java.sql.*;
import java.util.List;

public class MyStatement implements Statement {
    private int paramIndex = 1;
    private PreparedStatement stmt;

    public MyStatement(Connection conn, String sql) throws SQLException {
        stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }

    /**
     * 添加字段参数
     * @param values 字段列表
     */
    public MyStatement addValues(List<ContentValue> values) throws SQLException {
        for (ContentValue value : values) {
            Object obj = value.getValue();
            if (obj == null) continue;

            // 支持的数据类型
            if (obj instanceof Integer) {
                stmt.setInt(paramIndex, (Integer) obj);
            } else if (obj instanceof Short) {
                stmt.setShort(paramIndex, (Short) obj);
            } else if (obj instanceof Long) {
                stmt.setLong(paramIndex, (Long) obj);
            } else if (obj instanceof Float) {
                stmt.setFloat(paramIndex, (Float) obj);
            } else if (obj instanceof Double) {
                stmt.setDouble(paramIndex, (Double) obj);
            } else if (obj instanceof String) {
                stmt.setString(paramIndex, (String) obj);
            } else if (obj instanceof Date) {
                java.sql.Date date = new java.sql.Date(((Date) obj).getTime());
                stmt.setDate(paramIndex, date);
            }

            paramIndex++;
        }

        return this;
    }

    /**
     * 添加字符串参数
     * @param values 字符串数组
     */
    public MyStatement addValues(String[] values) throws SQLException {
        return addValues(values, 0, values.length);
    }

    /**
     * 添加字符串参数
     * @param values 字符串数组
     * @param start 从start下标开始
     */
    public MyStatement addValues(String[] values, int start) throws SQLException {
        return addValues(values, start, values.length - start);
    }

    /**
     * 添加字符串参数
     * @param values 字符串数组
     * @param start 从start下标开始
     * @param length 长度
     */
    public MyStatement addValues(String[] values, int start, int length) throws SQLException {
        int end = start + length;
        for (int i = start; i < end; i++) {
            stmt.setString(paramIndex++, values[i]);
        }
        return this;
    }

    public ResultSet executeQuery() throws SQLException {
        return stmt.executeQuery();
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        return stmt.executeQuery(sql);
    }

    public int executeUpdate() throws SQLException {
        return stmt.executeUpdate();
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return stmt.executeUpdate(sql);
    }

    @Override
    public void close() throws SQLException {
        stmt.close();
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return stmt.getMaxFieldSize();
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        stmt.setMaxFieldSize(max);
    }

    @Override
    public int getMaxRows() throws SQLException {
        return stmt.getMaxRows();
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        stmt.setMaxRows(max);
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        stmt.setEscapeProcessing(enable);
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return stmt.getQueryTimeout();
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        stmt.setQueryTimeout(seconds);
    }

    @Override
    public void cancel() throws SQLException {
        stmt.cancel();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return stmt.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        stmt.clearWarnings();
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        stmt.setCursorName(name);
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return stmt.execute(sql);
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return stmt.getResultSet();
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return stmt.getUpdateCount();
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return stmt.getMoreResults();
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        stmt.setFetchDirection(direction);
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return stmt.getFetchDirection();
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        stmt.setFetchSize(rows);
    }

    @Override
    public int getFetchSize() throws SQLException {
        return stmt.getFetchSize();
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return stmt.getResultSetConcurrency();
    }

    @Override
    public int getResultSetType() throws SQLException {
        return stmt.getResultSetType();
    }

    public void addBatch() throws SQLException {
        paramIndex = 1;
        stmt.addBatch();
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        paramIndex = 1;
        stmt.addBatch(sql);
    }

    @Override
    public void clearBatch() throws SQLException {
        stmt.clearBatch();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return stmt.executeBatch();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return stmt.getConnection();
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return stmt.getMoreResults(current);
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return stmt.getGeneratedKeys();
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return stmt.executeUpdate(sql, autoGeneratedKeys);
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return stmt.executeUpdate(sql, columnIndexes);
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return stmt.executeUpdate(sql, columnNames);
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return stmt.execute(sql, autoGeneratedKeys);
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return stmt.execute(sql, columnIndexes);
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return stmt.execute(sql, columnNames);
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return stmt.getResultSetHoldability();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return stmt.isClosed();
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        stmt.setPoolable(poolable);
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return stmt.isPoolable();
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        stmt.closeOnCompletion();
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return stmt.isCloseOnCompletion();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return stmt.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return stmt.isWrapperFor(iface);
    }
}
