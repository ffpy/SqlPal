package com.sqlpal.manager;

import com.sqlpal.bean.FieldBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class PreparedStatementBuilder {
    private int index = 1;
    private PreparedStatement statement;

    public PreparedStatementBuilder(Connection conn, String sql) throws SQLException {
        statement = conn.prepareStatement(sql);
    }

    /**
     * 设置PreparedStatement的参数
     */
    public PreparedStatementBuilder addValues(List<FieldBean> list) throws SQLException {
        for (FieldBean bean : list) {
            Object obj = bean.getValue();
            if (obj == null) continue;

            // 支持的数据类型
            if (obj instanceof Integer) {
                statement.setInt(index, (Integer) obj);
            } else if (obj instanceof Short) {
                statement.setShort(index, (Short) obj);
            } else if (obj instanceof Long) {
                statement.setLong(index, (Long) obj);
            } else if (obj instanceof Float) {
                statement.setFloat(index, (Float) obj);
            } else if (obj instanceof Double) {
                statement.setDouble(index, (Double) obj);
            } else if (obj instanceof String) {
                statement.setString(index, (String) obj);
            } else if (obj instanceof Date) {
                java.sql.Date date = new java.sql.Date(((Date) obj).getTime());
                statement.setDate(index, date);
            }

            index++;
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
