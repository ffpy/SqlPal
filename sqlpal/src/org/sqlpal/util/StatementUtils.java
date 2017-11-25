package org.sqlpal.util;

import org.sqlpal.common.ModelField;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class StatementUtils {
    private int paramIndex = 1;             // 参数位置
    private PreparedStatement statement;

    public StatementUtils(PreparedStatement statement) {
        this.statement = statement;
    }

    /**
     * 添加字段参数
     * @param values 字段列表
     * @throws SQLException 数据库错误
     */
    public void addValues(List<ModelField> values) throws SQLException {
        for (ModelField value : values) {
            Object obj = value.getValue();
            if (obj != null) {
                statement.setObject(paramIndex++, obj);
            }
        }
    }

    /**
     * 添加字符串参数
     * @param values 字符串数组
     * @throws SQLException 数据库错误
     */
    public void addValues(String[] values) throws SQLException {
        addValues(values, 0, values.length);
    }

    /**
     * 添加字符串参数
     * @param values 字符串数组
     * @param start 从start下标开始
     * @throws SQLException 数据库错误
     */
    public void addValues(String[] values, int start) throws SQLException {
        addValues(values, start, values.length - start);
    }

    /**
     * 添加字符串参数
     * @param values 字符串数组
     * @param start 从start下标开始
     * @param length 长度
     * @throws SQLException 数据库错误
     */
    public void addValues(String[] values, int start, int length) throws SQLException {
        int end = start + length;
        for (int i = start; i < end; i++) {
            statement.setString(paramIndex++, values[i]);
        }
    }
}
