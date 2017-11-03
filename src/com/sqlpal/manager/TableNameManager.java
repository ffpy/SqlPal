package com.sqlpal.manager;

import com.sqlpal.bean.Configuration;
import com.sqlpal.crud.DataSupport;
import com.sqlpal.exception.ConfigurationException;
import com.sqlpal.annotation.TableName;

import java.util.HashMap;

/**
 * 表名管理器
 */
public class TableNameManager {
    private static HashMap<String, String> tableMap;

    /**
     * 初始化
     */
    public static void init() {
        tableMap = new HashMap<>();
        Configuration configuration = ConfigurationManager.getConfiguration();
        for (String className : configuration.getMapping()) {
            Class<?> cls = DataSupportClassManager.getClass(className);
            TableName annotation = cls.getAnnotation(TableName.class);
            if (annotation == null) {
                throw new ConfigurationException("请为" + className + "添加TableName注解以指定表名");
            }
            String tableName = annotation.name();
            tableMap.put(className, tableName);
        }
    }

    /**
     * 销毁
     */
    public static void destroy() {
        if (tableMap != null) {
            tableMap.clear();
            tableMap = null;
        }
    }

    /**
     * 获取Model类所映射的表名
     * @param modelClass Model类的Class
     * @return 表名
     */
    public static String getTableName(Class<? extends DataSupport> modelClass) throws ConfigurationException {
        String tableName = tableMap.get(modelClass.getName());
        if (tableName == null) {
            throw new ConfigurationException("找不到表名，请把" + modelClass.getName() + "添加到" +
                    ConfigurationManager.getConfigFilename() + "的mapping中");
        }
        return tableName;
    }
}
