package com.sqlpal.exception;

/**
 * 数据操作异常
 */
public class DataSupportException extends SqlPalException {

    public DataSupportException() {
    }

    public DataSupportException(String message) {
        super(message);
    }

    public DataSupportException(Throwable cause) {
        super(cause);
    }

    public DataSupportException(String message, Exception exception) {
        super(message, exception);
    }
}
