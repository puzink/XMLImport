package app.model;

import app.xml.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class Row {

    private Map<String, String> values;

    public Row(Map<String, String> values){
        this.values = values;
    }

    public Row(List<Node> nodes){
        Supplier<Map<String,String>> supp = HashMap::new;
        this.values = nodes.stream()
                .collect(
                        supp,
                        (map, node) -> map.put(node.getName(), node.getText()),
                        Map::putAll
                );
    }

    public Map<String, String> getValues(){
        return values;
    }

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

    public String toString(){
        return "Row:" + values.toString();
    }
}
