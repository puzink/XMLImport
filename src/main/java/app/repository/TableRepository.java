package app.repository;

import app.jdbc.TableDao;
import app.table.Column;

import java.util.List;

public class TableRepository {

    private final TableDao tableDao;

    public TableRepository(TableDao tableDao) {
        this.tableDao = tableDao;
    }

    public List<Column> getTableColumns(String tableName){
        return tableDao.getTableColumns(tableName);
    }
}
