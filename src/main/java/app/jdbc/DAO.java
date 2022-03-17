package app.jdbc;

import app.table.Column;
import app.table.DataType;
import app.table.Row;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DAO {

    private final Connection conn;

    public DAO(String url, String user, String password) throws SQLException {
        conn = DriverManager.getConnection(url, user, password);
    }

    public DAO(Connection conn){
        this.conn = conn;
    }

    public int insertRows(List<Row> rows, List<Column> columns, String tableName){
        if(rows.isEmpty() || columns.isEmpty()){
            return 0;
        }

        StringBuilder query = new StringBuilder().append("insert into ").append(tableName);
        List<String> columnsNames= columns.stream()
                .map(Column::getName)
                .collect(Collectors.toList());
        String columnsToInsert = String.join(",",columnsNames);
        query.append("(").append(columnsToInsert).append(") values ");
        String rowValue = "(" + "?,".repeat(columns.size()-1) + "?)";
        query.append((rowValue + ",").repeat(rows.size() - 1)).append(rowValue);
        query.append(" on conflict do nothing");

        try(PreparedStatement preparedStatement = conn.prepareStatement(query.toString())){
            for(int i = 0; i < rows.size();++i){
                for(int j = 0 ; j < columns.size();++j){
                    preparedStatement.setObject(
                            i * columns.size() + j + 1,
                            rows.get(i).get(columns.get(j))
                    );
                }
            }

            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
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

    public List<Boolean> hasDuplicate(List<Row> rows, String tableName, List<Column> uniqueColumns) {

        StringBuilder query = new StringBuilder()
                .append("select case exists(select * from ").append(tableName).append(" where ");
        List<String> columnsComparisons = new ArrayList<>();
        for(Column uniqueCol : uniqueColumns){
//            StringBuilder columnComparison = new StringBuilder();
//            columnComparison.append("((").append("(").append(tableName).append(".").append(uniqueCol.getName())
//                    .append(" is null)::integer + (? is null)::integer = 2").append(")");
//            columnComparison.append(" or ");
//            columnComparison.append("( ").append(tableName).append(".").append(uniqueCol.getName()).append(" = ? ").append("))");

            String columnComparison = String.format("(((%s.%s is null)::integer + (? is null)::integer = 2) " +
                    " or " +
                    " (%s.%s = ?)) ",
                    tableName, uniqueCol.getName(),
                    tableName, uniqueCol.getName()
            );

            columnsComparisons.add(columnComparison);
        }
        query.append(String.join(" and ", columnsComparisons));
        query.append(") when True then True else False end\n");
        String stringQuery = query.toString();

        StringBuilder generalQuery = new StringBuilder();
        generalQuery.append(stringQuery).append("\n");
        for(int i = 1; i < rows.size();++i){
            generalQuery.append("union all\n");
            generalQuery.append(stringQuery).append("\n");
        }

        try(PreparedStatement preparedStatement = conn.prepareStatement(generalQuery.toString())){

            int uniqueColumnSize = uniqueColumns.size();
            for(int i = 0; i<rows.size();++i){
                for(int j = 0; j < uniqueColumns.size();++j){
                    Column col = uniqueColumns.get(j);
                    preparedStatement.setObject((i*uniqueColumnSize + j) * 2 + 1, rows.get(i).get(col));
                    preparedStatement.setObject((i*uniqueColumnSize + j) * 2 + 2, rows.get(i).get(col));
                }
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Boolean> result = new ArrayList<>();
            while(resultSet.next()){
                result.add(resultSet.getBoolean(1));
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
