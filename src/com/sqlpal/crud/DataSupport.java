package com.sqlpal.crud;

import com.sqlpal.SqlPal;
import com.sqlpal.annotation.PrimaryKey;
import com.sqlpal.exception.SqlPalException;
import com.sqlpal.bean.FieldBean;
import com.sqlpal.manager.TableNameManager;
import com.sqlpal.util.SqlSentenceUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public abstract class DataSupport {
    private String tableName;

    /**
     * 获取表名
     */
    public String getTableName() throws SqlPalException {
        if (tableName == null) {
            tableName = TableNameManager.getTableName(getClass());
        }
        return tableName;
    }

    /**
     * 检查字段类型是不是支持的类型
     */
    private boolean checkFieldType(Object obj) {
        if (    obj instanceof Integer || obj instanceof Short || obj instanceof Long ||
                obj instanceof Float || obj instanceof Double || obj instanceof String ||
                obj instanceof Date) {
            return true;
        }
        return false;
    }

    /**
     * 获取所有字段
     */
    private List<FieldBean> getAllFields() throws SqlPalException {
        ArrayList<FieldBean> list = new ArrayList<>();
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getModifiers() != Modifier.PRIVATE) continue;
            field.setAccessible(true);
            String name = field.getName();
            try {
                Object obj = field.get(this);
                if (checkFieldType(obj)) {
                    list.add(new FieldBean(name, obj));
                }
            } catch (IllegalAccessException e) {
                throw new SqlPalException("读取字段失败！", e);
            }
        }
        return list;
    }

    /**
     * 获取主键字段
     */
    private List<FieldBean> getPrimaryKeyFields() throws SqlPalException {
        ArrayList<FieldBean> list = new ArrayList<>();
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getModifiers() != Modifier.PRIVATE) continue;
            field.setAccessible(true);
            if (field.getAnnotation(PrimaryKey.class) != null) {
                String name = field.getName();
                try {
                    Object obj = field.get(this);
                    if (checkFieldType(obj)) {
                        list.add(new FieldBean(name, obj));
                    }
                } catch (IllegalAccessException e) {
                    throw new SqlPalException("读取字段失败！", e);
                }
            }
        }
        return list;
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
        List<FieldBean> list = getAllFields();
        if (list.isEmpty()) return;

        try {
            String sql = SqlSentenceUtils.insert(getTableName(), list);
            PreparedStatement ps = getConnection().prepareStatement(sql);
            int index = 1;
            for (FieldBean bean : list) {
                Object obj = bean.getValue();
                if (obj == null) continue;

                // 支持的数据类型
                if (obj instanceof Integer) {
                    ps.setInt(index, (Integer) obj);
                } else if (obj instanceof Short) {
                    ps.setShort(index, (Short) obj);
                } else if (obj instanceof Long) {
                    ps.setLong(index, (Long) obj);
                } else if (obj instanceof Float) {
                    ps.setFloat(index, (Float) obj);
                } else if (obj instanceof Double) {
                    ps.setDouble(index, (Double) obj);
                } else if (obj instanceof String) {
                    ps.setString(index, (String) obj);
                } else if (obj instanceof Date) {
                    java.sql.Date date = new java.sql.Date(((Date) obj).getTime());
                    ps.setDate(index, date);
                }

                index++;
            }

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
        List<FieldBean> list = getPrimaryKeyFields();
        if (list.isEmpty()) {
            throw new SqlPalException("找不到主键，请用PrimaryKey注解来指定主键！");
        }

        try {
            String sql = SqlSentenceUtils.delete(getTableName(), list);
            PreparedStatement ps = getConnection().prepareStatement(sql);
            int index = 1;
            for (FieldBean bean : list) {
                Object obj = bean.getValue();
                if (obj == null) continue;

                // 支持的数据类型
                if (obj instanceof Integer) {
                    ps.setInt(index, (Integer) obj);
                } else if (obj instanceof Short) {
                    ps.setShort(index, (Short) obj);
                } else if (obj instanceof Long) {
                    ps.setLong(index, (Long) obj);
                } else if (obj instanceof Float) {
                    ps.setFloat(index, (Float) obj);
                } else if (obj instanceof Double) {
                    ps.setDouble(index, (Double) obj);
                } else if (obj instanceof String) {
                    ps.setString(index, (String) obj);
                } else if (obj instanceof Date) {
                    java.sql.Date date = new java.sql.Date(((Date) obj).getTime());
                    ps.setDate(index, date);
                }

                index++;
            }

            int rows = ps.executeUpdate();
            ps.close();
            return rows;
        } catch (SQLException e) {
            throw new SqlPalException("操作数据库出错！", e);
        }
    }
}
