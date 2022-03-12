package app.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
public class Column {

    private String name;
    private DataType type;

    public Column(String name) {
        this.name = name;
    }

    public Column(String name, DataType type){
        this.name = name;
        this.type = type;
    }

    @Override
    public int hashCode(){
        return name.hashCode();
    }

    public boolean equals(Object o){
        if(o == null){
            return false;
        }
        if(!(o instanceof Column)){
            return false;
        }
        Column other = (Column) o;

        return Objects.equals(other.name, name);
    }

}
