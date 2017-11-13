package com.sqlpal.manager;

import com.sqlpal.annotation.AutoIncrement;
import com.sqlpal.annotation.PrimaryKey;
import com.sqlpal.bean.Config;
import com.sqlpal.bean.ModelField;
import com.sqlpal.crud.DataSupport;
import com.sqlpal.exception.ConfigurationException;
import com.sun.istack.internal.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模型管理器
 */
public class ModelManager {
    private static ConcurrentHashMap<String, ArrayList<String>> primaryKeyNamesMap;     // 模型类对应的主键
    private static ConcurrentHashMap<String, String> autoIncrementMap;                  // 模型类对应的自增字段

    /**
     * 初始化字段信息
     */
    public static void init() {
        primaryKeyNamesMap = new ConcurrentHashMap<>();
        autoIncrementMap = new ConcurrentHashMap<>();

        Config config = ConfigurationManager.getConfig();
        for (String className : config.getMapping()) {
            Class<?> cls = ClassManager.getClass(className);
            ArrayList<String> primaryKeyNames = new ArrayList<>();
            Field[] fields = cls.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                if (!isSupportedField(field)) continue;
                if (!isPrimaryKeyField(field)) continue;
                // 主键
                primaryKeyNames.add(field.getName());

                // 自增字段
                if (isAutoIncrement(field)) {
                    autoIncrementMap.put(className, field.getName());
                }
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
        if (autoIncrementMap != null) {
            autoIncrementMap.clear();
            autoIncrementMap = null;
        }
    }

    /**
     * 获取主键列表
     * @param modelClass 要获取的Model类的Class
     * @return 返回主键列表
     */
    public static ArrayList<String> getPrimaryKeyNames(Class<? extends DataSupport> modelClass) {
        return primaryKeyNamesMap.get(modelClass.getName());
    }

    /**
     * 检查字段类型是不是支持的类型
     * @param field 字段
     * @return 支持返回true，否则返回false
     */
    private static boolean isSupportedField(Field field) {
        return field.getModifiers() == Modifier.PRIVATE;
    }

    /**
     * 判断是不是主键字段
     * @param field 字段
     * @return 是返回true，不是返回false
     */
    private static boolean isPrimaryKeyField(Field field) {
        return field.getAnnotation(PrimaryKey.class) != null;
    }

    /**
     * 判断是不是自增字段
     * @param field 字段
     * @return 是返回true，不是返回false
     */
    private static boolean isAutoIncrement(Field field) {
        return field.getAnnotation(AutoIncrement.class) != null;
    }

    /**
     * 遍历非null字段
     * @param model Model类实例
     * @param fieldListCallback 遍历回调
     */
    private static void listNotNullFields(@NotNull DataSupport model, @NotNull FieldListCallback fieldListCallback) {
        Field[] fields = model.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (!isSupportedField(field)) continue;
            try {
                Object obj = field.get(model);
                if (obj != null) {
                    fieldListCallback.onList(field, field.getName(), obj);
                }
            } catch (IllegalAccessException ignored) {
            }
        }
    }

    /**
     * 获取所有字段
     * @param model 要读取的Model类
     * @param allFields 在这里保存读取结果
     */
    public static void getAllFields(@NotNull DataSupport model, @NotNull List<ModelField> allFields) {
        allFields.clear();
        listNotNullFields(model, (field, name, obj) -> allFields.add(new ModelField(name, obj)));
    }

    /**
     * 获取主键字段
     * @param model 要读取的Model类
     * @param primaryKeyFields 在这里保存读取结果
     */
    public static void getPrimaryKeyFields(@NotNull DataSupport model, @NotNull List<ModelField> primaryKeyFields) {
        primaryKeyFields.clear();
        Class<? extends DataSupport> cls = model.getClass();
        for (String name : getPrimaryKeyNames(cls)) {
            try {
                Field field = cls.getDeclaredField(name);
                field.setAccessible(true);
                Object obj = field.get(model);
                if (obj == null) continue;
                primaryKeyFields.add(new ModelField(field.getName(), obj));
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
        }
    }

    /**
     * 获取字段
     * @param model model对象
     * @param primaryKeyFields 这里保存主键字段列表
     * @param notPrimaryKeyFields 这里保存非主键字段列表
     */
    public static void getFields(@NotNull DataSupport model, @NotNull List<ModelField> primaryKeyFields, @NotNull List<ModelField> notPrimaryKeyFields) {
        primaryKeyFields.clear();
        notPrimaryKeyFields.clear();
        listNotNullFields(model, (field, name, obj) -> {
            if (isPrimaryKeyField(field)) {
                primaryKeyFields.add(new ModelField(name, obj));
            } else {
                notPrimaryKeyFields.add(new ModelField(name, obj));
            }
        });
    }

    /**
     * 根据结果集实例化一个model
     * @param modelClass Model类的Class
     * @param rs 结果集
     */
    public static <T extends DataSupport> T instance(@NotNull Class<? extends DataSupport> modelClass, @NotNull ResultSet rs) {
        try {
            T model = (T) modelClass.newInstance();
            ModelManager.setFields(model, rs);
            return model;
        } catch (InstantiationException | IllegalAccessException ignored) {
        }
        return null;
    }

    /**
     * 设置字段值
     * @param model Model对象
     * @param rs 结果集
     */
    public static void setFields(@NotNull DataSupport model, @NotNull ResultSet rs) {
        Field[] fields = model.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (!isSupportedField(field)) continue;
            String columnName = field.getName();
            try {
                field.set(model, rs.getObject(columnName));
            } catch (SQLException | IllegalAccessException ignored) {
            }
        }
    }

    /**
     * 获取模型类的自增字段
     * @param modelClass Model类的Class
     * @return 返回自增字段名，null代表没有自增字段
     */
    public static String getAutoIncrement(@NotNull Class<? extends DataSupport> modelClass) {
        return autoIncrementMap.get(modelClass.getName());
    }

    /**
     * 字段遍历接口
     */
    private interface FieldListCallback {
        void onList(Field field, String name, Object obj);
    }
}
