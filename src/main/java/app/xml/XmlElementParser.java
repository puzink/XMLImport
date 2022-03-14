package app.xml;

import app.xml.exception.XmlElementParseException;

import java.util.List;

public interface XmlElementParser {

    Element parseElement(String elem) throws XmlElementParseException;

    String parseName(String elem) throws XmlElementParseException;
    List<Attribute> parseAttributes(String str) throws XmlElementParseException;
    ElementType parseType(String elem) throws XmlElementParseException;
}
