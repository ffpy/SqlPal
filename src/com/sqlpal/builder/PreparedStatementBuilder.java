package com.sqlpal.builder;

import com.sqlpal.bean.FieldBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class PreparedStatementBuilder {
    private int paramIndex = 1;
    private PreparedStatement statement;

    public PreparedStatementBuilder(Connection conn, String sql) throws SQLException {
        statement = conn.prepareStatement(sql);
    }

    /**
     * 添加字段参数
     * @param list 字段列表
     * @return 返回自身
     */
    public PreparedStatementBuilder addValues(List<FieldBean> list) throws SQLException {
        for (FieldBean bean : list) {
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
     * @return 返回自身
     */
    public PreparedStatementBuilder addValues(String[] values) throws SQLException {
        return addValues(values, 0, values.length);
    }

    /**
     * 添加字符串参数
     * @param values 字符串数组
     * @param start 从start下标开始
     * @return 返回自身
     */
    public PreparedStatementBuilder addValues(String[] values, int start) throws SQLException {
        return addValues(values, start, values.length - start);
    }

    /**
     * 添加字符串参数
     * @param values 字符串数组
     * @param start 从start下标开始
     * @param length 长度
     * @return 返回自身
     */
    public PreparedStatementBuilder addValues(String[] values, int start, int length) throws SQLException {
        int end = start + length;
        for (int i = start; i < end; i++) {
            statement.setString(paramIndex++, values[i]);
        }
        return this;
    }

    /**
     * 获取PrepareStatement
     */
    public PreparedStatement build() {
        return statement;
    }
}
