package org.sqlpal.config;

import java.util.ArrayList;

/**
 * 配置信息
 */
public class Config {
    private String database;            // 数据库
    private String driverName;          // 驱动程序名
    private String url;                 // 连接地址
    private String username;            // 用户名
    private String password;            // 密码
    private int maxBatchCount = 10000;  // 批处理最大数量
    private ArrayList<String> mapping = new ArrayList<>();  // mapping列表

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public int getMaxBatchCount() {
        return maxBatchCount;
    }

    public void setMaxBatchCount(int maxBatchCount) {
        this.maxBatchCount = maxBatchCount;
    }

    public ArrayList<String> getMapping() {
        return mapping;
    }

    public void setMapping(ArrayList<String> mapping) {
        this.mapping = mapping;
    }
}
