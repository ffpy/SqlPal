package com.sqlpal;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 自动关闭Statement
 */
public interface AutoConnection extends Connection{
    void closeStatements() throws SQLException;
}
