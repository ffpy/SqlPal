package com.sqlpal.exception;

import com.sqlpal.crud.DataSupport;

/**
 * 找不到主键异常
 */
public class PrimaryKeyNotFoundException extends ConfigurationException {

    public PrimaryKeyNotFoundException(Class<? extends DataSupport> cls) {
        super("找不到主键，请用PrimaryKey注解来指定" + cls.getName() + "的主键！");
    }
}
