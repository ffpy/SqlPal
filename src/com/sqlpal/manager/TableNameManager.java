package com.sqlpal.manager;

import com.sqlpal.exception.SqlPalException;
import com.sqlpal.annotation.TableName;

import java.util.HashMap;

/**
 * 表名管理器
 */
public class TableNameManager {
    private static HashMap<String, String> tableMap = new HashMap<>();

    /**
     * 获取Model类所映射的表名
     * @param modelClass Model类的Class
     * @return 表名
     */
    public static String getTableName(Class<?> modelClass) throws SqlPalException {
        String className = modelClass.getName();
        String tableName = tableMap.get(className);
        if (tableName == null) {
            TableName annotation = modelClass.getAnnotation(TableName.class);
            if (annotation != null) {
                tableName = annotation.name();
                tableMap.put(className, tableName);
            } else {
                throw new SqlPalException("获取表名失败，请为" + className + "添加TableName注解以指定表名！");
            }
        }
        return tableName;
    }
}
