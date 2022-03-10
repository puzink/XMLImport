package app.xml;

import lombok.Getter;

//import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class Tag {

    private String name;
    private List<Attribute> attributes = new ArrayList<>();
    private TagType type;

    public Tag(String name, List<Attribute> attributes, TagType type) {
        this.name = name;
        this.attributes = attributes;
        this.type = type;
    }

    public String toString() {
        List<String> attributeStrings = attributes.stream()
                .map(Attribute::toString)
                .collect(Collectors.toList());

        return String.format("<%s %s>", name, String.join(" ", attributeStrings));
    }

    public boolean equals(Object o) {
        if(o == null){
            return false;
        }
        if(o.getClass() != this.getClass()){
            return false;
        }
        Tag other = (Tag) o;
        if(Objects.equals(attributes, other.attributes)){
            return true;
        }
        if(other.attributes.size() != this.attributes.size()){
            return false;
        }
        for(int i = 0; i < attributes.size(); ++i){
            if(!Objects.equals(other.attributes.get(i), attributes.get(i))){
                return false;
            }
        }
        return Objects.equals(other.name, name)
                && type == other.type;
    }

}
