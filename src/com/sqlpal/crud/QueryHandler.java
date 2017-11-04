package com.sqlpal.crud;

import com.sqlpal.builder.PreparedStatementBuilder;
import com.sqlpal.exception.DataSupportException;
import com.sqlpal.manager.ConnectionManager;
import com.sqlpal.manager.ModelManager;
import com.sqlpal.manager.TableNameManager;
import com.sqlpal.util.DBUtils;
import com.sqlpal.util.SqlSentenceUtils;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class QueryHandler {

    static <T extends DataSupport> List<T> findAll(@NotNull Class<? extends DataSupport> modelClass) throws DataSupportException {
        return find(modelClass, null, null, null, 0, 0);
    }

    static <T extends DataSupport> List <T> find(@NotNull Class<? extends DataSupport> modelClass, @Nullable String[] columns,
                                                        @Nullable String[] conditions, @Nullable String orderBy,
                                                        int limit, int offset) throws DataSupportException {
        String tableName = TableNameManager.getTableName(modelClass);
        List<T> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            String sql = SqlSentenceUtils.find(tableName, columns, conditions, orderBy, limit, offset);
            System.out.println(sql);
            PreparedStatementBuilder builder = new PreparedStatementBuilder(conn, sql);
            if (conditions != null && conditions.length > 1) {
                builder.addValues(conditions, 1);
            }
            stmt = builder.build();
            rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(ModelManager.instance(modelClass, rs));
            }
        } catch (SQLException e) {
            throw new DataSupportException("操作数据库出错！", e);
        } finally {
            DBUtils.close(stmt, rs);
            ConnectionManager.freeConnection(conn);
        }

        return list;
    }
}
