package com.sqlpal.crud;

import com.sqlpal.bean.FieldBean;
import com.sqlpal.exception.DataSupportException;
import com.sqlpal.manager.ModelManager;
import com.sqlpal.util.SqlUtils;
import com.sun.istack.internal.NotNull;

import java.util.List;

class SaveHandler extends BaseUpdateHandler {

    int save(@NotNull DataSupport model) throws DataSupportException {
        return handle(model);
    }

    void saveAll(@NotNull List<? extends DataSupport> models) throws DataSupportException {
        handleAll(models);
    }

    @Override
    protected String onCreateSql(DataSupport model) throws DataSupportException {
        return SqlUtils.insert(model.getTableName(), getFields(0));
    }

    @Override
    protected boolean onInitFieldLists(DataSupport model, List<List<FieldBean>> fieldLists) throws DataSupportException {
        List<FieldBean> fields = ModelManager.getAllFields(model);
        if (fields.isEmpty()) return false;
        fieldLists.add(fields);
        return true;
    }
}
