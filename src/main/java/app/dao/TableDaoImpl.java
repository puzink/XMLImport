package app.dao;

import app.table.Column;
import app.table.DataType;
import app.utils.DbUtils;
import app.imports.transaction.ThreadConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TableDaoImpl extends AbstractDao implements TableDao {

    public TableDaoImpl(ThreadConnectionPool connectionPool) {
        super(connectionPool);
    }

    public List<Column> getTableColumns(String tableName) throws SQLException {

        String query = "select column_name, data_type from information_schema.columns where table_name = ?";
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try{
            conn = getConnection();
            preparedStatement = conn.prepareStatement(query);
            List<Column> columns = new ArrayList<>();
            preparedStatement.setString(1, tableName);
            //TODO add RowMapper
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String colName = resultSet.getString("column_name");
                String strType = resultSet.getString("data_type");
                DataType type = DataType.getBySqlType(strType)
                        .orElseThrow(() -> new IllegalArgumentException("This data type is not supported: " + strType));
                columns.add(new Column(colName, type));
            }
            return columns;
        } finally {
            DbUtils.close(preparedStatement);
        }
    }

}
