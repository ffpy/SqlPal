package com.sqlpal.crud;

import com.sqlpal.bean.FieldBean;
import com.sqlpal.exception.DataSupportException;
import com.sqlpal.builder.PreparedStatementBuilder;
import com.sqlpal.manager.ConnectionManager;
import com.sqlpal.manager.ModelManager;
import com.sqlpal.util.DBUtils;
import com.sqlpal.util.SqlSentenceUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

class DeleteHandler {

    static int delete(DataSupport model) throws DataSupportException {
        List<FieldBean> list = ModelManager.getPrimaryKeyFields(model);

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionManager.getConnection();
            String sql = SqlSentenceUtils.delete(model.getTableName(), list);
            ps = new PreparedStatementBuilder(conn, sql)
                    .addValues(list)
                    .build();
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataSupportException("操作数据库出错！", e);
        } finally {
            DBUtils.close(ps);
            ConnectionManager.freeConnection(conn);
        }
    }
}
