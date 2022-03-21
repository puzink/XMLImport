package app.jdbc;

import app.table.Column;
import app.table.DataType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TableDao {

    private final Connection conn;

    public TableDao(String url, String user, String password) throws SQLException {
        conn = DriverManager.getConnection(url, user, password);
    }

    public TableDao(Connection conn){
        this.conn = conn;
    }

    public List<Column> getTableColumns(String tableName) {

        String query = "select column_name, data_type from information_schema.columns where table_name = ?";
        try(PreparedStatement preparedStatement = conn.prepareStatement(query)){
            List<Column> columns = new ArrayList<>();
            preparedStatement.setString(1, tableName);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String colName = resultSet.getString("column_name");
                String strType = resultSet.getString("data_type");
                DataType type = DataType.getBySqlType(strType)
                        .orElseThrow(() -> new IllegalArgumentException("This data type is not supported: " + strType));
                columns.add(new Column(colName, type));
            }
            return columns;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
