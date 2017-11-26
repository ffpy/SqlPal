package org.sqlpal.manager;

import org.sqlpal.config.Config;
import org.sqlpal.crud.factory.SQLServerSqlFactory;
import org.sqlpal.crud.factory.SqlFactory;

public class FactoryManager {
    private static SqlFactory sqlFactory;

    public static void init() {
        Config config = ConfigManager.getConfig();
        initSqlFactory(config);
    }

    private static void initSqlFactory(Config config) {
        String database = config.getDatabase();
        if (database != null) {
            database = database.toLowerCase();
            if (database.contains("access") || database.contains("sql server")) {
                sqlFactory = new SQLServerSqlFactory();
            }
        }
        if (sqlFactory == null) sqlFactory = new SqlFactory();
    }

    public static SqlFactory getSqlFactory() {
        return sqlFactory;
    }

    public static void destroy() {
        sqlFactory = null;
    }
}
