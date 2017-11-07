package com.sqlpal.crud;

import com.sqlpal.bean.ContentValue;
import com.sqlpal.manager.ModelManager;
import com.sqlpal.util.SqlUtils;
import com.sun.istack.internal.NotNull;

import java.sql.SQLException;
import java.util.List;

class SaveHandler extends BaseUpdateHandler {

    int save(@NotNull DataSupport model) throws SQLException {
        return handle(model, true);
    }

    void saveAll(@NotNull List<? extends DataSupport> models) throws SQLException {
        handleAll(models, true);
    }

    @Override
    protected String onCreateSql(DataSupport model) {
        return SqlUtils.insert(model.getTableName(), getFields(0));
    }

    @Override
    protected boolean onInitFieldLists(DataSupport model, List<List<ContentValue>> fieldLists) {
        List<ContentValue> fields = ModelManager.getAllFields(model);
        if (fields.isEmpty()) return false;
        fieldLists.add(fields);
        return true;
    }
}
