package com.sqlpal.parser;

import com.sqlpal.bean.Configuration;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 配置文件XML处理器
 */
public class ConfigurationParser extends DefaultHandler {
    private Configuration configuration = new Configuration();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        switch (qName) {
            case "driver": configuration.setDriverName(attributes.getValue("value")); break;
            case "username": configuration.setUsername(attributes.getValue("value")); break;
            case "password": configuration.setPassword(attributes.getValue("value")); break;
            case "host": configuration.setHost(attributes.getValue("value")); break;
            case "port": configuration.setPort(Integer.parseInt(attributes.getValue("value"))); break;
            case "dbname": configuration.setDbname(attributes.getValue("value")); break;
            case "encoding": configuration.setEncoding(attributes.getValue("value")); break;
            case "initSize": configuration.setInitSize(Integer.parseInt(attributes.getValue("value"))); break;
            case "maxSize": configuration.setMaxSize(Integer.parseInt(attributes.getValue("value"))); break;
            case "maxWati": configuration.setMaxWati(Integer.parseInt(attributes.getValue("value"))); break;
            case "mapping": configuration.getMapping().add(attributes.getValue("class")); break;
        }
    }

    public Configuration getConfig() {
        configuration.buildUrl();
        return configuration;
    }
}
