package com.sqlpal.manager;

import com.sqlpal.bean.Config;
import com.sqlpal.exception.ConfigurationException;
import com.sqlpal.parser.ConfigurationParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;

/**
 * 配置管理器
 */
public class ConfigurationManager {
    private static final String CONFIG_FILENAME = "sqlpal.xml";     // 配置文件名
    private static Config config;                                   // 配置信息

    /**
     * 初始化配置信息
     */
    public static void init() throws ConfigurationException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        ConfigurationParser handler = new ConfigurationParser();
        try {
            SAXParser parser = factory.newSAXParser();
            try {
                parser.parse(new File(CONFIG_FILENAME), handler);
                config = handler.getConfig();
            } catch (IOException e) {
                throw new ConfigurationException("打开" + CONFIG_FILENAME + "失败，请确保把文件放在项目根目录！", e);
            } catch (SAXException e) {
                throw new ConfigurationException("解析" + CONFIG_FILENAME + "出错！", e);
            }
        } catch (ParserConfigurationException | SAXException e) {
            throw new ConfigurationException("创建SAX解析器失败!", e);
        }
    }

    /**
     * 销毁
     */
    public static void destroy() {
        config = null;
    }

    /**
     * 获取配置信息
     * @return 返回配置信息
     */
    public static Config getConfig() throws ConfigurationException {
        if (config == null) {
            throw new ConfigurationException("没有初始化！");
        }
        return config;
    }

    /**
     * 获取配置文件名
     * @return 返回配置文件名
     */
    static String getConfigFilename() {
        return CONFIG_FILENAME;
    }
}
