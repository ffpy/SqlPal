package com.sqlpal;

import com.sqlpal.bean.ConfigInfo;
import com.sqlpal.exception.SqlPalException;
import com.sqlpal.parser.ConfigParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlPal {
    private static final String CONFIG_FILENAME = "sqlpal.xml";    // 配置文件

    private static ConfigInfo config;
    private static Connection conn;

    /**
     * 初始化
     * @throws SqlPalException
     */
    public static void init() throws SqlPalException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SqlPalException("找不到驱动程序！", e);
        }
        initConfig();
    }

    private static void initConfig() throws SqlPalException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        ConfigParser handler = new ConfigParser();
        try {
            SAXParser parser = factory.newSAXParser();
            try {
                parser.parse(new File(CONFIG_FILENAME), handler);
                config = handler.getConfig();
            } catch (IOException e) {
                throw new SqlPalException("打开" + CONFIG_FILENAME + "失败，请确保把文件放在项目根目录！", e);
            } catch (SAXException e) {
                throw new SqlPalException("解析" + CONFIG_FILENAME + "出错！", e);
            }
        } catch (ParserConfigurationException | SAXException e) {
            throw new SqlPalException("创建SAX解析器失败!", e);
        }
    }

    /**
     * 获取配置信息
     */
    public static ConfigInfo getConfig() {
        return config;
    }

    /**
     * 开始事务
     * @throws SqlPalException
     */
    public static void beginTransaction() throws SqlPalException {
        if (config == null) throw new SqlPalException("没有初始化！");
        if (conn != null) return;
        try {
            conn = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
        } catch (SQLException e) {
            throw new SqlPalException("获取数据库连接失败！", e);
        }
    }

    /**
     * 结束事务
     * @throws SqlPalException
     */
    public static void endTransaction() throws SqlPalException {
        if (conn == null) return;
        try {
            conn.close();
        } catch (SQLException e) {
            throw new SqlPalException("关闭数据库连接出错！", e);
        }
        conn = null;
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection() {
        return conn;
    }
}
