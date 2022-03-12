package app.model.dto;

import app.model.Column;
import app.model.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TableDto {

    private Table table;
    private List<Column> uniqueColumns;
    private List<Column> columnsForInsert;

    public TableDto(Table table, List<Column> uniqueColumns, List<Column> columnsForInsert) {
        this.table = table;
        this.uniqueColumns = uniqueColumns;
        this.columnsForInsert = columnsForInsert;
    }

}
