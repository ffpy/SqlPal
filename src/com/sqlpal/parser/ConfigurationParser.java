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
            case "driver": configuration.setDriverName(value); break;
            case "url": configuration.setUrl(value); break;
            case "username": configuration.setUsername(value); break;
            case "password": configuration.setPassword(value); break;
            case "initSize": configuration.setInitSize(Integer.parseInt(value)); break;
            case "maxSize": configuration.setMaxSize(Integer.parseInt(value)); break;
            case "maxWait": configuration.setMaxWait(Integer.parseInt(value)); break;
            case "maxBatch": configuration.setMaxBatchCount(Integer.parseInt(value)); break;
            case "mapping": configuration.getMapping().add(value); break;
        }
    }

    public Configuration getConfig() {
        return configuration;
    }
}
