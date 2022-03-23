package app.jdbc;

import app.table.Column;
import app.table.Row;
import app.utils.DbUtils;
import app.utils.ThreadConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RowDaoImpl extends AbstractDao implements RowDao {

    public RowDaoImpl(ThreadConnectionPool connectionPool){
        super(connectionPool);
    }

    public int insertRows(List<Row> rows, List<Column> columns, String tableName) throws SQLException {
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

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try{
            conn = getConnection();
            preparedStatement = conn.prepareStatement(query.toString());
            for(int i = 0; i < rows.size();++i){
                for(int j = 0 ; j < columns.size();++j){
                    preparedStatement.setObject(
                            i * columns.size() + j + 1,
                            rows.get(i).get(columns.get(j))
                    );
                }
            }

            return preparedStatement.executeUpdate();
        } finally {
            DbUtils.close(preparedStatement);
        }
    }


    public List<Boolean> hasDuplicateRow(List<Row> rows, String tableName, List<Column> uniqueColumns)
            throws SQLException {

        StringBuilder query = new StringBuilder()
                .append("select case exists(select * from ").append(tableName).append(" where ");
        List<String> columnsComparisons = new ArrayList<>();
        for(Column uniqueCol : uniqueColumns){

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

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try{
            conn = getConnection();
            preparedStatement = conn.prepareStatement(generalQuery.toString());
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
        }finally {
            DbUtils.close(preparedStatement);
        }
    }
}
