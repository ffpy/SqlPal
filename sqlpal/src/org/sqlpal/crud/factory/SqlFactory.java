package org.sqlpal.crud.factory;

import org.sqlpal.common.ModelField;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.sqlpal.manager.LoggerManager;
import org.sqlpal.util.EmptyUtils;

import java.util.List;

/**
 * SQL语句工厂类基类
 */
public class SqlFactory {

    /**
     * 创建插入语句
     * @param tableName 表名
     * @param fields 要插入的字段列表
     * @return 返回插入语句
     */
    public String insert(@NotNull String tableName, @NotNull List<ModelField> fields) {
        if (EmptyUtils.isEmpty(tableName) || fields.isEmpty()) return "";

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

        LoggerManager.debug(sql);
        return sql.toString();
    }

    /**
     * 创建删除语句
     * @param tableName 表名
     * @param fields 要查找的字段列表，用and连接
     * @return 返回删除语句
     */
    public String delete(@NotNull String tableName, @NotNull List<ModelField> fields) {
        if (EmptyUtils.isEmpty(tableName) || fields.isEmpty()) return "";
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
    public String delete(@NotNull String tableName, @Nullable String where) {
        if (EmptyUtils.isEmpty(tableName)) return "";
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(tableName);
        if (!EmptyUtils.isEmpty(where)) {
            sql.append(" WHERE ").append(where);
        }

        LoggerManager.debug(sql);
        return sql.toString();
    }

    /**
     * 创建更新语句
     * @param tableName 表名
     * @param primaryKeyFields 主键字段
     * @param updatedFields 要更新的字段
     * @return 返回更新语句
     */
    public String update(@NotNull String tableName, @NotNull List<ModelField> primaryKeyFields, @NotNull List<ModelField> updatedFields) {
        if (EmptyUtils.isEmpty(primaryKeyFields)) return "";

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
    public String update(@NotNull String tableName, @NotNull String where, @NotNull List<ModelField> updatedFields) {
        if (EmptyUtils.isEmpty(tableName) || EmptyUtils.isEmpty(where) || EmptyUtils.isEmpty(updatedFields)) return "";

        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(tableName).append(" SET ");

        for (ModelField bean : updatedFields) {
            sql.append(bean.getName()).append("=?,");
        }
        sql.deleteCharAt(sql.length() - 1);

        sql.append(" WHERE ").append(where);

        LoggerManager.debug(sql);
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
    public String find(@NotNull String tableName, @Nullable String[] columns, @Nullable String[] conditions,
                                @Nullable String[] orderBy, int limit, int offset) {
        if (EmptyUtils.isEmpty(tableName)) return "";
        StringBuilder sql = new StringBuilder();

        // select
        sql.append("SELECT ");
        if (EmptyUtils.isEmpty(columns)) {
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
        if (!EmptyUtils.isEmpty(conditions)) {
            sql.append(" WHERE ").append(conditions[0]);
        }

        // orderBy
        if (!EmptyUtils.isEmpty(orderBy)) {
            sql.append(" ORDER BY ");
            for (String column : orderBy) {
                sql.append(column).append(",");
            }
            sql.deleteCharAt(sql.length() - 1);
        }

        if (limit > 0) {
            // limit
            sql.append(" LIMIT ").append(limit);

            // offset
            if (offset > 0) {
                sql.append(" OFFSET ").append(offset);
            }
        }

        LoggerManager.debug(sql);
        return sql.toString();
    }
}
