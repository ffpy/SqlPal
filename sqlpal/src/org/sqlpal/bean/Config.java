package org.sqlpal.bean;

import java.util.ArrayList;

/**
 * 配置信息
 */
public class Config {
    private String driverName;          // 驱动程序名
    private String url;                 // 连接地址
    private String username;            // 用户名
    private String password;            // 密码
    private int initSize = 1;           // 连接池初始数量
    private int maxSize = 10;           // 连接池最大数量
    private int maxWait = 30000;        // 最长等待时间
    private int maxBatchCount = 10000;  // 批处理最大数量
    private ArrayList<String> mapping = new ArrayList<>();  // mapping列表

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
        return maxWait;
    }

    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
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
