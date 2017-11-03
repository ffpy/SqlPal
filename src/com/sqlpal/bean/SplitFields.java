package com.sqlpal.bean;

import java.util.List;

public class SplitFields {
    private List<FieldBean> primaryKeyFields;
    private List<FieldBean> ordinaryFields;

    public SplitFields(List<FieldBean> primaryKeyFields, List<FieldBean> ordinaryFields) {
        this.primaryKeyFields = primaryKeyFields;
        this.ordinaryFields = ordinaryFields;
    }

    public List<FieldBean> getPrimaryKeyFields() {
        return primaryKeyFields;
    }

    public List<FieldBean> getOrdinaryFields() {
        return ordinaryFields;
    }
}
