package org.sqlpal.crud;

import org.sqlpal.manager.ModelManager;
import org.sqlpal.util.EmptyUtils;
import org.sqlpal.util.StatementUtils;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 查询处理类
 */
class QueryHandler {

    /**
     * 查找第一条记录
     * @param modelClass 要查找的Model类的Class
     * @return 返回查找结果
     * @throws SQLException 数据库错误
     */
    <T extends DataSupport> T findFirst(@NotNull Class<? extends DataSupport> modelClass, @Nullable String[] columns,
                                        @Nullable String[] conditions) throws SQLException {
        List<T> models = find(modelClass, columns, conditions, null, 1, 0);
        return models.isEmpty() ? null : models.get(0);
    }

    /**
     * 查找最后一条记录
     * @param modelClass 要查找的Model类的Class
     * @return 返回查找结果
     * @throws SQLException 数据库错误
     */
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

    /**
     * 查询所有记录
     * @param modelClass 要查找的Model类的Class
     * @return 返回查找结果
     * @throws SQLException 数据库错误
     */
    <T extends DataSupport> List<T> findAll(@NotNull Class<? extends DataSupport> modelClass) throws SQLException {
        return find(modelClass, null, null, null, 0, 0);
    }

    /**
     * 查找记录
     * @param modelClass 要查找的Model类的Class
     * @param columns select的参数
     * @param conditions where的参数
     * @param orderBy order by的参数
     * @param limit limit的参数
     * @param offset offset的参数
     * @return 返回查询结果
     * @throws SQLException 数据库错误
     */
    <T extends DataSupport> List<T> find(@NotNull final Class<? extends DataSupport> modelClass, @Nullable final String[] columns,
                                         @Nullable final String[] conditions, @Nullable final String[] orderBy,
                                         final int limit, final int offset) throws SQLException {
        List<T> list = new DataHandler().execute(new DefaultExecuteCallback<List<T>>() {
            @Override
            public PreparedStatement onCreateStatement(Connection connection, DataSupport model) throws SQLException {
                return connection.prepareStatement(SqlFactory.find(
                        ModelManager.getTableName(modelClass), columns, conditions, orderBy, limit, offset));
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
        return list == null ? Collections.<T>emptyList() : list;
    }

    /**
     * 聚合函数
     * @param modelClass 要查找的Model类的Class
     * @param columnType 结果类型，支持的类型有short.class, int.class, long.class, float.class, double.class
     * @param columns select的参数
     * @param conditions where的参数
     * @return 返回查询结果
     * @throws SQLException 数据库错误
     */
    <T extends Number> T aggregate(@NotNull final Class<? extends DataSupport> modelClass, @NotNull final Class<T> columnType,
                                   @Nullable final String[] columns, @Nullable final String[] conditions) throws SQLException {
        return new DataHandler().execute(new DefaultExecuteCallback<T>() {
            @Override
            public PreparedStatement onCreateStatement(Connection connection, DataSupport model) throws SQLException {
                return connection.prepareStatement(SqlFactory.find(
                        ModelManager.getTableName(modelClass), columns, conditions, null, 0, 0));
            }

            @Override
            public void onAddValues(PreparedStatement statement) throws SQLException {
                StatementUtils utils = new StatementUtils(statement);
                if (conditions != null && conditions.length > 1) {
                    utils.addValues(conditions, 1);
                }
            }

            @Override
            public T onExecute(PreparedStatement statement) throws SQLException {
                ResultSet rs = statement.executeQuery();
                T result = null;
                if (rs.next()) {
                    String s = rs.getString(1);
                    if (!EmptyUtils.isEmpty(s)) {
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
