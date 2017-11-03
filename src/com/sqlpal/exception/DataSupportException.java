package com.sqlpal.exception;

/**
 * 数据操作异常
 */
public class DataSupportException extends Exception {

    public DataSupportException() {
    }

    public DataSupportException(String message) {
        super(message);
    }

    public DataSupportException(Throwable cause) {
        super(cause);
    }

    public DataSupportException(String message, Throwable cause) {
        super(message, cause);
    }
}
