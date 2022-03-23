package app.repository;

import app.table.Column;
import app.table.Row;

import java.sql.SQLException;
import java.util.List;

public interface RowRepository extends Repository {

    int insertUniqueRows(List<Row> rows, List<Column> rowColumns,
                         List<Column> uniqueColumns, String tableName) throws SQLException;

    int insertRows(List<Row> rows, List<Column> rowColumns, String tableName) throws SQLException;


}
