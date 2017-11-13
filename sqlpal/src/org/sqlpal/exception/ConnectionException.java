package org.sqlpal.exception;

import java.sql.SQLException;

/**
 * 连接异常
 */
public class ConnectionException extends SQLException {

    public ConnectionException() {
    }

    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionException(Throwable cause) {
        super(cause);
    }
}
