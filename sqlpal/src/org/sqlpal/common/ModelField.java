package org.sqlpal.common;

/**
 * Model类字段属性
 */
public class ModelField {
    private String name;    // 字段名
    private Object value;   // 字段值

    public ModelField() {
    }

    public ModelField(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
