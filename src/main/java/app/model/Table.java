package app.model;

import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Table {

    private final String name;
    private final List<Column> columns;

    public Table(String name, List<Column> columns) {
        this.name = name;
        this.columns = columns;
    }

    public List<Column> getColumns(){
        return Collections.unmodifiableList(columns);
    }

    public boolean containsColumn(String columnName){
        return columns.stream().anyMatch(col -> col.getName().equals(columnName));
    }
}
