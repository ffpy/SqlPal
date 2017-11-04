package com.sqlpal.util;

import com.sqlpal.bean.FieldBean;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.List;

/**
 * SQL语句工具类
 */
public class SqlSentenceUtils {

    /**
     * 创建插入语句
     * @param tableName 表名
     * @param fields 要插入的字段列表
     * @return 返回插入语句
     */
    public static String insert(@NotNull String tableName, @NotNull List<FieldBean> fields) {
        if (StringUtils.isEmpty(tableName) || fields.isEmpty()) return "";

        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(tableName).append("(");

        for (FieldBean bean : fields) {
            sql.append(bean.getName()).append(",");
        }
        sql.deleteCharAt(sql.length() - 1);

        sql.append(") VALUES(");
        for (int i = 0; i < fields.size(); i++) {
            sql.append("?,");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")");

        return sql.toString();
    }

    /**
     * 创建删除语句
     * @param tableName 表名
     * @param fields 要查找的字段列表，用and连接
     * @return 返回删除语句
     */
    public static String delete(@NotNull String tableName, @NotNull List<FieldBean> fields) {
        if (StringUtils.isEmpty(tableName) || fields.isEmpty()) return "";
        StringBuilder where = new StringBuilder();
        for (FieldBean bean : fields) {
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
        if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(where)) return "";
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
        if (StringUtils.isEmpty(tableName) || ListUtils.isEmpty(primaryKeyFields) || ListUtils.isEmpty(updatedFields)) return "";

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

    /**
     * 创建查询语句
     * @param tableName 表名
     * @param columns 选择的列
     * @param conditions 查询条件
     * @param orderBy 排序
     * @param limit 行数限制
     * @param offset 偏移
     * @return 返回查询语句
     */
    public static String find(@NotNull String tableName, @Nullable String[] columns, @Nullable String[] conditions,
                              @Nullable String orderBy, int limit, int offset) {
        if (StringUtils.isEmpty(tableName)) return "";
        StringBuilder sql = new StringBuilder();

        // select
        sql.append("SELECT ");
        if (columns == null || columns.length == 0) {
            sql.append("*");
        } else {
            for (String column : columns) {
                sql.append(column).append(",");
            }
            sql.deleteCharAt(sql.length() - 1);
        }

        // from
        sql.append(" FROM ").append(tableName);

        // where
        if (conditions != null && conditions.length != 0) {
            sql.append(" WHERE ").append(conditions[0]);
        }

        // orderBy
        if (!StringUtils.isEmpty(orderBy)) {
            sql.append(" ORDER BY ").append(orderBy);
        }

        // limit
        if (limit > 0) {
            sql.append(" LIMIT ").append(limit);
        }

        // offset
        if (limit > 0 && offset > 0) {
            sql.append(" OFFSET ").append(offset);
        }

        return sql.toString();
    }
}
