package org.sqlpal.crud.factory;

import com.sun.istack.internal.NotNull;
import org.sqlpal.manager.ModelManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SQLServerSqlFactory extends SqlFactory {

    @Override
    public String find(@NotNull String tableName, @NotNull String[] columns, @NotNull String[] conditions,
                       @NotNull String[] orderBy, int limit, int offset) {
        if (limit > 0) {
            // limit
            List<String> columnList = columns == null ? new ArrayList<String>() : Arrays.asList(columns);
            if (columnList.isEmpty()) {
                columnList.add( "TOP " + limit + " *");
            } else {
                String firstColumn = columnList.get(0);
                firstColumn = "TOP " + limit + " " + firstColumn;
                columnList.set(0, firstColumn);
            }

            columns = new String[columnList.size()];
            columnList.toArray(columns);

            // offset
            if (offset > 0) {
                // from
                List<String> primaryKeyNames = ModelManager.getPrimaryKeyColumns(tableName);
                StringBuilder primaryColumns = new StringBuilder();
                for (String column : primaryKeyNames) {
                    primaryColumns.append(column).append(",");
                }
                if (primaryColumns.length() > 0) {
                    primaryColumns.deleteCharAt(primaryColumns.length() - 1);
                }
                if (primaryColumns.length() > 0) {
                    tableName = "(SELECT ROW_NUMBER() OVER (ORDER BY " + primaryColumns.toString() + ") AS RowNumber,* FROM " +
                            tableName + ") AS A";
                }

                // where
                List<String> conditionList = conditions == null ? new ArrayList<String>() : Arrays.asList(conditions);
                columnList.add("RowNumber > " + offset);

                conditions = new String[conditionList.size()];
                conditionList.toArray(conditions);
            }
        }
        return super.find(tableName, columns, conditions, orderBy, 0, 0);
    }
}
