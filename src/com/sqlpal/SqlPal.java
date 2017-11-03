package com.sqlpal;

import com.sqlpal.exception.ConfigurationException;
import com.sqlpal.exception.DataSupportException;
import com.sqlpal.manager.*;

public class SqlPal {

    /**
     * 初始化
     */
    public static void init() throws ConfigurationException, DataSupportException {
        ConfigurationManager.init();
        ConnectionManager.init();
        DataSupportClassManager.init();
        TableNameManager.init();
        FieldManager.init();

        DataSupportClassManager.destroy();
    }

    /**
     * 销毁
     */
    public static void destroy() throws DataSupportException {
        ConfigurationManager.destroy();
        ConnectionManager.destroy();
        TableNameManager.destroy();
        FieldManager.destroy();
    }
}
