package app.xml;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Element {

    private String name;
    private List<Attribute> attributes;

    public Element(String str){
        //FIXME
        name = str;
        attributes = new ArrayList<>();
    }

    public Element(String name, List<Attribute> attributes){
        this.name = name;
        this.attributes = attributes;
    }

    public String toString(){
        return String.format("Name = %s, attrs = %s", name, attributes.toString());
    }



}
