package com.sqlpal.manager;

import com.sqlpal.annotation.PrimaryKey;
import com.sqlpal.bean.Configuration;
import com.sqlpal.bean.ContentValue;
import com.sqlpal.crud.DataSupport;
import com.sqlpal.exception.ConfigurationException;
import com.sqlpal.exception.DataSupportException;
import com.sqlpal.exception.PrimaryKeyNotFoundException;
import com.sun.istack.internal.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 模型管理器
 */
public class ModelManager {
    private static HashMap<String, ArrayList<String>> primaryKeyNamesMap;   // 主键名

    /**
     * 初始化字段信息
     */
    public static void init() {
        primaryKeyNamesMap = new HashMap<>();
        Configuration configuration = ConfigurationManager.getConfiguration();
        for (String className : configuration.getMapping()) {
            Class<?> cls = DataSupportClassManager.getClass(className);
            ArrayList<String> primaryKeyNames = new ArrayList<>();
            Field[] fields = cls.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                if (!isSupportedField(field)) continue;
                if (!isPrimaryKeyField(field)) continue;
                primaryKeyNames.add(field.getName());
            }

            if (primaryKeyNames.isEmpty()) {
                throw new ConfigurationException("找不到主键，请为" + className + "添加PrimaryKey注解以指定主键");
            }

            primaryKeyNamesMap.put(className, primaryKeyNames);
        }
    }

    /**
     * 销毁
     */
    public static void destroy() {
        if (primaryKeyNamesMap != null) {
            primaryKeyNamesMap.clear();
            primaryKeyNamesMap = null;
        }
    }

    /**
     * 获取主键列表
     */
    public static ArrayList<String> getPrimaryKeyNames(Class<? extends DataSupport> modelClass) {
        return primaryKeyNamesMap.get(modelClass.getName());
    }

    /**
     * 检查字段类型是不是支持的类型
     */
    private static boolean isSupportedField(Field field) {
        String[] supportedClassNames = {
                "java.lang.Integer", "java.lang.Short", "java.lang.Long",
                "java.lang.Float", "java.lang.Double", "java.lang.String",
                "java.util.Date"};
        if (field.getModifiers() != Modifier.PRIVATE) return false;
        String className = field.getGenericType().getTypeName();
        for (String name : supportedClassNames) {
            if (className.equals(name)) return true;
        }
        return false;
    }

    /**
     * 判断是不是主键字段
     */
    private static boolean isPrimaryKeyField(Field field) {
        return field.getAnnotation(PrimaryKey.class) != null;
    }

    /**
     * 遍历非null字段
     */
    private static void listNotNullFields(@NotNull DataSupport model, @NotNull FieldListCallback fieldListCallback) throws DataSupportException {
        Field[] fields = model.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (!isSupportedField(field)) continue;
            try {
                Object obj = field.get(model);
                if (obj != null) {
                    fieldListCallback.onList(field, field.getName(), obj);
                }
            } catch (IllegalAccessException e) {
                throw new DataSupportException("读取字段失败！", e);
            }
        }
    }

    /**
     * 获取所有字段
     */
    public static List<ContentValue> getAllFields(@NotNull DataSupport model) throws DataSupportException {
        ArrayList<ContentValue> list = new ArrayList<>();
        listNotNullFields(model, (field, name, obj) -> list.add(new ContentValue(name, obj)));
        return list;
    }

    /**
     * 获取主键字段
     */
    public static List<ContentValue> getPrimaryKeyFields(@NotNull DataSupport model) throws DataSupportException {
        ArrayList<ContentValue> list = new ArrayList<>();
        Class<? extends DataSupport> cls = model.getClass();
        for (String name : getPrimaryKeyNames(cls)) {
            try {
                Field field = cls.getDeclaredField(name);
                field.setAccessible(true);
                Object obj = field.get(model);
                list.add(new ContentValue(field.getName(), obj));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new DataSupportException("读取字段失败", e);
            }
        }
        if (list.isEmpty()) {
            throw new PrimaryKeyNotFoundException(model.getClass());
        }
        return list;
    }

    /**
     * 获取字段
     * @param model model对象
     * @param primaryKeyFields 主键字段
     * @param notPrimaryKeyFields 非主键字段
     */
    public static void getFields(@NotNull DataSupport model, @NotNull List<ContentValue> primaryKeyFields, @NotNull List<ContentValue> notPrimaryKeyFields) throws DataSupportException {
        listNotNullFields(model, (field, name, obj) -> {
            if (isPrimaryKeyField(field)) {
                primaryKeyFields.add(new ContentValue(name, obj));
            } else {
                notPrimaryKeyFields.add(new ContentValue(name, obj));
            }
        });
    }

    /**
     * 根据结果集实例化一个model
     */
    public static <T extends DataSupport> T instance(@NotNull Class<? extends DataSupport> modelClass, @NotNull ResultSet rs) throws DataSupportException {
        try {
            T model = (T) modelClass.newInstance();
            ModelManager.setFields(model, rs);
            return model;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new DataSupportException("实例化Model对象出错", e);
        }
    }

    /**
     * 设置字段值
     * @param model model对象
     * @param rs 结果集
     */
    public static void setFields(@NotNull DataSupport model, @NotNull ResultSet rs) throws DataSupportException {
        Field[] fields = model.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (!isSupportedField(field)) continue;
            String columnName = field.getName();
            try {
                Object value = null;
                switch (field.getGenericType().getTypeName()) {
                    case "java.lang.Integer": value = rs.getInt(columnName);break;
                    case "java.lang.Short": value = rs.getShort(columnName); break;
                    case "java.lang.Long": value = rs.getLong(columnName); break;
                    case "java.lang.Float": value = rs.getFloat(columnName); break;
                    case "java.lang.Double": value = rs.getDouble(columnName); break;
                    case "java.lang.String": value = rs.getString(columnName); break;
                    case "java.util.Date": value = rs.getDate(columnName); break;
                }

                field.set(model, value);
            } catch (SQLException ignored) {
            } catch (IllegalAccessException e) {
                throw new DataSupportException("设置字段值出错", e);
            }
        }
    }

    /**
     * 字段遍历接口
     */
    private interface FieldListCallback {
        void onList(Field field, String name, Object obj);
    }
}
