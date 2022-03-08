package app.xml;

import app.xml.exception.XmlTagParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class XmlTagParserImpl implements XmlTagParser {

    @Override
    public Tag parseTag(String str) throws XmlTagParseException {
        checkTagEmpty(str);
        String name = parseName(str);
        int attributeStart = str.indexOf(name) + name.length();
        List<Attribute> attributes = parseAttributes(str.substring(attributeStart));
        TagType type = parseType(str);
        return new Tag(name, attributes, type);
    }

    @Override
    public String parseName(String str) throws XmlTagParseException {
        checkTagEmpty(str);

        int nameStart = findFirstNotWhitespaceChar(str, 0);
        if(nameStart > 0){
            throw new XmlTagParseException("Whitespaces before tag name.");
        }
        if (hasSpecialSymbol(str, nameStart)) {
            nameStart = findFirstNotWhitespaceChar(str, nameStart + 1);
        }
        if(nameStart < 0){
            throw new XmlTagParseException("Tag name is not found.");
        }
        int nameEnd = findWhitespace(str, nameStart + 1);
        nameEnd = (nameEnd < 0) ? str.length() : nameEnd;

        String name = str.substring(nameStart, nameEnd).trim();
        validateName(name);

        return name;
    }



    @Override
    public List<Attribute> parseAttributes(String str) throws XmlTagParseException {
        List<Attribute> result = new ArrayList<>();

        int attributeKeyStart = findFirstNotWhitespaceChar(str,0);
        while(attributeKeyStart != -1){
            int equalSign = str.indexOf('=', attributeKeyStart);
            if(equalSign == -1){
                throw new XmlTagParseException("Attribute must has a value.");
            }

            int attrValueStart = str.indexOf("\"", equalSign + 1);
            if(attrValueStart == -1 && !str.substring(equalSign + 1).isBlank()){
                throw new XmlTagParseException("Attribute value must be enclosed in double quotes.");
            }
            if(attrValueStart == -1){
                throw new XmlTagParseException("Attribute must has a value.");
            }

            int attrValueEnd = str.indexOf("\"", attrValueStart + 1);
            if(attrValueEnd == -1){
                throw new XmlTagParseException("Attribute value is not closed.");
            }
            if(attrValueEnd + 1 < str.length() && !Character.isWhitespace(str.charAt(attrValueEnd + 1))){
                throw new XmlTagParseException("There must be a whitespace between attributes.");
            }

            String key = str.substring(attributeKeyStart, equalSign).trim();
            validateName(key);
            String value = str.substring(attrValueStart + 1, attrValueEnd);
            result.add(new Attribute(key, value));

            attributeKeyStart = findFirstNotWhitespaceChar(str, attrValueEnd + 1);
        }

        return result;
    }

    @Override
    public TagType parseType(String str) throws XmlTagParseException {
        checkTagEmpty(str);
        if(Character.isWhitespace(str.charAt(0))){
            throw new XmlTagParseException("Whitespaces before tag name.");
        }
        if(str.charAt(0) == '/'){
            return TagType.CLOSE;
        }
        return TagType.OPEN;
    }

    private boolean hasSpecialSymbol(String str, int nameStart) {
        return str.charAt(nameStart) == '/';
    }

    private void validateName(String name) throws XmlTagParseException {
        if(name.isBlank()){
            throw new XmlTagParseException("Name must not be empty.");
        }
        char firstLetter = name.charAt(0);
        if(!Character.isLetter(firstLetter)){
            throw new XmlTagParseException("Name must start with letter.");
        }
        for(int i = 1; i < name.length();++i){
            char c = name.charAt(i);
            if(!Character.isLetterOrDigit(c)){
                throw new XmlTagParseException("Name must contain digits and letters only.");
            }
        }
    }

//    private int findChar(Predicate<Character> predicate, int from, String str){
//        if(from >= str.length()){
//            return -1;
//        }
//        for(int i = from;i < str.length();++i){
//            if(predicate.test(str.charAt(i))){
//                return i;
//            }
//        }
//        return -1;
//    }


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

    private void checkTagEmpty(String str) throws XmlTagParseException {
        if(str.isBlank()){
            throw new XmlTagParseException("Tag must not be empty.");
        }
    }
}
