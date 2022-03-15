package app.xml;

import lombok.Getter;

//import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * Xml-элемент. Элемент состоит из имени, списка атрибутов({@link Attribute})
 *  и типа элемента({@link ElementType}).
 */
@Getter
public class Element {

    private String name;
    private List<Attribute> attributes = new ArrayList<>();
    private ElementType type;

    public Element(String name, List<Attribute> attributes, ElementType type) {
        this.name = name;
        this.attributes = attributes;
        this.type = type;
    }

    public List<Attribute> getAttributesBy(Predicate<Attribute> predicate){
        return attributes.stream()
                .filter(predicate)
                .collect(Collectors.toList());
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
        Element other = (Element) o;
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

    public boolean isClose() {
        return type == ElementType.CLOSE;
    }

}
