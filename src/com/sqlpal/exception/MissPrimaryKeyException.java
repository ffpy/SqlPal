package com.sqlpal.exception;

public class MissPrimaryKeyException extends SqlPalException {

    public MissPrimaryKeyException() {
        super("找不到主键，请用PrimaryKey注解来指定主键！");
    }
}
