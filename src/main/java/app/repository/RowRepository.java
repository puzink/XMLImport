package app.repository;

import app.jdbc.RowDao;
import app.table.Column;
import app.table.Row;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TODO
 */
public class RowRepository {

    public final RowDao rowDao;

    public RowRepository(RowDao rowDao) {
        this.rowDao = rowDao;
    }

    /**
     * Вставляет уникальные строки в БД.
     * Уникальность строки определяется сравнением значений
     *          по уникальным столбцам(по null тоже сравниваются).
     * @param rows - строки для вставки
     * @param tableName - имя таблицы в БД
     * @param rowColumns - в таблицу будут вставляться значения строк только по этим столбцам
     * @param uniqueColumns - столбцы, по которым определяются дубли
     * @return кол-во вставленных строк
     */
    public int insertUniqueRows(List<Row> rows, List<Column> rowColumns,
                                List<Column> uniqueColumns, String tableName){
        List<Row> uniqueRows = removeDuplicates(rows, tableName, uniqueColumns);
        return insertRows(uniqueRows, rowColumns, tableName);
    }

    /**
     * Вставляет строки в БД.
     * @param rows - строки для вставки
     * @param tableName - имя таблицы в БД
     * @param rowColumns - в таблицу будут вставляться значения строк только по этим столбцам
     * @return кол-во вставленных строк
     */
    public int insertRows(List<Row> rows, List<Column> rowColumns, String tableName){
        return rowDao.insertRows(rows, rowColumns, tableName);
    }

    /**
     * Возвращает список строк, дубликатов которых нет в таблице и в самом списке.
     * Уникальность строки определяется сравнением значений
     *          по уникальным столбцам(по null тоже сравниваются).
     * @param rows - строки
     * @param tableName - имя таблицы
     * @param uniqueColumns - уникальные столбцы, по которым сравниваются строки
     * @return строки, уникальные по определённым столбцам
     */
    private List<Row> removeDuplicates(List<Row> rows, String tableName, List<Column> uniqueColumns) {
        if(uniqueColumns.isEmpty() || rows.isEmpty()){
            return rows;
        }

        List<Boolean> isRowDuplicateInTable =
                rowDao.hasDuplicateRow(rows, tableName, uniqueColumns);
        Set<Row> rowsToInsert = new HashSet<>();
        for(int i = 0; i<rows.size();++i){
            Row rowProjection = rows.get(i).projectOnto(uniqueColumns);
            if(!isRowDuplicateInTable.get(i)){
                rowsToInsert.add(rowProjection);
            }
        }

        return new ArrayList<>(rowsToInsert);
    }
}

