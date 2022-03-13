package app.model;

import app.xml.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class Row {

    private Map<String, Object> values;

    public Row(Map<String, Object> values){
        this.values = values;
    }

    public Row(List<Node> nodes){
        Supplier<Map<String,Object>> supp = HashMap::new;
        this.values = nodes.stream()
                .collect(
                        supp,
                        (map, node) -> map.put(node.getName(), node.getText()),
                        Map::putAll
                );
    }

    public Map<String, Object> getValues(){
        return values;
    }

    public Object addValue(String column, Object value){
        Object oldValue = values.get(column);
        values.put(column, value);
        return oldValue != null ? oldValue : value;
    }

    @Override
    public boolean equals(Object o){
        if(o == null){
            return false;
        }
        if(!(o instanceof Row)){
            return false;
        }
        Row other = (Row) o;
        return Objects.equals(values, other.values);
    }

    @Override
    public String toString(){
        return "Row:" + values.toString();
    }

    @Override
    public int hashCode(){
        return values.hashCode();
    }

    public boolean containsColumn(Column column){
        return containsColumn(column.getName());
    }

    public boolean containsColumn(String name) {
        return values.containsKey(name);
    }

    public boolean containsColumns(List<Column> columns) {
        return columns.stream()
                .map(Column::getName)
                .allMatch(this::containsColumn);
    }

    public Object get(Column col){
        return values.get(col.getName());
    }

    public Row projectOnto(List<Column> columns){
        Map<String, Object> projectionValues = new HashMap<>();
        for(Column col : columns){
            if(containsColumn(col)){
                projectionValues.put(col.getName(), get(col));
            }
        }
        return new Row(projectionValues);
    }
}
