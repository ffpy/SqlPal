package com.sqlpal.manager;

import com.sqlpal.bean.Config;
import com.sqlpal.exception.ConfigurationException;

import java.util.HashMap;

/**
 * DataSupport类管理器
 */
public class DataSupportClassManager {
    private static HashMap<String, Class<?>> classMap;      // className对应的class映射

    /**
     * 初始化
     */
    public static void init() {
        classMap = new HashMap<>();
        Config config = ConfigurationManager.getConfig();
        for (String className : config.getMapping()) {
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
