package com.sqlpal.crud;

import com.sqlpal.SqlPal;
import com.sqlpal.bean.SplitFields;
import com.sqlpal.exception.SqlPalException;
import com.sqlpal.bean.FieldBean;
import com.sqlpal.manager.TableNameManager;
import com.sqlpal.manager.FieldManager;
import com.sqlpal.util.SqlSentenceUtils;
import com.sqlpal.util.StatementUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public abstract class DataSupport {
    private String tableName;
    private FieldManager fieldManager = new FieldManager(this);

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
        if (conn == null) throw new SqlPalException("获取Connection失败，请先执行begin方法！");
        return conn;
    }

    /**
     * 保存
     */
    public void save() throws SqlPalException {
        List<FieldBean> list = fieldManager.getAllFields();
        if (list.isEmpty()) return;

        try {
            String sql = SqlSentenceUtils.insert(getTableName(), list);
            PreparedStatement ps = getConnection().prepareStatement(sql);
            StatementUtils.setValues(ps, list);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new SqlPalException("操作数据库出错！", e);
        }
    }

    /**
     * 删除
     */
    public int delete() throws SqlPalException {
        List<FieldBean> list = fieldManager.getPrimaryKeyFields();
        if (list.isEmpty()) {
            throw new SqlPalException("找不到主键，请用PrimaryKey注解来指定主键！");
        }

        try {
            String sql = SqlSentenceUtils.delete(getTableName(), list);
            PreparedStatement ps = getConnection().prepareStatement(sql);
            StatementUtils.setValues(ps, list);
            int rows = ps.executeUpdate();
            ps.close();
            return rows;
        } catch (SQLException e) {
            throw new SqlPalException("操作数据库出错！", e);
        }
    }

    /**
     * 更新
     */
    public int update() throws SqlPalException {
        SplitFields splitFields = fieldManager.getSplitFields();
        if (splitFields.getPrimaryKeyFields().isEmpty()) {
            throw new SqlPalException("找不到主键，请用PrimaryKey注解来指定主键！");
        }
        if (splitFields.getOrdinaryFields().isEmpty()) return 0;

        try {
            String sql = SqlSentenceUtils.update(getTableName(), splitFields.getPrimaryKeyFields(), splitFields.getOrdinaryFields());
            System.out.println(sql);
            PreparedStatement ps = getConnection().prepareStatement(sql);
            StatementUtils.setValues(ps, splitFields.getOrdinaryFields());
            StatementUtils.setValues(ps, splitFields.getPrimaryKeyFields(), splitFields.getOrdinaryFields().size() + 1);
            int row = ps.executeUpdate();
            ps.close();
            return row;
        } catch (SQLException e) {
            throw new SqlPalException("操作数据库出错！", e);
        }
    }
}
