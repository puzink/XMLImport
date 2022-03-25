package app.dao;

import app.table.Column;
import app.table.Row;
import app.utils.DbUtils;
import app.imports.transaction.ThreadConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Простая реализация {@link RowDao}
 */
public class RowDaoImpl extends AbstractDao implements RowDao {

    public RowDaoImpl(ThreadConnectionPool connectionPool){
        super(connectionPool);
    }

    /**
     * Вставляет строки по указанным столбцам в таблицу.
     * Если строку вставить нельзя: нарушает ограничение и т.д. -
     *      она пропускается(on conflict do nothing).
     * @param rows - строки
     * @param columns - столбцы, значения по которым будут вставлены
     * @param tableName - имя таблицы, в которую нужно вставить
     * @return кол-во вставленных строк
     * @throws SQLException - если произошла ошибка во время вставки строк
     */
    public int insertRowsAsPossible(List<Row> rows, List<Column> columns, String tableName) throws SQLException {
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


    /**
     * Проверяет наличие дубликатов строк в таблице по указанным столбцам.
     * У строки "row" есть дубликат, если есть такая строка "tableRow" в таблице,
     *      что для каждого столбца "col" из указанных справедливо следующее:
     *      (row.col = tableRow.col) and ((row.col is null) and (tableRow.col is null))
     *
     * <p>В СУБД отправляется 1 запрос вида:</p>
     * <p>select case exists(select * from :tableName where <b>сравнение по столбцам</b>)
     *      when True then True else False end
     *     union all
     *     <i>такой же запрос для следующей строки</i>
     * </p>
     *
     * @param rows - строки, у которых необъодимо проверить наличе дубликата
     * @param tableName - имя таблицы
     * @param uniqueColumns - столбцы, по которым будут сравниваться строки
     * @return список с результатом по каждой строке из полученных.
     *       <p>Результат равен true, если у строки есть дубликат, иначе - false.</p>
     * @throws SQLException - если произошла ошибка во время выполнения запроса
     */
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
