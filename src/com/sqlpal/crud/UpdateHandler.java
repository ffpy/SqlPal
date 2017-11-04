package com.sqlpal.crud;

import com.sqlpal.bean.FieldBean;
import com.sqlpal.exception.DataSupportException;
import com.sqlpal.manager.ConnectionManager;
import com.sqlpal.manager.ModelManager;
import com.sqlpal.util.DBUtils;
import com.sqlpal.util.SqlSentenceUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

class UpdateHandler {

    static int update(DataSupport model) throws DataSupportException {
        ArrayList<FieldBean> primaryKeyFields = new ArrayList<>();
        ArrayList<FieldBean> updatedFields = new ArrayList<>();
        ModelManager.getFields(model, primaryKeyFields, updatedFields);
        if (updatedFields.isEmpty()) return 0;

        Connection conn = null;
        MyStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            String sql = SqlSentenceUtils.update(model.getTableName(), primaryKeyFields, updatedFields);
            stmt = new MyStatement(conn, sql)
                    .addValues(updatedFields)
                    .addValues(primaryKeyFields);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataSupportException("操作数据库出错！", e);
        } finally {
            DBUtils.close(stmt);
            ConnectionManager.freeConnection(conn);
        }
    }
}
