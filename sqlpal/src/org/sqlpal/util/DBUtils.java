package org.sqlpal.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 数据库工具类
 */
public class DBUtils {

    public static void close(Statement stmt) {
        close(stmt, null);
    }

    public static void close(Statement stmt, ResultSet rs) {
        try {
            if (stmt != null) stmt.close();
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
