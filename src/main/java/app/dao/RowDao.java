package app.dao;

import app.table.Column;
import app.table.Row;

import java.sql.SQLException;
import java.util.List;

public interface RowDao extends Dao {

    int insertRows(List<Row> rows, List<Column> columns, String tableName) throws SQLException;
    List<Boolean> hasDuplicateRow(List<Row> rows, String tableName, List<Column> uniqueColumns) throws SQLException;

}
