package com.sqlpal.util;

import com.sqlpal.bean.ModelField;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.List;

/**
 * SQL语句工具类
 */
public class SqlUtils {

    /**
     * 创建插入语句
     * @param tableName 表名
     * @param fields 要插入的字段列表
     * @return 返回插入语句
     */
    public static String insert(@NotNull String tableName, @NotNull List<ModelField> fields) {
        if (EmptyUtlis.isEmpty(tableName) || fields.isEmpty()) return "";

        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(tableName).append("(");

        for (ModelField bean : fields) {
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
    public static String delete(@NotNull String tableName, @NotNull List<ModelField> fields) {
        if (EmptyUtlis.isEmpty(tableName) || fields.isEmpty()) return "";
        StringBuilder where = new StringBuilder();
        for (ModelField bean : fields) {
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
    public static String delete(@NotNull String tableName, @Nullable String where) {
        if (EmptyUtlis.isEmpty(tableName)) return "";
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(tableName);
        if (!EmptyUtlis.isEmpty(where)) {
            sql.append(" WHERE ").append(where);
        }
        return sql.toString();
    }

    /**
     * 创建更新语句
     * @param tableName 表名
     * @param primaryKeyFields 主键字段
     * @param updatedFields 要更新的字段
     * @return 返回更新语句
     */
    public static String update(@NotNull String tableName, @NotNull List<ModelField> primaryKeyFields, @NotNull List<ModelField> updatedFields) {
        if (EmptyUtlis.isEmpty(primaryKeyFields)) return "";

        StringBuilder where = new StringBuilder();
        for (ModelField bean : primaryKeyFields) {
            where.append(bean.getName()).append("=? and ");
        }
        where.delete(where.length() - 5, where.length());

        return update(tableName, where.toString(), updatedFields);
    }

    /**
     * 创建更新语句
     * @param tableName 表名
     * @param where 更新条件
     * @param updatedFields 要更新的字段
     * @return 返回更新语句
     */
    public static String update(@NotNull String tableName, @NotNull String where, @NotNull List<ModelField> updatedFields) {
        if (EmptyUtlis.isEmpty(tableName) || EmptyUtlis.isEmpty(where) || EmptyUtlis.isEmpty(updatedFields)) return "";

        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(tableName).append(" SET ");

        for (ModelField bean : updatedFields) {
            sql.append(bean.getName()).append("=?,");
        }
        sql.deleteCharAt(sql.length() - 1);

        sql.append(" WHERE ").append(where);

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
                              @Nullable String[] orderBy, int limit, int offset) {
        if (EmptyUtlis.isEmpty(tableName)) return "";
        StringBuilder sql = new StringBuilder();

        // select
        sql.append("SELECT ");
        if (EmptyUtlis.isEmpty(columns)) {
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
        if (!EmptyUtlis.isEmpty(conditions)) {
            sql.append(" WHERE ").append(conditions[0]);
        }

        // orderBy
        if (!EmptyUtlis.isEmpty(orderBy)) {
            sql.append(" ORDER BY ");
            for (String column : orderBy) {
                sql.append(column).append(",");
            }
            sql.deleteCharAt(sql.length() - 1);
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
