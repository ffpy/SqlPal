package com.sqlpal.crud;

import com.sqlpal.exception.DataSupportException;
import com.sqlpal.manager.ConnectionManager;
import com.sqlpal.manager.ModelManager;
import com.sqlpal.manager.TableNameManager;
import com.sqlpal.util.DBUtils;
import com.sqlpal.util.EmptyUtlis;
import com.sqlpal.util.SqlUtils;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class QueryHandler {

    <T extends DataSupport> T findFirst(@NotNull Class<? extends DataSupport> modelClass, @Nullable String[] columns,
                                        @Nullable String[] conditions) throws DataSupportException {
        List<T> models = find(modelClass, columns, conditions, null, 1, 0);
        return models.isEmpty() ? null : models.get(0);
    }

    <T extends DataSupport> T findLast(@NotNull Class<? extends DataSupport> modelClass, @Nullable String[] columns,
                                       @Nullable String[] conditions) throws DataSupportException {
        ArrayList<String> primaryKeyNames = ModelManager.getPrimaryKeyNames(modelClass);
        String[] orderBy = new String[primaryKeyNames.size()];
        for (int i = 0; i < orderBy.length; i++) {
            orderBy[i] = primaryKeyNames.get(i) + " desc";
        }
        List<T> models = find(modelClass, columns, conditions, orderBy, 1, 0);
        return models.isEmpty() ? null : models.get(0);
    }

    <T extends DataSupport> List<T> findAll(@NotNull Class<? extends DataSupport> modelClass) throws DataSupportException {
        return find(modelClass, null, null, null, 0, 0);
    }

    <T extends DataSupport> List <T> find(@NotNull Class<? extends DataSupport> modelClass, @Nullable String[] columns,
                                                        @Nullable String[] conditions, @Nullable String[] orderBy,
                                                        int limit, int offset) throws DataSupportException {
        String tableName = TableNameManager.getTableName(modelClass);
        List<T> list = new ArrayList<>();
        MyStatement stmt = null;
        ResultSet rs = null;
        try {
            Connection conn = ConnectionManager.getConnection();
            String sql = SqlUtils.find(tableName, columns, conditions, orderBy, limit, offset);
            stmt = new MyStatement(conn, sql);
            if (conditions != null && conditions.length > 1) {
                stmt.addValues(conditions, 1);
            }
            rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(ModelManager.instance(modelClass, rs));
            }
        } catch (SQLException e) {
            throw new DataSupportException("操作数据库出错！", e);
        } finally {
            DBUtils.close(stmt, rs);
        }

        return list;
    }

    <T extends Number> T aggregate(@NotNull Class<? extends DataSupport> modelClass, @NotNull Class<T> columnType,
                                 @Nullable String[] columns, @Nullable String[] conditions, @Nullable String[] orderBy,
                                 int limit, int offset) throws DataSupportException {
        String tableName = TableNameManager.getTableName(modelClass);
        MyStatement stmt = null;
        ResultSet rs = null;
        try {
            Connection conn = ConnectionManager.getConnection();
            String sql = SqlUtils.find(tableName, columns, conditions, orderBy, limit, offset);
            stmt = new MyStatement(conn, sql);
            if (conditions != null && conditions.length > 1) {
                stmt.addValues(conditions, 1);
            }
            rs = stmt.executeQuery();
            if (rs.next()) {
                String s = rs.getString(1);
                if (!EmptyUtlis.isEmpty(s)) {
                    try {
                        switch (columnType.getName()) {
                            case "short":
                                return (T) Short.valueOf(s);
                            case "int":
                                return (T) Integer.valueOf(s);
                            case "long":
                                return (T) Long.valueOf(s);
                            case "float":
                                return (T) Float.valueOf(s);
                            case "double":
                                return (T) Double.valueOf(s);
                            default:
                                throw new RuntimeException("不支持的类型" + columnType.getName());
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            switch (columnType.getName()) {
                case "short":
                    return (T) Short.valueOf((short) 0);
                case "int":
                    return (T) Integer.valueOf(0);
                case "long":
                    return (T) Long.valueOf(0);
                case "float":
                    return (T) Float.valueOf(0);
                case "double":
                    return (T) Double.valueOf(0);
                default:
                    throw new RuntimeException("不支持的类型" + columnType.getName());
            }
        } catch (SQLException e) {
            throw new DataSupportException("操作数据库出错！", e);
        } finally {
            DBUtils.close(stmt, rs);
        }
    }

    public Cursor executeQuery(@NotNull String[] conditions) throws DataSupportException {
        if (EmptyUtlis.isEmpty(conditions)) throw new RuntimeException("SQL语句不能为空");

        try {
            Connection conn = ConnectionManager.getConnection();
            MyStatement stmt = new MyStatement(conn, conditions[0]);
            if (conditions.length > 1) {
                stmt.addValues(conditions, 1);
            }
            ResultSet rs = stmt.executeQuery();
            return new Cursor(stmt, rs);
        } catch (SQLException e) {
            throw new DataSupportException("操作数据库出错！", e);
        }
    }
}
