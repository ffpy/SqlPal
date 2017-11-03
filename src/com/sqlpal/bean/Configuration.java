package com.sqlpal.bean;

import java.util.ArrayList;

public class Configuration {
    private String driverName;          // 驱动程序名
    private String username;            // 用户名
    private String password;            // 密码
    private String host;                // 主机名
    private int port;                   // 端口号
    private String dbname;              // 数据库名
    private String encoding;            // 编码
    private int initSize;               // 连接池初始数量
    private int maxSize;                // 连接池最大数量
    private int maxWati;                // 最长等待时间
    private ArrayList<String> mapping = new ArrayList<>();  // mapping列表
    private String url;

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public int getInitSize() {
        return initSize;
    }

    public void setInitSize(int initSize) {
        this.initSize = initSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getMaxWait() {
        return maxWati;
    }

    public void setMaxWati(int maxWati) {
        this.maxWati = maxWati;
    }

    public ArrayList<String> getMapping() {
        return mapping;
    }

    public void setMapping(ArrayList<String> mapping) {
        this.mapping = mapping;
    }

    public void buildUrl() {
        url = "jdbc:mysql://" + host + ":" + port + "/" + dbname + "?&useUnicode=true&characterEncoding=" + encoding;
    }

    public String getUrl() {
        return url;
    }
}
