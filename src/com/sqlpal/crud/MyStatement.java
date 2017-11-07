package com.sqlpal.crud;

import com.sqlpal.bean.ContentValue;

import java.sql.*;
import java.util.Date;
import java.util.List;

public class MyStatement {
    private int paramIndex = 1;
    private PreparedStatement statement;

    public MyStatement(Connection conn, String sql) throws SQLException {
        statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }

    public void addBatch() throws SQLException {
        paramIndex = 1;
        statement.addBatch();
    }

    public int[] executeBatch() throws SQLException {
        return statement.executeBatch();
    }

    public int executeUpdate() throws SQLException {
        return statement.executeUpdate();
    }

    public ResultSet executeQuery() throws SQLException {
        return statement.executeQuery();
    }

    /**
     * 添加字段参数
     * @param list 字段列表
     */
    public MyStatement addValues(List<ContentValue> list) throws SQLException {
        for (ContentValue bean : list) {
            Object obj = bean.getValue();
            if (obj == null) continue;

            // 支持的数据类型
            if (obj instanceof Integer) {
                statement.setInt(paramIndex, (Integer) obj);
            } else if (obj instanceof Short) {
                statement.setShort(paramIndex, (Short) obj);
            } else if (obj instanceof Long) {
                statement.setLong(paramIndex, (Long) obj);
            } else if (obj instanceof Float) {
                statement.setFloat(paramIndex, (Float) obj);
            } else if (obj instanceof Double) {
                statement.setDouble(paramIndex, (Double) obj);
            } else if (obj instanceof String) {
                statement.setString(paramIndex, (String) obj);
            } else if (obj instanceof Date) {
                java.sql.Date date = new java.sql.Date(((Date) obj).getTime());
                statement.setDate(paramIndex, date);
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
            statement.setString(paramIndex++, values[i]);
        }
        return this;
    }

    public PreparedStatement getStatement() {
        return statement;
    }
}
