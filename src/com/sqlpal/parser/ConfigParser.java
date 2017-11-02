package com.sqlpal.parser;

import com.sqlpal.bean.ConfigInfo;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 配置文件XML处理器
 */
public class ConfigParser extends DefaultHandler {
    private String username;
    private String password;
    private String host;
    private int port;
    private String dbname;
    private String encoding;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        switch (qName) {
            case "username": username = attributes.getValue("value"); break;
            case "password": password = attributes.getValue("value"); break;
            case "host": host = attributes.getValue("value"); break;
            case "port": port = Integer.parseInt(attributes.getValue("value")); break;
            case "dbname": dbname = attributes.getValue("value"); break;
            case "encoding": encoding = attributes.getValue("value"); break;
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    public ConfigInfo getConfig() {
        return new ConfigInfo(username, password, host, port, dbname, encoding);
    }
}
