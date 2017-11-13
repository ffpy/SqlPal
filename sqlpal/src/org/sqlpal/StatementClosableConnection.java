package org.sqlpal;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 添加关闭Statement的接口
 */
public interface StatementClosableConnection extends Connection{
    void closeStatements() throws SQLException;
}
