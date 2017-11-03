package com.sqlpal.util;

import com.sqlpal.bean.FieldBean;
import com.sun.istack.internal.NotNull;

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
    public static String insert(@NotNull String tableName, @NotNull List<FieldBean> list) {
        if (list.isEmpty()) return null;

        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(tableName).append("(");

        for (FieldBean bean : list) {
            sql.append(bean.getName()).append(",");
        }
        sql.deleteCharAt(sql.length() - 1);

        sql.append(") VALUES(");
        for (int i = 0; i < list.size(); i++) {
            sql.append("?,");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")");

        return sql.toString();
    }

    /**
     * 创建删除语句
     * @param tableName 表名
     * @param list 要查找的字段列表，用and连接
     * @return 返回删除语句
     */
    public static String delete(@NotNull String tableName, @NotNull List<FieldBean> list) {
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
    public static String delete(@NotNull String tableName, @NotNull String where) {
        if (where.isEmpty()) return null;
        return "DELETE FROM " + tableName + " WHERE " + where;
    }

    /**
     * 创建更新语句
     * @param tableName 表名
     * @param primaryKeyFields 主键字段
     * @param updatedFields 要更新的字段
     * @return 返回更新语句
     */
    public static String update(@NotNull String tableName, @NotNull List<FieldBean> primaryKeyFields, @NotNull List<FieldBean> updatedFields) {
        if (primaryKeyFields.isEmpty() || updatedFields.isEmpty()) return null;

        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(tableName).append(" SET ");

        for (FieldBean bean : updatedFields) {
            sql.append(bean.getName()).append("=? and ");
        }
        sql.delete(sql.length() - 5, sql.length());

        sql.append(" WHERE ");
        for (FieldBean bean : primaryKeyFields) {
            sql.append(bean.getName()).append("=? and ");
        }
        sql.delete(sql.length() - 5, sql.length());

        return sql.toString();
    }
}
