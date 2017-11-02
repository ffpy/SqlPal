package com.sqlpal.util;

import com.sqlpal.bean.FieldBean;

import java.util.List;

/**
 * SQL语句工具类
 */
public class SqlSentenceUtils {

    /**
     * 创建插入语句
     * @param tableName 表名
     * @param list 要插入的字段列表
     * @return 返回插入语句
     */
    public static String insert(String tableName, List<FieldBean> list) {
        if (list.isEmpty()) return null;

        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(tableName).append("(");

        for (FieldBean bean : list) {
            sb.append(bean.getName()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);

        sb.append(") VALUES(");
        for (int i = 0; i < list.size(); i++) {
            sb.append("?,");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");

        return sb.toString();
    }

    /**
     * 创建删除语句
     * @param tableName 表名
     * @param list 要查找的字段列表，用and连接
     * @return 返回删除语句
     */
    public static String delete(String tableName, List<FieldBean> list) {
        if (list.isEmpty()) return null;
        StringBuilder where = new StringBuilder();
        for (FieldBean bean : list) {
            where.append(bean.getName()).append("=? and ");
        }
        where.delete(where.length() - 5, where.length());

        return delete(tableName, where.toString());
    }

    /**
     * 创建删除语句
     * @param tableName 表名
     * @param where where条件
     * @return 返回删除语句
     */
    public static String delete(String tableName, String where) {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ").append(tableName).append(" WHERE ").append(where);
        return sb.toString();
    }
}
