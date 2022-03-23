package app.repository;

import app.table.Column;

import java.sql.SQLException;
import java.util.List;

public interface TableRepository extends Repository{
    List<Column> getTableColumns(String tableName) throws SQLException;
}
