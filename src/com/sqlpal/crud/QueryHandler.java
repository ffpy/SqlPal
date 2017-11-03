package com.sqlpal.crud;

import com.sqlpal.exception.DataSupportException;
import com.sqlpal.manager.ConnectionManager;
import com.sqlpal.manager.TableNameManager;
import com.sqlpal.util.SqlSentenceUtils;
import com.sun.istack.internal.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class QueryHandler {

    public static <T extends DataSupport> List<T> findAll(@NotNull Class<? extends DataSupport> modelClass) throws DataSupportException {
        String tableName = TableNameManager.getTableName(modelClass);
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.createStatement();
            String sql = SqlSentenceUtils.findAll(tableName);
            rs = stmt.executeQuery(sql);
            return null;
        } catch (SQLException e) {
            throw new DataSupportException("操作数据库出错！", e);
        } finally {
            ConnectionManager.closeStatement(stmt);
            ConnectionManager.freeConnection(conn);
        }
    }
}
