package com.sqlpal.crud;

import com.sqlpal.bean.FieldBean;
import com.sqlpal.exception.DataSupportException;
import com.sqlpal.manager.ModelManager;
import com.sqlpal.util.SqlSentenceUtils;
import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.List;

class UpdateHandler extends BaseUpdateHandler {

    int update(@NotNull DataSupport model) throws DataSupportException {
        return handle(model);
    }

    void updateAll(@NotNull List<? extends DataSupport> models) throws DataSupportException {
        handleAll(models);
    }

    @Override
    protected String onCreateSql(DataSupport model) {
        return SqlSentenceUtils.update(model.getTableName(), getFields(1), getFields(0));
    }

    @Override
    protected boolean onInitFieldLists(DataSupport model, List<List<FieldBean>> fieldLists) throws DataSupportException {
        ArrayList<FieldBean> primaryKeyFields = new ArrayList<>();
        ArrayList<FieldBean> updatedFields = new ArrayList<>();
        ModelManager.getFields(model, primaryKeyFields, updatedFields);
        if (updatedFields.isEmpty()) return false;

        fieldLists.add(updatedFields);
        fieldLists.add(primaryKeyFields);
        return true;
    }
}
