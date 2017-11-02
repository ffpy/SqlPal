package com.sqlpal.bean;

public class ConfigInfo {
    private String username;        // 用户名
    private String password;        // 密码
    private String host;            // 主机名
    private int port;               // 端口号
    private String dbname;          // 数据库名
    private String encoding;        // 编码
    private String url;

    public ConfigInfo(String username, String password, String host, int port, String dbname, String encoding) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.dbname = dbname;
        this.encoding = encoding;
        buildUrl();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDbname() {
        return dbname;
    }

    public String getEncoding() {
        return encoding;
    }

    private void buildUrl() {
        url = "jdbc:mysql://" + host + ":" + port + "/" + dbname + "?&useUnicode=true&characterEncoding=" + encoding;
    }

    public String getUrl() {
        return url;
    }
}
