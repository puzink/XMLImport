package app.xml.exception;

import app.xml.CursorPosition;

public class XmlElementParseException extends XmlParseException{

    //TODO добавить курсор
    public XmlElementParseException(String message) {
        super(message);
    }

    public XmlElementParseException(String message, CursorPosition cursorPosition) {
        super(message, cursorPosition);
    }
}
