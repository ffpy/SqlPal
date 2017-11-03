package com.sqlpal.manager;

import com.sqlpal.bean.Configuration;
import com.sqlpal.exception.ConfigurationException;

import java.util.HashMap;

/**
 * DataSupport类管理器
 */
public class DataSupportClassManager {
    private static HashMap<String, Class<?>> classMap;

    /**
     * 初始化
     */
    public static void init() {
        classMap = new HashMap<>();
        Configuration configuration = ConfigurationManager.getConfiguration();
        for (String className : configuration.getMapping()) {
            try {
                Class<?> cls = Class.forName(className);
                classMap.put(className, cls);
            } catch (ClassNotFoundException e) {
                throw new ConfigurationException("找不到mapping中的" + className);
            }
        }
    }

    /**
     * 销毁清空
     */
    public static void destroy() {
        if (classMap != null) {
            classMap.clear();
            classMap = null;
        }
    }

    /**
     * 获取className对应的class
     */
    public static Class<?> getClass(String className) {
        return classMap.get(className);
    }
}
