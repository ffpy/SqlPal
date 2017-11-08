package com.sqlpal.crud;

import com.sqlpal.manager.ModelManager;
import com.sqlpal.manager.TableNameManager;
import com.sqlpal.util.EmptyUtlis;
import com.sqlpal.util.SqlUtils;
import com.sqlpal.util.StatementUtils;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class QueryHandler {

    <T extends DataSupport> T findFirst(@NotNull Class<? extends DataSupport> modelClass, @Nullable String[] columns,
                                        @Nullable String[] conditions) throws SQLException {
        List<T> models = find(modelClass, columns, conditions, null, 1, 0);
        return models.isEmpty() ? null : models.get(0);
    }

    <T extends DataSupport> T findLast(@NotNull Class<? extends DataSupport> modelClass, @Nullable String[] columns,
                                       @Nullable String[] conditions) throws SQLException {
        ArrayList<String> primaryKeyNames = ModelManager.getPrimaryKeyNames(modelClass);
        String[] orderBy = new String[primaryKeyNames.size()];
        for (int i = 0; i < orderBy.length; i++) {
            orderBy[i] = primaryKeyNames.get(i) + " desc";
        }
        List<T> models = find(modelClass, columns, conditions, orderBy, 1, 0);
        return models.isEmpty() ? null : models.get(0);
    }

    <T extends DataSupport> List<T> findAll(@NotNull Class<? extends DataSupport> modelClass) throws SQLException {
        return find(modelClass, null, null, null, 0, 0);
    }

    <T extends DataSupport> List<T> find(@NotNull Class<? extends DataSupport> modelClass, @Nullable String[] columns,
                                                        @Nullable String[] conditions, @Nullable String[] orderBy,
                                                        int limit, int offset) throws SQLException {
        return new DataHandler().execute(new DefaultExecuteCallback<List<T>>() {
            @Override
            public PreparedStatement onCreateStatement(Connection connection, DataSupport model) throws SQLException {
                return connection.prepareStatement(SqlUtils.find(
                        TableNameManager.getTableName(modelClass), columns, conditions, orderBy, limit, offset));
            }

            @Override
            public void onAddValues(PreparedStatement statement) throws SQLException {
                if (conditions != null && conditions.length > 1) {
                    StatementUtils utils = new StatementUtils(statement);
                    utils.addValues(conditions, 1);
                }
            }

            @Override
            public List<T> onExecute(PreparedStatement statement) throws SQLException {
                List<T> models = new ArrayList<>();
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    T model = ModelManager.instance(modelClass, rs);
                    if (model != null) models.add(model);
                }
                rs.close();
                return models;
            }
        });
    }

    <T extends Number> T aggregate(@NotNull Class<? extends DataSupport> modelClass, @NotNull Class<T> columnType,
                                 @Nullable String[] columns, @Nullable String[] conditions, @Nullable String[] orderBy,
                                 int limit, int offset) throws SQLException {
        return new DataHandler().execute(new DefaultExecuteCallback<T>() {
            @Override
            public PreparedStatement onCreateStatement(Connection connection, DataSupport model) throws SQLException {
                return connection.prepareStatement(SqlUtils.find(
                        TableNameManager.getTableName(modelClass), columns, conditions, orderBy, limit, offset));
            }

            @Override
            public void onAddValues(PreparedStatement statement) throws SQLException {
                StatementUtils utils = new StatementUtils(statement);
                utils.addValues(conditions, 1);
            }

            @Override
            public T onExecute(PreparedStatement statement) throws SQLException {
                ResultSet rs = statement.executeQuery();
                T result = null;
                if (rs.next()) {
                    String s = rs.getString(1);
                    if (!EmptyUtlis.isEmpty(s)) {
                        try {
                            switch (columnType.getName()) {
                                case "short":
                                    result = (T) Short.valueOf(s); break;
                                case "int":
                                    result = (T) Integer.valueOf(s); break;
                                case "long":
                                    result = (T) Long.valueOf(s); break;
                                case "float":
                                    result = (T) Float.valueOf(s); break;
                                case "double":
                                    result = (T) Double.valueOf(s); break;
                                default:
                                    throw new RuntimeException("不支持的类型" + columnType.getName());
                            }
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }

                if (result == null) {
                    switch (columnType.getName()) {
                        case "short":
                            result = (T) Short.valueOf((short) 0); break;
                        case "int":
                            result = (T) Integer.valueOf(0); break;
                        case "long":
                            result = (T) Long.valueOf(0); break;
                        case "float":
                            result = (T) Float.valueOf(0); break;
                        case "double":
                            result = (T) Double.valueOf(0); break;
                        default:
                            throw new RuntimeException("不支持的类型" + columnType.getName());
                    }
                }

                rs.close();
                return result;
            }
        });
    }
}
