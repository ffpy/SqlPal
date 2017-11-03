package com.sqlpal.manager;

import com.sqlpal.annotation.PrimaryKey;
import com.sqlpal.bean.FieldBean;
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
    private ArrayList<String> primaryKeyNameCache;  // 主键名缓存

    /**
     * 检查字段类型是不是支持的类型
     */
    private boolean isSupportedField(Object obj) {
        return  obj instanceof Integer || obj instanceof Short || obj instanceof Long ||
                obj instanceof Float || obj instanceof Double || obj instanceof String ||
                obj instanceof Date;
    }

    /**
     * 判断是不是主键字段
     */
    private boolean isPrimaryKeyField(Field field) {
        return field.getAnnotation(PrimaryKey.class) != null;
    }

    /**
     * 遍历字段
     */
    private void listFields(Object modelObject, @NotNull FieldListListener fieldListListener) throws SqlPalException {
        Field[] fields = modelObject.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getModifiers() != Modifier.PRIVATE) continue;
            try {
                Object obj = field.get(modelObject);
                if (isSupportedField(obj)) {
                    fieldListListener.onList(field, field.getName(), obj);
                }
            } catch (IllegalAccessException e) {
                throw new SqlPalException("读取字段失败！", e);
            }
        }
    }

    /**
     * 获取所有字段
     */
    public List<FieldBean> getAllFields(Object modelObject) throws SqlPalException {
        ArrayList<FieldBean> list = new ArrayList<>();
        listFields(modelObject, (field, name, obj) -> list.add(new FieldBean(name, obj)));
        return list;
    }

    /**
     * 获取主键字段
     */
    public List<FieldBean> getPrimaryKeyFields(Object modelObject) throws SqlPalException {
        ArrayList<FieldBean> list = new ArrayList<>();
        if (primaryKeyNameCache == null) {
            primaryKeyNameCache = new ArrayList<>();
            listFields(modelObject, (field, name, obj) -> {
                if (isPrimaryKeyField(field)) {
                    list.add(new FieldBean(field.getName(), obj));
                    primaryKeyNameCache.add(field.getName());
                }
            });
        } else {
            Class<?> cls = modelObject.getClass();
            for (String name : primaryKeyNameCache) {
                try {
                    Field field = cls.getDeclaredField(name);
                    field.setAccessible(true);
                    Object obj = field.get(modelObject);
                    list.add(new FieldBean(field.getName(), obj));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new SqlPalException("读取字段失败！", e);
                }
            }
        }
        return list;
    }

    /**
     * 获取字段
     * @param modelObject model对象
     * @param primaryKeyFields 主键字段
     * @param notPrimaryKeyFields 非主键字段
     */
    public void getFields(Object modelObject, @NotNull List<FieldBean> primaryKeyFields, @NotNull List<FieldBean> notPrimaryKeyFields) throws SqlPalException {
        listFields(modelObject, (field, name, obj) -> {
            if (isPrimaryKeyField(field)) {
                primaryKeyFields.add(new FieldBean(name, obj));
            } else {
                notPrimaryKeyFields.add(new FieldBean(name, obj));
            }
        });
    }

    /**
     * 字段遍历接口
     */
    private interface FieldListListener {
        void onList(Field field, String name, Object obj);
    }
}
