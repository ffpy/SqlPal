package com.sqlpal.util;

import com.sqlpal.bean.FieldBean;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * 事务工具类
 */
public class StatementUtils {

    /**
     * 设置PreparedStatement的参数
     * @param startIndex 开始的序号
     */
    public static void setValues(PreparedStatement ps, List<FieldBean> list, int startIndex) throws SQLException {
        int index = startIndex;
        for (FieldBean bean : list) {
            Object obj = bean.getValue();
            if (obj == null) continue;

            // 支持的数据类型
            if (obj instanceof Integer) {
                ps.setInt(index, (Integer) obj);
            } else if (obj instanceof Short) {
                ps.setShort(index, (Short) obj);
            } else if (obj instanceof Long) {
                ps.setLong(index, (Long) obj);
            } else if (obj instanceof Float) {
                ps.setFloat(index, (Float) obj);
            } else if (obj instanceof Double) {
                ps.setDouble(index, (Double) obj);
            } else if (obj instanceof String) {
                ps.setString(index, (String) obj);
            } else if (obj instanceof Date) {
                java.sql.Date date = new java.sql.Date(((Date) obj).getTime());
                ps.setDate(index, date);
            }

            index++;
        }
    }

    /**
     * 设置PreparedStatement的参数
     */
    public static void setValues(PreparedStatement ps, List<FieldBean> list) throws SQLException {
        setValues(ps, list, 1);
    }
}
