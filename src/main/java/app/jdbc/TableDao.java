package app.jdbc;

import app.table.Column;

import java.sql.SQLException;
import java.util.List;

public interface TableDao extends Dao {

    List<Column> getTableColumns(String tableName) throws SQLException;

}
