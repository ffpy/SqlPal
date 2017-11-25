package org.sqlpal.manager;

import org.sqlpal.exception.ConfigurationException;
import org.sqlpal.config.Config;

import java.util.HashMap;

/**
 * 类管理器
 */
public class ClassManager {
    private static HashMap<String, Class<?>> modelClassMap;     // Model类className对应的class

    /**
     * 初始化
     */
    public static void init() {
        // 初始化modelClassMap
        modelClassMap = new HashMap<>();
        Config config = ConfigManager.getConfig();
        for (String className : config.getMapping()) {
            try {
                Class<?> cls = Class.forName(className);
                modelClassMap.put(className, cls);
            } catch (ClassNotFoundException e) {
                throw new ConfigurationException("找不到mapping中的" + className);
            }
        }
    }

    /**
     * 销毁清空
     */
    public static void destroy() {
        if (modelClassMap != null) {
            modelClassMap.clear();
            modelClassMap = null;
        }
    }

    /**
     * 获取className对应的class
     */
    public static Class<?> getClass(String className) {
        return modelClassMap.get(className);
    }
}
