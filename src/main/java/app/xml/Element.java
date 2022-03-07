package app.xml;

import lombok.Getter;

//import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Element {

    private String name;
    private List<Attribute> attributes;
    private ElementType type = ElementType.OPEN;

    public Element(String str){
        parseString(str);
    }

    //TODO create element parser.
    private void parseString(String str){
        if(str.length() == 0 || str.isBlank()){
            throw new IllegalArgumentException("Element is empty.");
        }
        String name = findName(str);
        int attributesStart = findFirstNotWhitespaceChar(str,0) + name.length();

        ElementType type = defineElementType(name);
        if(type == ElementType.CLOSE){
            name = name.substring(1).trim();
        }
        if(name.isBlank()){
            throw new IllegalArgumentException("Wrong tag name.");
        }

        this.attributes = parseAttributes(str.substring(attributesStart));
        this.name = name;
        this.type = type;
    }

    private ElementType defineElementType(String name) {
        if(name.charAt(0) == '/'){
            return ElementType.CLOSE;
        }
        return ElementType.OPEN;
    }

    private String findName(String str) {
        int nameStart = findFirstNotWhitespaceChar(str, 0);
        if(nameStart > 0){
            throw new IllegalArgumentException("Whitespaces before tag name.");
        }
        if(nameStart < 0){
            throw new IllegalArgumentException("Wrong element name.");
        }
        int nameEnd = findWhitespace(str, nameStart + 1);
        nameEnd = (nameEnd < 0) ? str.length() : nameEnd;

        return str.substring(nameStart, nameEnd).trim();
    }

    private List<Attribute> parseAttributes(String str) {
        List<Attribute> result = new ArrayList<>();

        int attributeKeyStart = findFirstNotWhitespaceChar(str,0);
        while(attributeKeyStart != -1){
            int equalSign = str.indexOf('=', attributeKeyStart + 1);
            if(equalSign == -1){
                throw new IllegalArgumentException("Attribute must has a value.");
            }

            int attrValueStart = str.indexOf("\"", equalSign + 1);
            if(attrValueStart == -1 && !str.substring(equalSign + 1).isBlank()){
                throw new IllegalArgumentException("Attribute value must be enclosed in double quotes.");
            }
            if(attrValueStart == -1){
                throw new IllegalArgumentException("Attribute must has a value.");
            }

            int attrValueEnd = str.indexOf("\"", attrValueStart + 1);
            if(attrValueEnd == -1){
                throw new IllegalArgumentException("Attribute value is not closed.");
            }
            if(attrValueEnd + 1 < str.length() && !Character.isWhitespace(str.charAt(attrValueEnd + 1))){
                throw new IllegalArgumentException("There must be a whitespace between attributes.");
            }

            String key = str.substring(attributeKeyStart, equalSign);
            String value = str.substring(attrValueStart + 1, attrValueEnd);
            result.add(new Attribute(key, value));

            attributeKeyStart = findFirstNotWhitespaceChar(str, attrValueEnd + 1);
        }

        return result;
    }

    private int findFirstNotWhitespaceChar(String str, int from){
        if(from >= str.length()){
            return -1;
        }
        for(int i = from;i < str.length();++i){
            if(!Character.isWhitespace(str.charAt(i))){
                return i;
            }
        }
        return -1;
    }

    private int findWhitespace(String str, int from) {
        if(from >= str.length()){
            return -1;
        }
        for(int i = from; i < str.length();++i){
            if(Character.isWhitespace(str.charAt(i))){
                return i;
            }
        }
        return -1;
    }

    public String toString(){
        List<String> attributeStrings = attributes.stream()
                .map(Attribute::toString)
                .collect(Collectors.toList());

        return String.format("<%s %s>", name, String.join(" ",attributeStrings));
    }



}
