package app.xml;

import lombok.Getter;

//import java.lang.annotation.ElementType;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Tag {

    private String name;
    private List<Attribute> attributes;
    private TagType type;

    public Tag(String name, List<Attribute> attributes, TagType type) {
        this.name = name;
        this.attributes = attributes;
        this.type = type;
    }

    public String toString(){
        List<String> attributeStrings = attributes.stream()
                .map(Attribute::toString)
                .collect(Collectors.toList());

        return String.format("<%s %s>", name, String.join(" ",attributeStrings));
    }

}
