package app.xml;

import java.util.List;

public interface XmlElementParser {

    Element parseElement(String elem);

    String parseName(String elem);
    List<Attribute> parseAttributes(String elem);
    ElementType parseType(String elem);
}
