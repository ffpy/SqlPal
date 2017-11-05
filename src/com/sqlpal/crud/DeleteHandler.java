package com.sqlpal.crud;

import com.sqlpal.bean.FieldBean;
import com.sqlpal.exception.DataSupportException;
import com.sqlpal.manager.ModelManager;
import com.sqlpal.util.SqlSentenceUtils;
import com.sun.istack.internal.NotNull;

import java.util.List;

class DeleteHandler extends BaseUpdateHandler {

    int delete(DataSupport model) throws DataSupportException {
        return handle(model);
    }

    void deleteAll(@NotNull List<? extends DataSupport> models) throws DataSupportException {
        handleAll(models);
    }

    @Override
    protected String onCreateSql(DataSupport model) throws DataSupportException {
        return SqlSentenceUtils.delete(model.getTableName(), getFields(0));
    }

    @Override
    protected boolean onInitFieldLists(DataSupport model, List<List<FieldBean>> fieldLists) throws DataSupportException {
        List<FieldBean> fields = ModelManager.getPrimaryKeyFields(model);
        fieldLists.add(fields);
        return true;
    }
}
