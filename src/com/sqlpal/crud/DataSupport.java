package com.sqlpal.crud;

import com.sqlpal.SqlPal;
import com.sqlpal.exception.DataSupportException;
import com.sqlpal.exception.MissPrimaryKeyException;
import com.sqlpal.exception.SqlPalException;
import com.sqlpal.bean.FieldBean;
import com.sqlpal.manager.PreparedStatementBuilder;
import com.sqlpal.manager.TableNameManager;
import com.sqlpal.manager.FieldManager;
import com.sqlpal.util.SqlSentenceUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class DataSupport {
    private String tableName;
    private static FieldManager fieldManager = new FieldManager();

    /**
     * 获取表名
     */
    public String getTableName() throws SqlPalException {
        if (tableName == null) {
            tableName = TableNameManager.getTableName(getClass());
        }
        return tableName;
    }

    private Connection getConnection() throws SqlPalException {
        Connection conn = SqlPal.getConnection();
        if (conn == null) throw new DataSupportException("获取Connection失败，请先执行begin方法！");
        return conn;
    }

    /**
     * 保存
     */
    public void save() throws SqlPalException {
        List<FieldBean> list = fieldManager.getAllFields(this);
        if (list.isEmpty()) return;

        try {
            String sql = SqlSentenceUtils.insert(getTableName(), list);
            PreparedStatement ps = new PreparedStatementBuilder(getConnection(), sql)
                    .addValues(list)
                    .build();
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new DataSupportException("操作数据库出错！", e);
        }
    }

    /**
     * 删除
     */
    public int delete() throws SqlPalException {
        List<FieldBean> list = fieldManager.getPrimaryKeyFields(this);
        if (list.isEmpty()) {
            throw new MissPrimaryKeyException();
        }

        try {
            String sql = SqlSentenceUtils.delete(getTableName(), list);
            PreparedStatement ps = new PreparedStatementBuilder(getConnection(), sql)
                    .addValues(list)
                    .build();
            int rows = ps.executeUpdate();
            ps.close();
            return rows;
        } catch (SQLException e) {
            throw new DataSupportException("操作数据库出错！", e);
        }
    }

    /**
     * 更新
     */
    public int update() throws SqlPalException {
        ArrayList<FieldBean> primaryKeyFields = new ArrayList<>();
        ArrayList<FieldBean> updatedFields = new ArrayList<>();
        fieldManager.getFields(this, primaryKeyFields, updatedFields);
        if (primaryKeyFields.isEmpty()) {
            throw new MissPrimaryKeyException();
        }
        if (updatedFields.isEmpty()) return 0;

        try {
            String sql = SqlSentenceUtils.update(getTableName(), primaryKeyFields, updatedFields);
            PreparedStatement ps = new PreparedStatementBuilder(getConnection(), sql)
                    .addValues(updatedFields)
                    .addValues(primaryKeyFields)
                    .build();
            int row = ps.executeUpdate();
            ps.close();
            return row;
        } catch (SQLException e) {
            throw new DataSupportException("操作数据库出错！", e);
        }
    }
}
