package com.sqlpal;

import com.sqlpal.exception.ConnectionException;
import com.sqlpal.manager.*;

public class SqlPal {

    /**
     * 初始化
     */
    public static void init() {
        try {
            ConfigurationManager.init();
            ConnectionManager.init();
            DataSupportClassManager.init();
            TableNameManager.init();
            ModelManager.init();

            DataSupportClassManager.destroy();
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }

    /**
     * 销毁
     */
    public static void destroy() {
        try {
            ConfigurationManager.destroy();
            ConnectionManager.destroy();
            TableNameManager.destroy();
            ModelManager.destroy();
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }

    public static void begin() throws ConnectionException {
        ConnectionManager.requestConnection();
    }

    public static void end() {
        ConnectionManager.freeConnection();
    }
}
