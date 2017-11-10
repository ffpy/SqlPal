package com.sqlpal.parser;

import com.sqlpal.bean.Config;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 配置文件XML处理器
 */
public class ConfigurationParser extends DefaultHandler {
    private Config config = new Config();
    private StringBuilder content = new StringBuilder();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        content.delete(0, content.length());
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        content.append(new String(ch, start, length));
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        String value = content.toString();
        switch (qName) {
            case "driver": config.setDriverName(value); break;
            case "url": config.setUrl(value); break;
            case "username": config.setUsername(value); break;
            case "password": config.setPassword(value); break;
            case "initSize": config.setInitSize(Integer.parseInt(value)); break;
            case "maxSize": config.setMaxSize(Integer.parseInt(value)); break;
            case "maxWait": config.setMaxWait(Integer.parseInt(value)); break;
            case "maxBatch": config.setMaxBatchCount(Integer.parseInt(value)); break;
            case "mapping": config.getMapping().add(value); break;
        }
    }

    public Config getConfig() {
        return config;
    }
}
