package app.xml;

import app.xml.exception.XmlTagParseException;

import java.util.List;

public interface XmlTagParser {

    Tag parseTag(String elem) throws XmlTagParseException;

    String parseName(String elem) throws XmlTagParseException;
    List<Attribute> parseAttributes(String str) throws XmlTagParseException;
    TagType parseType(String elem) throws XmlTagParseException;
}
