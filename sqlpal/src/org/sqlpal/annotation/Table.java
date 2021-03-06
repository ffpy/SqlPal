package org.sqlpal.annotation;

import java.lang.annotation.*;

/**
 * 设置Model类所映射的表名
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
    String name();
}
