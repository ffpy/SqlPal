package org.sqlpal.manager;

import org.sqlpal.annotation.AutoIncrement;
import org.sqlpal.annotation.PrimaryKey;
import org.sqlpal.annotation.Table;
import org.sqlpal.config.Config;
import org.sqlpal.common.ModelField;
import org.sqlpal.exception.ConfigurationException;
import org.sqlpal.crud.DataSupport;
import com.sun.istack.internal.NotNull;
import org.sqlpal.util.EmptyUtils;

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
    private static ConcurrentHashMap<String, ArrayList<String>> primaryKeyColumnsMap;   // 表名对应的主键列
    private static ConcurrentHashMap<String, String> autoIncrementMap;                  // 模型类对应的自增字段
    private static ConcurrentHashMap<String, String> tableNameMap;                      // 模型类对应的表名

    /**
     * 初始化字段信息
     */
    public static void init() {
        primaryKeyColumnsMap = new ConcurrentHashMap<>();
        autoIncrementMap = new ConcurrentHashMap<>();
        tableNameMap = new ConcurrentHashMap<>();

        Config config = ConfigManager.getConfig();
        for (String className : config.getMapping()) {
            Class<?> cls = ClassManager.getClass(className);

            // 获取Model类的表名
            Table annotation = cls.getAnnotation(Table.class);
            if (annotation == null) {
                throw new ConfigurationException("请为" + className + "添加TableName注解以指定表名");
            }
            String tableName = annotation.name();
            if (EmptyUtils.isEmpty(tableName)) {
                throw new ConfigurationException(className + "的表名不能为空");
            }
            tableNameMap.put(className, tableName);

            // 获取Model类的主键字段
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
            primaryKeyColumnsMap.put(tableName, primaryKeyNames);
        }
    }

    /**
     * 销毁
     */
    public static void destroy() {
        if (primaryKeyColumnsMap != null) {
            primaryKeyColumnsMap.clear();
            primaryKeyColumnsMap = null;
        }
        if (autoIncrementMap != null) {
            autoIncrementMap.clear();
            autoIncrementMap = null;
        }
        if (tableNameMap != null) {
            tableNameMap.clear();
            tableNameMap = null;
        }
    }

    /**
     * 获取主键名列表
     * @param tableName 表名
     * @return 返回主键列表
     */
    public static ArrayList<String> getPrimaryKeyColumns(String tableName) {
        return primaryKeyColumnsMap.get(tableName);
    }

    /**
     * 获取主键名列表
     * @param modelClass 要获取的Model类的Class
     * @return 返回主键列表
     */
    public static ArrayList<String> getPrimaryKeyColumns(Class<? extends DataSupport> modelClass) {
        return primaryKeyColumnsMap.get(getTableName(modelClass));
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
    public static void getAllFields(@NotNull DataSupport model, @NotNull final List<ModelField> allFields) {
        allFields.clear();
        listNotNullFields(model, new FieldListCallback() {
            @Override
            public void onList(Field field, String name, Object obj) {
                allFields.add(new ModelField(name, obj));
            }
        });
    }

    /**
     * 获取主键字段
     * @param model 要读取的Model类
     * @param primaryKeyFields 在这里保存读取结果
     */
    public static void getPrimaryKeyFields(@NotNull DataSupport model, @NotNull List<ModelField> primaryKeyFields) {
        primaryKeyFields.clear();
        Class<? extends DataSupport> cls = model.getClass();
        for (String name : getPrimaryKeyColumns(cls)) {
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
    public static void getFields(@NotNull DataSupport model, @NotNull final List<ModelField> primaryKeyFields, @NotNull final List<ModelField> notPrimaryKeyFields) {
        primaryKeyFields.clear();
        notPrimaryKeyFields.clear();
        listNotNullFields(model, new FieldListCallback() {
            @Override
            public void onList(Field field, String name, Object obj) {
                if (isPrimaryKeyField(field)) {
                    primaryKeyFields.add(new ModelField(name, obj));
                } else {
                    notPrimaryKeyFields.add(new ModelField(name, obj));
                }
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
     * 获取Model类所映射的表名
     * @param modelClass Model类的Class
     * @return 返回表名
     * @throws ConfigurationException 初始化错误
     */
    public static String getTableName(Class<? extends DataSupport> modelClass) throws ConfigurationException {
        String tableName = tableNameMap.get(modelClass.getName());
        if (tableName == null) {
            throw new ConfigurationException("找不到表名，请把" + modelClass.getName() + "添加到" +
                    ConfigManager.getConfigFilename() + "的mapping中");
        }
        return tableName;
    }

    /**
     * 字段遍历接口
     */
    private interface FieldListCallback {
        void onList(Field field, String name, Object obj);
    }
}
