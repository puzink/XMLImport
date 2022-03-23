package app.repository;

import app.jdbc.TableDao;
import app.table.Column;
import app.utils.ThreadConnectionPool;

import java.sql.SQLException;
import java.util.List;

public class TableRepositoryImpl extends AbstractRepository implements TableRepository {

    private final TableDao tableDao;

    public TableRepositoryImpl(ThreadConnectionPool connectionPool,
                               TableDao tableDao) {
        super(connectionPool);
        this.tableDao = tableDao;
    }

    public List<Column> getTableColumns(String tableName) throws SQLException {
        return tableDao.getTableColumns(tableName);
    }
}
