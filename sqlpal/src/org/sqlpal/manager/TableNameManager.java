package org.sqlpal.manager;

import org.sqlpal.exception.ConfigurationException;
import org.sqlpal.bean.Config;
import org.sqlpal.crud.DataSupport;
import org.sqlpal.annotation.Table;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 表名管理器
 */
public class TableNameManager {
    private static ConcurrentHashMap<String, String> tableMap;

    /**
     * 初始化
     */
    public static void init() {
        tableMap = new ConcurrentHashMap<>();
        Config config = ConfigurationManager.getConfig();
        for (String className : config.getMapping()) {
            Class<?> cls = ClassManager.getClass(className);
            Table annotation = cls.getAnnotation(Table.class);
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
     * @return 返回表名
     * @throws ConfigurationException 初始化错误
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
