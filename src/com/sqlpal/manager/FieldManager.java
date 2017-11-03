package com.sqlpal.manager;

import com.sqlpal.annotation.PrimaryKey;
import com.sqlpal.bean.FieldBean;
import com.sqlpal.bean.SplitFields;
import com.sqlpal.exception.SqlPalException;
import com.sun.istack.internal.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 字段管理器
 */
public class FieldManager {
    private Object modelObject;

    public FieldManager(@NotNull Object modelObject) {
        this.modelObject = modelObject;
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
     * 获取字段
     */
    private List<FieldBean> getFields(Filter filter) throws SqlPalException {
        ArrayList<FieldBean> list = new ArrayList<>();
        Field[] fields = modelObject.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getModifiers() != Modifier.PRIVATE) continue;
            field.setAccessible(true);
            if (filter == null || !filter.filter(field)) {
                String name = field.getName();
                try {
                    Object obj = field.get(modelObject);
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

    /**
     * 获取所有字段
     */
    public List<FieldBean> getAllFields() throws SqlPalException {
        return getFields(null);
    }

    /**
     * 获取主键字段
     */
    public List<FieldBean> getPrimaryKeyFields() throws SqlPalException {
        return getFields(field -> field.getAnnotation(PrimaryKey.class) == null);
    }

    /**
     * 获取分开的字段
     */
    public SplitFields getSplitFields() throws SqlPalException {
        List<FieldBean> primaryKeyFields = new ArrayList<>();
        List<FieldBean> ordinaryFields;

        ordinaryFields = getFields(field -> {
            if (field.getAnnotation(PrimaryKey.class) != null) {
                try {
                    primaryKeyFields.add(new FieldBean(field.getName(), field.get(modelObject)));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return true;
            }
            return false;
        });
        return new SplitFields(primaryKeyFields, ordinaryFields);
    }

    private interface Filter {
        /**
         * 返回true会被过滤掉
         */
        boolean filter(Field field);
    }
}
