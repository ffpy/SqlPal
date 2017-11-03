package com.sqlpal.exception;

public class SqlPalException extends Exception {

    public SqlPalException() {
    }

    public SqlPalException(String message) {
        super(message);
    }

    public SqlPalException(Throwable cause) {
        super(cause);
    }

    public SqlPalException(String message, Exception exception) {
        super(message + "详细信息：" + exception.getMessage(), exception.getCause());
    }
}
